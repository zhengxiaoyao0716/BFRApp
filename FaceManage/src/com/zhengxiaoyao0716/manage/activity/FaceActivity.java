package com.zhengxiaoyao0716.manage.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.ListObjectsRequest;
import com.baidubce.services.bos.model.ListObjectsResponse;
import com.zhengxiaoyao0716.manage.R;
import com.zhengxiaoyao0716.manage.net.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 脸谱展示.
 * Created by zhengxiaoyao0716 on 2015/12/29.
 */
public class FaceActivity extends Activity {
    private String member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faces);

        member = getIntent().getExtras().getString("member");
        loadFaces(member);
    }

    private int faceNum;
    private void loadFaces(final String member) {
        new Thread(new BaseBosThread(new BaseHandler(this) {
            @Override
            protected void doInHandleMessage(Message msg) {
                if (msg.obj == null) {
                    Toast.makeText(FaceActivity.this, R.string.loadFailed, Toast.LENGTH_LONG).show();
                    return;
                }

                //noinspection unchecked
                List<BosObjectSummary> contents = (List<BosObjectSummary>) msg.obj;
                contents.remove(0);
                LinearLayout facesLinearLayout = (LinearLayout) findViewById(R.id.facesLinearLayout);
                FacesGetter.INSTANCE.init(facesLinearLayout);
                int index = 0;
                for (BosObjectSummary objectSummary : contents) {
                    String imagePath = objectSummary.getKey();
                    ImageView imageView = (ImageView) facesLinearLayout.findViewById(getResources().getIdentifier("faceImageView" + index++, "id", getPackageName()));
                    imageView.setImageBitmap(makeTempBitmap(imagePath));
                    imageView.setTag(imagePath);
                    imageView.setOnClickListener(onFaceImageClick);
                    imageView.setOnLongClickListener(onFaceImageLongClick);
                    //添加到待获取图片任务中
                    FacesGetter.INSTANCE.addTask(imagePath);
                }
                //记录图片数量
                faceNum = index;
                //末尾变成添加
                if (faceNum < 9)
                {
                    ImageView imageView = (ImageView) facesLinearLayout.findViewById(getResources().getIdentifier("faceImageView" + faceNum, "id", getPackageName()));
                    imageView.setImageBitmap(makeTempBitmap("添加新的照片"));
                    imageView.setTag(null);
                    imageView.setOnClickListener(onBlankImageClick);
                    imageView.setOnLongClickListener(null);

                    //如果没有照片
                    if (faceNum < 1) Toast.makeText(FaceActivity.this, "请立即上传一张照片使这个成员生效！", Toast.LENGTH_LONG).show();
                    //上一次的末尾清零
                    else if (faceNum < 8)
                    {
                        imageView = (ImageView) facesLinearLayout.findViewById(getResources().getIdentifier("faceImageView" + (1 + faceNum), "id", getPackageName()));
                        imageView.setImageBitmap(null);
                        imageView.setTag(null);
                        imageView.setOnClickListener(null);
                        imageView.setOnLongClickListener(null);
                    }
                }
                FacesGetter.INSTANCE.start();
            }
        }) {
            @Override
            protected void doInRun(BosClient client, Message message)
            {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BosHelper.BUCKET_NAME);
                listObjectsRequest.setDelimiter("/");
                listObjectsRequest.setPrefix(member);
                ListObjectsResponse listing = client.listObjects(listObjectsRequest);

                message.obj = listing.getContents();
            }
        }).start();
    }
    private Bitmap makeTempBitmap(String imagePath) {
        int width = 100, height = 100;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);

        Paint paint = new Paint();
        paint.setTypeface(Typeface.create(getString(R.string.font), Typeface.BOLD));
        paint.setTextSize(16);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawText(imagePath.substring(1 + imagePath.indexOf("/", 6)), width / 2 - 45, height / 2, paint);

        return bitmap;
    }

    private View.OnClickListener onFaceImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateFace(v);
        }

        private void updateFace(final View v) {
            new AlertDialog.Builder(FaceActivity.this)
                    .setTitle((String) v.getTag())
                    .setMessage(R.string.updateFaceTip)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String facePath = (String) v.getTag();
                            captureAndUpload(facePath);
                        }
                    }).create().show();
        }
    };
    private View.OnLongClickListener onFaceImageLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            deleteFace(v);
            return true;
        }

        private void deleteFace(final View v)
        {
            new AlertDialog.Builder(FaceActivity.this)
                    .setTitle((String) v.getTag())
                    .setMessage(R.string.deleteFaceTip)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (faceNum < 2)
                            {
                                Toast.makeText(FaceActivity.this, R.string.lowFaceNumTip, Toast.LENGTH_LONG).show();
                                return;
                            }
                            new BaseBosThread(new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);

                                    //隐含faceNum--;
                                    if (msg.what == 1) loadFaces(member);
                                    else Toast.makeText(FaceActivity.this, R.string.failed, Toast.LENGTH_LONG).show();
                                }
                            }) {
                                @Override
                                protected void doInRun(BosClient client, Message message) {
                                    BosHelper.INSTANCE.getClient().deleteObject(BosHelper.BUCKET_NAME, (String) v.getTag());
                                    updatePerson();
                                }
                            }.start();
                        }
                    }).create().show();
        }
    };
    private View.OnClickListener onBlankImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addFace();
        }

        private void addFace() {
            new AlertDialog.Builder(FaceActivity.this)
                    .setMessage(R.string.addFaceTip)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.capture, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String facePath = member + "/face" + faceNum + ".jpg";
                            captureAndUpload(facePath);
                        }
                    }).create().show();
        }
    };

    private String resultFacePath;
    private void captureAndUpload(String facePath)
    {
        resultFacePath = facePath;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle extra = data.getExtras();
        if (extra == null)
        {
            Toast.makeText(FaceActivity.this, R.string.uploadFaceFailed, Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap bitmap = (Bitmap) extra.get("data");
        if (bitmap == null)
        {
            Toast.makeText(FaceActivity.this, R.string.uploadFaceFailed, Toast.LENGTH_LONG).show();
            return;
        }

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        final byte[] bytes = outputStream.toByteArray();
        new BaseBosThread(new BaseHandler(this) {
            @Override
            protected void doInHandleMessage(Message msg) {
                if (msg.what == 1) loadFaces(member);
                else Toast.makeText(FaceActivity.this, R.string.uploadFaceFailed, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected void doInRun(BosClient client, Message message) {
                BosHelper.INSTANCE.getClient().putObject(BosHelper.BUCKET_NAME, resultFacePath, bytes);
                if (!updatePerson())
                {
                    BosHelper.INSTANCE.getClient().deleteObject(BosHelper.BUCKET_NAME, resultFacePath);
                    message.what = -1;
                }
            }
        }.start();
    }

    private boolean updatePerson()
    {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BosHelper.BUCKET_NAME);
        listObjectsRequest.setDelimiter("/");
        listObjectsRequest.setPrefix(member);
        ListObjectsResponse listing = BosHelper.INSTANCE.getClient().listObjects(listObjectsRequest);

        List<BosObjectSummary> contents = listing.getContents();
        contents.remove(0);

        JSONArray faces = new JSONArray();
        for (BosObjectSummary objectSummary : contents) {
            try {
                faces.put(new JSONObject().put("bosPath", objectSummary.getBucketName()+ "/" + objectSummary.getKey()));
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return faceNum > 0 ?
                BfrHelper.manager.modifyPerson(member.substring(1 + member.indexOf("/"), member.length() - 1), faces)
                : BfrHelper.manager.createPerson(member.substring(1 + member.indexOf("/"), member.length() - 1), "XinManJing", faces);
    }
}