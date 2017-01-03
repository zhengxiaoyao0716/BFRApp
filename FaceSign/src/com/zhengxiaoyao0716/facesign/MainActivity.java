package com.zhengxiaoyao0716.facesign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;
import com.zhengxiaoyao0716.facesign.camera.ScanActivity;

/**
 * 主界面.
 * Created by zhengxiaoyao0716 on 2015/12/2.
 */
public class MainActivity extends Activity {
    private ImageView faceIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFaceIB();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        Bundle extras = data.getExtras();
        if (extras == null) return;
        byte[] bitmapBytes = (byte[]) extras.get("bitmapBytes");
        if (bitmapBytes == null) return;
        faceIB.setImageBitmap(BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length));
        Toast.makeText(this, getString(R.string.signedTip, extras.getString("personName")), Toast.LENGTH_LONG).show();
    }

    private boolean hasInit;
    //初始化预览图
    private void initFaceIB()
    {
        faceIB = (ImageView) findViewById(R.id.faceIB);
        faceIB.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (hasInit) return true;
                else hasInit = true;

                int width = faceIB.getWidth();
                int height = faceIB.getHeight();

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);

                Paint paint = new Paint();
                paint.setTypeface(Typeface.create(getString(R.string.font), Typeface.BOLD));
                paint.setTextSize(30);

                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.TRANSPARENT);
                canvas.drawText(getString(R.string.capture), width / 2 - 90, height / 2, paint);

                faceIB.setImageBitmap(bitmap);
                return true;
            }
        });
        faceIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }
}