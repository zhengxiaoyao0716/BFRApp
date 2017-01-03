package com.zhengxiaoyao0716.manage.net;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.zhengxiaoyao0716.manage.R;

/**
 * 检查网络连接状态.
 * Created by zhengxiaoyao0716 on 2016/1/1.
 */
public abstract class CheckNetwork {
    private Activity activity;
    public CheckNetwork(Activity activity) { this.activity = activity; }

    public void sureConnect()
    {
        if (!isNetConnected()) showNoNetworkDialog();
        else doAfterConnected();
    }
    protected abstract void doAfterConnected();

    public boolean isNetConnected()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    public void showNoNetworkDialog()
    {
        new AlertDialog.Builder(activity).setTitle(R.string.noNetwork)
                .setMessage(R.string.openNetwork)
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setPositiveButton(R.string.redo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sureConnect();
                    }
                }).create().show();
    }
}
