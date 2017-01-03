package com.zhengxiaoyao0716.facesign.camera;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.zhengxiaoyao0716.facesign.net.BFRConnect;
import org.json.JSONObject;

/**
 * 分析响应结果.
 * Created by zhengxiaoyao0716 on 2015/11/27.
 */
public class ScanHandler extends Handler {
    private ScanActivity activity;
    public ScanHandler(ScanActivity activity) { this.activity = activity; }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        BFRConnect.ResultBean resultBean = (BFRConnect.ResultBean) msg.obj;
        JSONObject response = resultBean.getResponseJO();

        if (activity == null || response == null) return;
        Toast.makeText(activity, response.toString(), Toast.LENGTH_LONG).show();
        if (response.optInt("confidence", 0) > 60)
        {
            Intent intent = activity.getIntent();
            intent.putExtra("personName", response.optString("personName"));
            intent.putExtra("bitmapBytes", resultBean.getBitmapBytes());
            activity.setResult(1, intent);
            activity.finish();
            activity = null;
        }
    }
}