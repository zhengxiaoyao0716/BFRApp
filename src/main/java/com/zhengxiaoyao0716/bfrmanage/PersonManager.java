package com.zhengxiaoyao0716.bfrmanage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 成员管理.
 * Created by zhengxiaoyao0716 on 2015/12/5.
 */
class PersonManager {
    /**
     * 创建成员.
     * <p>基本接口，用户向服务创建一个成员。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @param personName 成员的名称，允许大小写字母、数字，且长度不大于50个字符，工程内唯一
     * @param groupName 成员所属的组
     * @param faces 成员的已知照片列表（应该是资源类型+图片地址/图片数据的字典的数组），长度可以为0。不提供视为提供空数组
     * @throws IOException
     */
    static void create(String version, String appId, String personName, String groupName, JSONArray faces) throws IOException {
        try {
            HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/person", version, appId), "POST"),
                    new JSONObject().put("personName", personName).put("groupName", groupName).put("faces", faces)
            );
            System.out.println("Create " + personName + " succeed.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Create " + personName + " failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Create " + personName + " failed!");
        }
    }

    /**
     * 删除成员.
     * <p>基本接口，用户向组删除一个成员。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @param personName 要删除的成员的名称【！工程内唯一】
     * @throws IOException
     */
    static void delete(String version, String appId, String personName) throws IOException {
        try {
            HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/person/%s", version, appId, personName), "DELETE")
            );
            System.out.println("Delete " + personName + " succeed.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Delete " + personName + " failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Delete " + personName + " failed!");
        }
    }

    /**
     * 修改成员.
     * <p>基本接口，用户向服务修改一个成员的信息。
     * //注1：只允许faces。如果需要对groupName修改，必须删除user并且新加user到新的groupName。
     * //注2：faces提供且置为空，相当于删除该Person名下所有Face。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @param personName 成员的名称，允许大小写字母、数字，且长度不大于50个字符，工程内唯一
     * <!-- @param groupName 成员所属的组 -->
     * @param faces 成员的已知照片列表（应该是资源类型+图片地址/图片数据的字典的数组），长度可以为0。不提供视为提供空数组
     * @throws IOException
     */
    static void modify(String version, String appId, String personName, /*String groupName, */JSONArray faces) throws IOException {
        try {
            HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/person/%s", version, appId, personName), "PUT"),
                    new JSONObject().put("faces", faces)
            );
            System.out.println("Modify " + personName + " succeed.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Modify " + personName + " failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Modify " + personName + " failed!");
        }
    }

    /**
     * 查询成员.
     * <p>基本接口，用户向服务查询一个成员。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @param personName 成员的名称，允许大小写字母、数字，且长度不大于50个字符，工程内唯一
     * @return 等同于创建成员
     * @throws IOException
     */
    static JSONObject query(String version, String appId, String personName) throws IOException {
        JSONObject responseJO;
        try {
            responseJO = HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/person/%s", version, appId, personName), "GET")
            );
            System.out.println("person:\n" + responseJO);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Query " + personName + " failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Query " + personName + " failed!");
        }
        return responseJO;
    }

    /**
     * 列出所有成员.
     * <p>基本接口，列出所有成员。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @return persons，为Person数组，Person详见创建成员
     * @throws IOException
     */
    static JSONArray list(String version, String appId) throws IOException {
        JSONArray personsJA;
        try {
            JSONObject response = HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/person", version, appId), "GET")
            );
            if (response == null) throw new IOException();
            personsJA = response.optJSONArray("persons");
            System.out.println("person list:\n" + personsJA);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Search persons failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Search persons failed!");
        }
        return personsJA;
    }

    /**
     * 按组列出所有成员.
     * <p>基本接口，列出某分组所有成员。</p>
     * @param version 百度Api版本
     * @param appId appId
     * @param groupName 要查询的Group
     * @return persons，为Person数组，Person详见创建成员
     * @throws IOException
     */
    static JSONArray listWhereGroup(String version, String appId, String groupName) throws IOException {
        JSONArray personsJA;
        try {
            JSONObject response = HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/person?groupName=%s", version, appId, groupName), "GET")
            );
            if (response == null) throw new IOException();
            personsJA = response.optJSONArray("persons");
            System.out.println("person list:\n" + personsJA);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Search persons in " + groupName + " failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Search persons in " + groupName + " failed!");
        }
        return personsJA;
    }
}
