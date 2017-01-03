package com.zhengxiaoyao0716.bfrcli;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片工具类.
 * Created by zhengxiaoyao0716 on 2015/12/5.
 */
public class FaceUtil {
    /**
     * 读取图片路径构建JSONArray.
     * @param facePaths 图片bos路径，相对于xinmajing/faces/
     * @return [{"bosPath":"bucketName/images/student/temp001.jpg"}, ... ]
     */
    public static JSONArray faces(String...facePaths) {
        JSONArray faces = new JSONArray();
        for (String facePath : facePaths)
            try {
                faces.put(new JSONObject().put("bosPath", "xinmanjing/faces/" + facePath));
                //faces.put(new JSONObject().put("base64", toBase64(facePath)));
            } catch (JSONException e) {
                e.printStackTrace();
                new IOException(facePath + " load failed!").printStackTrace();
            }
        return faces;
    }
    /**
     * 读取图片数据转为Base64字符串.
     * @param facePath 图片存放路径
     * @return Base64字符串
     */
    public static String toBase64(String facePath) {
        String base64Str = null;
        try {
            base64Str = toBase64(ClassLoader.getSystemResourceAsStream(facePath));
            System.out.println(facePath + " : " + base64Str);
        } catch (IOException e) {
            e.printStackTrace();
            new IOException(facePath + " load failed!").printStackTrace();
        }
        return base64Str;
    }
    /**
     * 输入流转为Base64字符串.
     * @param inputStream 输入流
     * @return Base64字符串
     * @throws IOException
     */
    public static String toBase64(InputStream inputStream) throws IOException {
        int cacheLength;
        byte[] cacheBytes = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((cacheLength = inputStream.read(cacheBytes)) > 0 ) { outputStream.write(cacheBytes, 0, cacheLength); }
        outputStream.close();
        inputStream.close();
        return new BASE64Encoder().encode(outputStream.toByteArray());
    }
}
