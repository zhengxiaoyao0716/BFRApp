package com.zhengxiaoyao0716.manage.net;

import android.os.Handler;
import android.os.Message;
import com.baidubce.BceClientException;
import com.baidubce.services.bos.BosClient;

/**
 * 与百度Bos交互.
 * Created by zhengxiaoyao0716 on 2015/12/25.
 */
public abstract class BaseBosThread extends Thread {
    private Handler handler;
    public BaseBosThread(Handler handler) {
        super();

        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        doInRun(BosHelper.INSTANCE.getClient(), handler);
    }
    protected void doInRun(BosClient client, Handler handler)
    {
        Message message = Message.obtain(handler);
        try {
            message.what = 1;
            doInRun(client, message);
        } catch (BceClientException e) {
            e.printStackTrace();
            message.what = -1;
        }
        message.sendToTarget();
    }
    protected abstract void doInRun(BosClient client, Message message);
}
