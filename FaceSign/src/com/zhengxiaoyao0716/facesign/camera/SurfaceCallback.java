package com.zhengxiaoyao0716.facesign.camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import com.zhengxiaoyao0716.facesign.net.BFRConnect;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 扫描预览回调.
 * Created by zhengxiaoyao0716 on 2015/11/27.
 */
@SuppressWarnings("deprecation")    //为了兼容5.0以下设备
public class SurfaceCallback implements SurfaceHolder.Callback {
    private ScanActivity activity;
    private Camera camera;
    private Timer timer;
    public void release()
    {
        timer.cancel();
        if (camera == null) return;
        camera.release();
        camera = null;
    }

    SurfaceCallback(ScanActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        camera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
            activity.setResult(0);
            activity.finish();
        }
        camera.startPreview();

        final ScanHandler scanHandler = new ScanHandler(activity);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        //上传照片数据
                        BFRConnect.sign(scanHandler, data);
                        camera.autoFocus(null);
                        camera.startPreview();
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
