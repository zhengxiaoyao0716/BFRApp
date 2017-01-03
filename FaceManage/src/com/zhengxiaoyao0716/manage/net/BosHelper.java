package com.zhengxiaoyao0716.manage.net;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;

/**
 * 方便管理Bos.
 * Created by zhengxiaoyao0716 on 2015/12/29.
 */
public enum BosHelper {
    INSTANCE();

    public static final String BUCKET_NAME = "xinmanjing";

    private BosClient client;
    BosHelper()
    {
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials("bb0427fc12c34940a11964b88bb38ca8", "d6a11592d8e64f40aa8e01371dc4b3b7"));   //您的AK/SK
        config.setEndpoint("http://bj.bcebos.com");    //传入Bucket所在区域域名
        client = new BosClient(config);
    }
    public BosClient getClient() { return client; }
}
