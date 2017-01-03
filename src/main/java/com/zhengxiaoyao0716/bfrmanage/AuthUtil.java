package com.zhengxiaoyao0716.bfrmanage;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 授权工具类.
 * Created by zhengxiaoyao0716 on 2015/12/4.
 */
class AuthUtil {

    /**
     * 生成认证字符串.
     * <p>认证字符串的格式为bce-auth-v1/{accessKeyId}/{timestamp}/{expirationPeriodInSeconds}/{signedHeaders}/{signature}。</p>
     * @param x_bce_date 就是x_bce_date咯
     * @param httpMethod 指HTTP协议中定义的GET、PUT、POST等请求，必须使用全大写的形式
     * @param uri 百度Api里这么说的：...URL中的绝对路径...要求绝对路径必须以“/”开头，不以“/”开头的需要补充上，空路径为“/”
     * @param queryString URL中的Query String（Query String即URL中“？”后面的“key1 = valve1 & key2 = valve2 ”字符串）【BUT】目前人脸识别Api里只有"verify"、"identify"两种
     * @return 认证字符串authorization
     */
    static String generate(String x_bce_date, String httpMethod, String uri, String queryString) {
        /*
        bce-auth-v1/{accessKeyId}/{timestamp}/{expirationPeriodInSeconds}
        //accessKeyId：生成签名所使用的Access Key ID，关于Access Key ID的获取方法，请参看获取AK/SK。
        //timestamp：签名生效UTC时间，格式为year-month-dayThour:minute:secondZ，例如：2015-04-27T08:23:49Z`，可选参数，默认值为当前时间。说明： 考虑到客户端可能存在时钟偏移，实际生效时间为{timestamp}-00:30:00，即允许30分钟的误差，且签名生效时间和HTTP请求的Date头域没有任何关联，无需保持一致。
        //expirationPeriodInSeconds：签名有效期限，从timestamp所指定的时间开始计算，时间为秒，默认值为1800秒（30）分钟。
         */
        String authStringPrefix  = String.format("bce-auth-v1/%s/%s/1800", BFRManager.accessKeyId, x_bce_date);
        /*
        生成认证字符串
        认证字符串的格式为bce-auth-v1/{accessKeyId}/{timestamp}/{expirationPeriodInSeconds}/{signedHeaders}/{signature}。
        //accessKeyId：生成签名所使用的Access Key ID，关于Access Key ID的获取方法，请参看获取AK/SK。
        //timestamp：签名生效UTC时间，格式为year-month-dayThour:minute:secondZ，例如：2015-04-27T08:23:49Z`，可选参数，默认值为当前时间。
            说明： 考虑到客户端可能存在时钟偏移，实际生效时间为{timestamp}-00:30:00，即允许30分钟的误差，且签名生效时间和HTTP请求的Date头域没有任何关联，无需保持一致。
        //expirationPeriodInSeconds：签名有效期限，从timestamp所指定的时间开始计算，时间为秒，默认值为1800秒（30）分钟。
        //signedHeaders：签名算法中涉及到的HTTP头域列表。HTTP头域名字一律要求小写且头域名字之间用分号（;）分隔，如host;range;x-bce-date。列表按照字典序排列。当signedHeaders为空时表示取默认值。
        //signature：256位签名的十六进制表示，由64个小写字母组成，关于Signature的详细介绍，请参看生成最终签名Signature。
         */
        String authorization;
        try {
            authorization = authStringPrefix + "/host/" + signature(authStringPrefix, httpMethod, uri, queryString);
            //System.out.println("authorization = " + authorization);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new IllegalStateException("Generate authorization failed!");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IllegalStateException("Generate authorization failed!");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Generate authorization failed!");
        }
        return authorization;
    }
    /**
     * 生成最终签名Signature.
     * <p>Signature =HMAC-SHA256-HEX(SigningKey, CanonicalRequest)</p>
     * @param authStringPrefix bce-auth-v1/{accessKeyId}/{timestamp}/{expirationPeriodInSeconds}
     * @param httpMethod 指HTTP协议中定义的GET、PUT、POST等请求，必须使用全大写的形式
     * @param uri 百度Api里这么说的：...URL中的绝对路径...要求绝对路径必须以“/”开头，不以“/”开头的需要补充上，空路径为“/”
     * @param queryString URL中的Query String（Query String即URL中“？”后面的“key1 = valve1 & key2 = valve2 ”字符串）【BUT】目前人脸识别Api里只有"verify"、"identify"两种
     * @return 最终签名Signature
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static String signature(String authStringPrefix, String httpMethod, String uri, String queryString) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        /*
        Signature =HMAC-SHA256-HEX(SigningKey, CanonicalRequest)
        有关函数HMAC-SHA256-HEX的详细介绍，请参看函数介绍。
        //SigningKey = *************************************。
        //CanonicalRequest = HTTP Method + "\n" + CanonicalURI + "\n" + CanonicalQueryString + "\n" + CanonicalHeaders，有关各参数的详细介绍，请参看生成CanonicalRequest
        */
        return HMAC_SHA256_HEX(signingKey(authStringPrefix), canonicalRequest(httpMethod, uri, queryString));
    }

    /**
     * 取得经过加密的数据.
     * @param authStringPrefix bce-auth-v1/{accessKeyId}/{timestamp}/{expirationPeriodInSeconds}
     * @return 加密后的数据
     */
    private static String signingKey(String authStringPrefix) {
        String signingKey = null;
        try {
            signingKey = HMAC_SHA256_HEX("d6a11592d8e64f40aa8e01371dc4b3b7", authStringPrefix);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        /*
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://123.57.72.138:6000/bce/get_sign").openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(6000);
            connection.setDoOutput(true);
            connection.getOutputStream().write(String.format("accessKeyId=%s&authStringPrefix=%s", BFRManager.accessKeyId, authStringPrefix).getBytes("UTF-8"));
            signingKey = HttpUtil.readStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        return signingKey;
    }
    /**
     * 实现HMAC-SHA256-HEX(key, message)函数.
     * <p>调用HMAC SHA256算法，根据提供的密钥（key）和密文（message）输出密文摘要，并把结果转换为小些形式的十六进制字符串.</p>
     * @param key 密钥
     * @param message 密文
     * @return 小些形式的十六进制字符串
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static String HMAC_SHA256_HEX(String  key, String message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
        char[] hexChars = Hex.encodeHex(mac.doFinal(message.getBytes()));
        return new String(hexChars).toLowerCase();
    }
    /**
     * 生成CanonicalRequest.
     * <p>CanonicalRequest是一个字符串，由HTTP Method、CanonicalURI、CanonicalQueryString、CanonicalHeaders四部分组成，四部分之间以换行符（\n）分隔的。CanonicalRequest的格式为：CanonicalRequest = HTTP Method + "\n" + CanonicalURI + "\n" + CanonicalQueryString + "\n" + CanonicalHeaders.</p>
     * @param httpMethod 指HTTP协议中定义的GET、PUT、POST等请求，必须使用全大写的形式
     * @param uri 百度Api里这么说的：...URL中的绝对路径...要求绝对路径必须以“/”开头，不以“/”开头的需要补充上，空路径为“/”
     * @param queryString URL中的Query String（Query String即URL中“？”后面的“key1 = valve1 & key2 = valve2 ”字符串）【BUT】目前人脸识别Api里只有"verify"、"identify"两种
     * @return canonicalRequest
     * @throws UnsupportedEncodingException
     */
    private static String canonicalRequest(String httpMethod, String uri, String queryString) throws UnsupportedEncodingException {
        /*
        是对URL中的绝对路径进行编码后的结果。要求绝对路径必须以“/”开头，不以“/”开头的需要补充上，空路径为“/”。
         */
        String canonicalURI = URLEncoder.encode(uri, "UTF-8").replace("%2F","/");
        /*
        对于URL中的Query String（Query String即URL中“？”后面的“key1 = valve1 & key2 = valve2 ”字符串）进行编码后的结果。
        编码方法为：
        1、将Query String根据&拆开成若干项，每一项是key=value或者只有key的形式。
        2、对拆开后的每一项进行如下处理：
            对于key是authorization，直接忽略。
            对于只有key的项，转换为UriEncode(key) + "="的形式，有关UriEncode函数的详细介绍，请参看函数介绍。
            对于key=value的项，转换为 UriEncode(key) + "=" + UriEncode(value) 的形式。这里value可以是空字符串。
        3、将上面转换后的所有字符串按照字典顺序排序。
        4、将排序后的字符串按顺序用 & 符号链接起来。
        【BUT】【BUT】【BUT】
        百度人脸识别Api目测没辣么复杂，只有三种queryString："groupName={groupName}"、"verify"、"identify"。。。
        So我要偷懒咯~~~
        【BUT】【BUT】【BUT】
         */
        String canonicalQueryString;
        if (queryString == null) canonicalQueryString = "";
        else
        {
            String[] keyAndValue = queryString.split("=");
            canonicalQueryString = URLEncoder.encode(keyAndValue[0], "UTF-8") + "=";
            if (keyAndValue.length == 2) canonicalQueryString += URLEncoder.encode(keyAndValue[1], "UTF-8");
        }
        /*
        CanonicalHeaders：对HTTP请求中的Header部分进行选择性编码的结果。
        您可以自行决定哪些Header 需要编码。百度开放云API的唯一要求是Host域必须被编码。大多数情况下，我们推荐您对以下Header进行编码：
            Host
            Content-Length
            Content-Type
            Content-MD5
        所有以 x-bce- 开头的Header
        如果这些Header没有全部出现在您的HTTP请求里面，那么没有出现的部分无需进行编码。
        如果您按照我们的推荐范围进行编码，那么认证字符串中的 {signedHeaders} 可以直接留空，无需填写。
        您也可以自行选择自己想要编码的Header。如果您选择了不在推荐范围内的Header进行编码，或者您的HTTP请求包含了推荐范围内的Header但是您选择不对它进行编码，那么您必须在认证字符串中填写 {signedHeaders} 。填写方法为，把所有在这一阶段进行了编码的Header名字转换成全小写之后按照字典序排列，然后用分号（;）连接。
        选择哪些Header进行编码不会影响API的功能，但是如果选择太少则可能遭到中间人攻击。
        对于每个要编码的Header进行如下处理：
        1、将Header的名字变成全小写。
        2、将Header的值去掉开头和结尾的空白字符。
        3、经过上一步之后值为空字符串的Header忽略，其余的转换为 UriEncode(name) + ":" + UriEncode(value) 的形式。
        4、把上面转换后的所有字符串按照字典序进行排序。
        5、将排序后的字符串按顺序用\n符号连接起来得到最终的CanonicalQueryHeaders。
        注意：很多发送HTTP请求的第三方库，会添加或者删除你指定的header（例如：某些库会删除content-length:0这个header），如果签名错误，请检查你您真实发出的http请求的header，看看是否与签名时的header一样。
         */
        String canonicalHeaders = URLEncoder.encode("host", "UTF-8") + ":" + URLEncoder.encode(HttpUtil.host, "UTF-8");
        /*
        生成CanonicalRequest.
        CanonicalRequest是一个字符串，由HTTP Method、CanonicalURI、CanonicalQueryString、CanonicalHeaders四部分组成，四部分之间以换行符（\n）分隔的。
        CanonicalRequest的格式为：CanonicalRequest = HTTP Method + "\n" + CanonicalURI + "\n" + CanonicalQueryString + "\n" + CanonicalHeaders
         */
        return httpMethod + "\n" + canonicalURI + "\n" + canonicalQueryString + "\n" + canonicalHeaders;
    }
}
