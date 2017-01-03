package com.zhengxiaoyao0716.bfrmanage;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络工具类.
 * Created by zhengxiaoyao0716 on 2015/12/2.
 */
class HttpUtil {
    static final String host = "face.bj.baidubce.com";

    private static final DateTimeFormatter alternateIso8601DateFormat = ISODateTimeFormat.dateTimeNoMillis().withZone(DateTimeZone.UTC);
    /**
     * 构建HttpURLConnection.
     * @param uri 就是uri咯
     * @param httpMethod http方法
     * @return 构建好的HttpURLConnection
     * @throws IOException 构建失败
     */
    static HttpURLConnection build(String uri, String httpMethod) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://" + host + uri).openConnection();
        connection.setRequestProperty("accept-encoding", "gzip, deflate");
            /*
            签名生效UTC时间，格式为year-month-dayThour:minute:secondZ，例如：2015-04-27T08:23:49Z`，可选参数，默认值为当前时间。
            说明： 考虑到客户端可能存在时钟偏移，实际生效时间为{timestamp}-00:30:00，即允许30分钟的误差，且签名生效时间和HTTP请求的Date头域没有任何关联，无需保持一致。
            */
            String x_bce_date = alternateIso8601DateFormat.print(System.currentTimeMillis());
        connection.setRequestProperty("x-bce-date", x_bce_date);
        connection.setRequestProperty("connection", "keep-alive");
        connection.setRequestProperty("accept", "*/*");
        //connection.setRequestProperty("host", host);
        //connection.setRequestProperty("x-bce-request-id", "12345678-1234-1234-1234-123456789012");
        connection.setRequestProperty("content-type", "application/json");
            //Query String即URL中“？”后面的“key1 = valve1 & key2 = valve2 ”字符串
            int index = uri.indexOf("?");
            String queryString = null;
            if (index != -1)
            {
                queryString = uri.substring(index + 1);
                uri = uri.substring(0, index);
            }
        connection.setRequestProperty("authorization", AuthUtil.generate(x_bce_date, httpMethod, uri, queryString));
        connection.setRequestMethod(httpMethod);
        connection.setConnectTimeout(6000);
        connection.setReadTimeout(6000);
        return connection;
    }

    /**
     * 建立连接，并向输出流写入请求数据
     * @param connection 构建好的HttpURLConnection
     * @param content 要写入的Json数据
     * @return 响应数据
     * @throws IOException 读写失败
     * @throws JSONException Json解析失败
     */
    static JSONObject connect(HttpURLConnection connection, JSONObject content) throws IOException, JSONException {
        connection.setDoOutput(true);
        //auto
        //connection.connect();
        connection.getOutputStream().write(content.toString().getBytes());
        return connect(connection);
    }

    /**
     * 建立连接
     * @param connection 构建好的HttpURLConnection
     * @return 响应数据
     * @throws IOException 读写失败
     * @throws JSONException Json解析失败
     */
    static JSONObject connect(HttpURLConnection connection) throws IOException, JSONException {
        //auto
        //connection.connect();
        //auto
        //connection.disconnect();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK)
        {
            String response = readStream(connection.getInputStream());
            //System.out.println("response = " + response);
            if (response.length() == 0) return null;
            else return new JSONObject(response);
        }
        else
            throw new IOException(
                    "HTTP status error!" +
                            "\n        error code: " + responseCode +
                            "\n        error message: " + readStream(connection.getErrorStream())
            );
    }
    static String readStream(InputStream inputStream) throws IOException {
        int cacheLength;
        byte[] cacheBytes = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((cacheLength = inputStream.read(cacheBytes)) > 0 ) { outputStream.write(cacheBytes, 0, cacheLength); }
        outputStream.close();
        inputStream.close();
        return outputStream.toString();
    }
}
