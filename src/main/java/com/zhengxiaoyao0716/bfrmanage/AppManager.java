package com.zhengxiaoyao0716.bfrmanage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 工程管理.
 * Created by zhengxiaoyao0716 on 2015/12/2.
 */
class AppManager {
    /**
     * 创建工程.
     * <P>基本接口，用户向服务创建一个工程。一期暂定每个用户最多只能申请一个工程，且不可删除。</P>
     * @param version 百度Api版本
     * @return 创建的appId
     * @throws IOException
     */
    static String create(String version) throws IOException {
        String appId;
        try {
            JSONObject response = HttpUtil.connect(HttpUtil.build("/v" + version + "/app", "POST"));
            if (response == null) throw new IOException();
            appId = response.optString("appId", "Can't find appId in response");
            System.out.println("appId = " + appId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Create app failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Create app failed!");
        }
        return appId;
    }

    /**
     * 列出所有工程.
     * <p>基本接口，用户列出所有的工程。</p>
     * @param version 百度Api版本
     * @return appId数组，用户名下的所有工程id
     * @throws IOException
     */
    static JSONArray list(String version) throws IOException {
        JSONArray appJA;
        try {
            JSONObject response = HttpUtil.connect(HttpUtil.build("/v" + version + "/app", "GET"));
            if (response == null) throw new IOException();
            appJA = response.optJSONArray("apps");
            System.out.println("app list:\n" + appJA);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Search apps failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Search apps failed!");
        }
        return appJA;
    }
}
