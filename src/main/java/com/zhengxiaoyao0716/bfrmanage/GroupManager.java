package com.zhengxiaoyao0716.bfrmanage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 组管理.
 * Created by zhengxiaoyao0716 on 2015/12/5.
 */
class GroupManager {
    /**
     * 创建组.
     * <p>基本接口，用户向服务创建一个组。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @param groupName 用户指定的队列名称，允许大小写字母、数字，且长度不大于20个字符，工程内唯一
     * @throws IOException
     */
    static void create(String version, String appId, String groupName) throws IOException {
        try {
            HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/group", version, appId), "POST"),
                    new JSONObject().put("groupName", groupName)
            );
            System.out.println("Create " + groupName + " succeed.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Create " + groupName + " failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Create " + groupName + " failed!");
        }
    }

    /**
     * 删除组.
     * <p>基本接口，用户向服务删除一个组。服务限制：无法删除含有子用户的分组，需要先把分组的用户全部删除后才可以删除一个分组。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @param groupName 要删除的组的名字
     * @throws IOException
     */
    static void delete(String version, String appId, String groupName) throws IOException {
        try {
            HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/group/%s", version, appId, groupName), "DELETE")
            );
            System.out.println("Delete " + groupName + " succeed.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Delete " + groupName + " failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Delete " + groupName + " failed!");
        }
    }

    /**
     * 查询组（是否存在？）.
     * <p>基本接口，用户向服务查询一个组。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @param groupName 要查询的组的名字
     * @return 成功的话就是groupName
     * @throws IOException
     */
    static String query(String version, String appId, String groupName) throws IOException {
        String responseGroupName;
        try {
            JSONObject response = HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/group/%s", version, appId, groupName), "GET")
            );
            if (response == null) throw new IOException();
            responseGroupName = response.optString("groupName", "can't find groupName in response.");
            System.out.println("groupName = " + responseGroupName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Query failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Query failed!");
        }
        return responseGroupName;
    }

    /**
     * 列出所有组.
     * <p>基本接口，用户列出所有的组。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @return Group数组，用户名下的所有Group，Group参见创建组
     * @throws IOException
     */
    static JSONArray list(String version, String appId) throws IOException {
        JSONArray groupJA;
        try {
            JSONObject response = HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/group", version, appId), "GET")
            );
            if (response == null) throw new IOException();
            groupJA = response.optJSONArray("groups");
            System.out.println("group list:\n" + groupJA);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Search groups failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Search groups failed!");
        }
        return groupJA;
    }
}
