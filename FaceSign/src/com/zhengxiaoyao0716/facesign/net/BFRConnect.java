package com.zhengxiaoyao0716.facesign.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import com.zhengxiaoyao0716.bfrmanage.BFRManager;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * 连接BFR服务.
 * Created by zhengxiaoyao0716 on 2015/12/6.
 */
public class BFRConnect {
    //private static final BFRManager manager = BFRManager.getManager("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "1", "xxxxx");
    private static final BFRManager manager = BFRManager.getManager("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "1", "xxxxx");

    public static void sign(final Handler handler, final byte[] data)
    {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Matrix matrix = new Matrix();
                matrix.setRotate(270);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] bitmapBytes = outputStream.toByteArray();

                JSONObject response = null;
                try {
                    response = manager.identify("XinManJing", "base64", Base64.encodeToString(bitmapBytes, Base64.DEFAULT));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ResultBean resultBean = new ResultBean();
                resultBean.setResponse(response);
                resultBean.setBitmapBytes(bitmapBytes);

                Message message = Message.obtain(handler);
                message.obj = resultBean;
                message.sendToTarget();
            }
        }.start();
    }
    public static class ResultBean
    {
        private JSONObject response;
        private byte[] bitmapBytes;

        public JSONObject getResponseJO() {
            return response;
        }

        public void setResponse(JSONObject response) {
            this.response = response;
        }

        public byte[] getBitmapBytes() {
            return bitmapBytes;
        }

        public void setBitmapBytes(byte[] bitmapBytes) {
            this.bitmapBytes = bitmapBytes;
        }
    }
}