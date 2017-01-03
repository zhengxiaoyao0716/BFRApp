package com.zhengxiaoyao0716.facesign.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

/**
 * 扫描人脸.
 * Created by zhengxiaoyao0716 on 2015/11/26.
 */
public class ScanActivity extends Activity {

    private SurfaceView preView;

    private SurfaceCallback surfaceCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout rootRL = new RelativeLayout(this);
        setContentView(rootRL);

        preView = new SurfaceView(this);
        rootRL.addView(preView);
        SurfaceHolder preViewHolder = preView.getHolder();

        surfaceCallback = new SurfaceCallback(this);
        preViewHolder.addCallback(surfaceCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();

        surfaceCallback.release();
    }
}