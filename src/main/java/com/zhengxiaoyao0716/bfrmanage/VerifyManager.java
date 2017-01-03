package com.zhengxiaoyao0716.bfrmanage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 验证管理.
 * Created by zhengxiaoyao0716 on 2015/12/5.
 */
class VerifyManager {
    /**
     * 验证某个图片是否属于某个用户.
     * <p>基本接口，用户向服务验证某张照片是否属于某个用户。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @param personName 成员的名称，允许大小写字母、数字，且长度不大于50个字符，工程内唯一
     * @param faceAssetSort 资源类型，bosPath/base64
     * @param facePathOrDate 图片地址/图片数据
     * @return { "personName":String, "confidence":Float }
     * @throws IOException
     */
    static JSONObject verify(String version, String appId, String personName, String faceAssetSort, String facePathOrDate) throws IOException {
        JSONObject result;
        try {
            result = HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/person/%s?verify", version, appId, personName), "POST"),
                    new JSONObject().put(faceAssetSort, facePathOrDate)
            );
            System.out.println("result:\n" + result);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Verify failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Verify failed!");
        }
        return result;
    }

    /**
     * 验证某个图片是否属于某个组.
     * <p>基本接口，用户向服务验证某张照片是否属于某个组。</p>
     * @param version 百度Api版本
     * @param appId appId.
     * @param groupName 用户指定的队列名称，允许大小写字母、数字，且长度不大于20个字符，工程内唯一
     * @param faceAssetSort 资源类型，bosPath/base64
     * @param facePathOrDate 图片地址/图片数据
     * @return { "personName":String, "confidence":Float }
     * @throws IOException
     */
    static JSONObject identify(String version, String appId, String groupName, String faceAssetSort, String facePathOrDate) throws IOException {
        JSONObject result;
        try {
            result = HttpUtil.connect(
                    HttpUtil.build(String.format("/v%s/app/%s/group/%s?identify", version, appId, groupName), "POST"),
                    new JSONObject().put(faceAssetSort, facePathOrDate)
            );
            System.out.println("result:\n" + result);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Identify failed!");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Identify failed!");
        }
        return result;
    }
}
