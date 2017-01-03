package com.zhengxiaoyao0716.manage.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.BosObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取脸谱图片.
 * Created by zhengxiaoyao0716 on 2015/12/29.
 */
public enum FacesGetter {
    INSTANCE;

    private LinearLayout facesLinearLayout;
    private List<String> taskList;
    public void init(LinearLayout facesLinearLayout)
    {
        this.facesLinearLayout = facesLinearLayout;
        taskList = new ArrayList<String>();
    }
    public void addTask(String facePath) { taskList.add(facePath); }

    private Thread imageGetterThread;
    public void start()
    {
        imageGetterThread = new BaseBosThread(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                MsgObjBean msgObjBean = (MsgObjBean) msg.obj;
                if (msgObjBean.getData() == null)
                {
                    addTask(msgObjBean.getFacePath());
                    return;
                }

                byte[] data = msgObjBean.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                ImageView imageView = (ImageView) facesLinearLayout.findViewWithTag(msgObjBean.getFacePath());
                if (imageView == null) return;
                imageView.setImageBitmap(bitmap);
            }
        }) {
            @Override
            protected void doInRun(BosClient client, Handler handler) {
                while (taskList != null && taskList.size() != 0) {
                    super.doInRun(client, handler);
                }
            }

            @Override
            protected void doInRun(BosClient client, Message message) {
                MsgObjBean msgObjBean = new MsgObjBean();
                message.obj = msgObjBean;

                String facePath = taskList.remove(0);
                msgObjBean.setFacePath(facePath);

                BosObject object = client.getObject(BosHelper.BUCKET_NAME, facePath);
                InputStream objectContent = object.getObjectContent();

                int cacheLength;
                byte[] cacheBytes = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    while ((cacheLength = objectContent.read(cacheBytes)) > 0) {
                        outputStream.write(cacheBytes, 0, cacheLength);
                    }
                    outputStream.close();
                    objectContent.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                msgObjBean.setData(outputStream.toByteArray());
            }
        };
        imageGetterThread.start();
    }
    private static class MsgObjBean {
        private String facePath;
        private byte[] data;

        public String getFacePath() {
            return facePath;
        }

        public void setFacePath(String facePath) {
            this.facePath = facePath;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
}
