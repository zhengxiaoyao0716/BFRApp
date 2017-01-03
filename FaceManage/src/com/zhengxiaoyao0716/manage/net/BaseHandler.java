package com.zhengxiaoyao0716.manage.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.zhengxiaoyao0716.manage.R;

/**
 * 主要是添加一个Loading时显示的进度图标.
 * Created by zhengxiaoyao0716 on 2016/1/1.
 */
public abstract class BaseHandler extends Handler {
    private ProgressDialog progressDialog;
    public BaseHandler(Context context)
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(context.getString(R.string.waiting));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        doInHandleMessage(msg);
        progressDialog.dismiss();
        progressDialog = null;
    }
    protected abstract void doInHandleMessage(Message msg);
}
