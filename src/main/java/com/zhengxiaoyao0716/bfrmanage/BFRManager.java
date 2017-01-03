package com.zhengxiaoyao0716.bfrmanage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 集中管理的入口.
 * Created by zhengxiaoyao0716 on 2015/12/6.
 */
@SuppressWarnings("unused") //对外接口
public final class BFRManager {
    private static final BFRManager INSTANCE = new BFRManager();

    static String accessKeyId;
    private String apiVersion;
    private String appId;
    /**
     * 获取管理器实例.
     * @param apiVersion 百度人脸识别Api的版本
     * @param appId 以前创建的工程的appId
     * @return 管理器实例
     */
    public static BFRManager getManager(String accessKeyId, String apiVersion, String appId)
    {
        BFRManager.accessKeyId = accessKeyId;
        INSTANCE.apiVersion = apiVersion;
        INSTANCE.appId = appId;
        return INSTANCE;
    }
    /**
     * 获取管理器实例.
     * @param apiVersion 百度人脸识别Api的版本
     * @return 管理器实例
     */
    public static BFRManager getManager(String accessKeyId, String apiVersion)
    {
        BFRManager.accessKeyId = accessKeyId;
        INSTANCE.apiVersion = apiVersion;
        INSTANCE.appId = INSTANCE.createApp();
        return INSTANCE;
    }

    //工程操作
    /**
     * 创建工程.
     * <P>基本接口，用户向服务创建一个工程。一期暂定每个用户最多只能申请一个工程，且不可删除。</P>
     * @return 创建的appId
     */
    public String createApp()
    {
        String appId = null;
        try {
            appId = AppManager.create(apiVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appId;
    }
    /**
     * 列出所有工程.
     * <p>基本接口，用户列出所有的工程。</p>
     * @return appId数组，用户名下的所有工程id
     */
    public JSONArray listApp()
    {
        JSONArray appJA = null;
        try {
            appJA = AppManager.list(apiVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appJA;
    }

    //组操作
    /**
     * 创建组.
     * <p>基本接口，用户向服务创建一个组。</p>
     * @param groupName 用户指定的队列名称，允许大小写字母、数字，且长度不大于20个字符，工程内唯一
     * @return 成败
     */
    public boolean createGroup(String groupName)
    {
        boolean result = false;
        try {
            GroupManager.create(apiVersion, appId, groupName);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 删除组.
     * <p>基本接口，用户向服务删除一个组。服务限制：无法删除含有子用户的分组，需要先把分组的用户全部删除后才可以删除一个分组。</p>
     * @param groupName 要删除的组的名字
     * @return 成败
     */
    public boolean deleteGroup(String groupName)
    {
        boolean result = false;
        try {
            GroupManager.delete(apiVersion, appId, groupName);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 查询组（是否存在？）.
     * <p>基本接口，用户向服务查询一个组。</p>
     * @param groupName 要查询的组的名字
     * @return 成功的话就是groupName
     */
    public String queryGroup(String groupName)
    {
        String responseGroupName = null;
        try {
            responseGroupName = GroupManager.query(apiVersion, appId, groupName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseGroupName;
    }
    /**
     * 列出所有组.
     * <p>基本接口，用户列出所有的组。</p>
     * @return Group数组，用户名下的所有Group，Group参见创建组
     */
    public JSONArray listGroup()
    {
        JSONArray groupJA = null;
        try {
            groupJA = GroupManager.list(apiVersion, appId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return groupJA;
    }

    //成员操作
    /**
     * 创建成员.
     * <p>基本接口，用户向服务创建一个成员。</p>
     * @param personName 成员的名称，允许大小写字母、数字，且长度不大于50个字符，工程内唯一
     * @param groupName 成员所属的组
     * @param faces 成员的已知照片列表（应该是资源类型+图片地址/图片数据的字典的数组），长度可以为0。不提供视为提供空数组
     * @return 成败
     */
    public boolean createPerson(String personName, String groupName, JSONArray faces)
    {
        boolean result = false;
        try {
            PersonManager.create(apiVersion, appId, personName, groupName, faces);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 删除成员.
     * <p>基本接口，用户向组删除一个成员。</p>
     * @param personName 要删除的成员的名称【！工程内唯一】
     * @return 成败
     */
    public boolean deletePerson(String personName)
    {
        boolean result = false;
        try {
            PersonManager.delete(apiVersion, appId, personName);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 修改成员.
     * <p>基本接口，用户向服务修改一个成员的信息。
     * //注1：只允许faces。如果需要对groupName修改，必须删除user并且新加user到新的groupName。
     * //注2：faces提供且置为空，相当于删除该Person名下所有Face。</p>
     * @param personName 成员的名称，允许大小写字母、数字，且长度不大于50个字符，工程内唯一
     * <!-- @param groupName 成员所属的组 -->
     * @param faces 成员的已知照片列表（应该是资源类型+图片地址/图片数据的字典的数组），长度可以为0。不提供视为提供空数组
     * @return 成败
     */
    public boolean modifyPerson(String personName, /*String groupName, */JSONArray faces)
    {
        boolean result = false;
        try {
            PersonManager.modify(apiVersion, appId, personName, /*groupName, */faces);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 查询成员.
     * <p>基本接口，用户向服务查询一个成员。</p>
     * @param personName 成员的名称，允许大小写字母、数字，且长度不大于50个字符，工程内唯一
     * @return 等同于创建成员
     */
    public JSONObject queryPerson(String personName)
    {
        JSONObject personJO = null;
        try {
            personJO = PersonManager.query(apiVersion, appId, personName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return personJO;
    }
    /**
     * 列出所有成员.
     * <p>基本接口，列出所有成员。</p>
     * @return persons，为Person数组，Person详见创建成员
     */
    public JSONArray listPerson()
    {
        JSONArray personsJA = null;
        try {
            personsJA = PersonManager.list(apiVersion, appId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return personsJA;
    }
    /**
     * 按组列出所有成员.
     * <p>基本接口，列出某分组所有成员。</p>
     * @param groupName 要查询的Group
     * @return persons，为Person数组，Person详见创建成员
     */
    public JSONArray listPersonWhereGroup(String groupName)
    {
        JSONArray personsJA = null;
        try {
            personsJA = PersonManager.listWhereGroup(apiVersion, appId, groupName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return personsJA;
    }

    //验证操作
    /**
     * 验证某个图片是否属于某个用户.
     * <p>基本接口，用户向服务验证某张照片是否属于某个用户。</p>
     * @param personName 成员的名称，允许大小写字母、数字，且长度不大于50个字符，工程内唯一
     * @param faceAssetSort 资源类型，bosPath/base64
     * @param facePathOrDate 图片地址/图片数据
     * @return { "personName":String, "confidence":Float }
     */
    public JSONObject verify(String personName, String faceAssetSort, String facePathOrDate)
    {
        JSONObject result = null;
        try {
            result = VerifyManager.verify(apiVersion, appId, personName, faceAssetSort, facePathOrDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 验证某个图片是否属于某个组.
     * <p>基本接口，用户向服务验证某张照片是否属于某个组。</p>
     * @param groupName 用户指定的队列名称，允许大小写字母、数字，且长度不大于20个字符，工程内唯一
     * @param faceAssetSort 资源类型，bosPath/base64
     * @param facePathOrDate 图片地址/图片数据
     * @return { "personName":String, "confidence":Float }
     */
    public JSONObject identify(String groupName, String faceAssetSort, String facePathOrDate)
    {
        JSONObject result = null;
        try {
            result = VerifyManager.identify(apiVersion, appId, groupName, faceAssetSort, facePathOrDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
