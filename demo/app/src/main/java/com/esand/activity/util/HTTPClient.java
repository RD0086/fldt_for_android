package com.esand.activity.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.esandinfo.livingdetection.bean.EsLivingDetectResult;
import com.esandinfo.livingdetection.util.MyLog;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HTTP工具类
 */
public class HTTPClient {

    // WARNNING！！ : 为了保护密钥，这段代码建议写在服务器端，这里为了方便演示，把密钥写客户端了。
    // TODO 阿里云接入，请替换这里的APPCODE （为了保护APPCODE,此段代码通常放在服务器端）
    private String APPCODE;
    // TODO 非阿里云接入，请从管理控制台查询并替换这里的 APPCODE 和密钥, 参考文档： https://esandinfo.yuque.com/yv6e1k/aa4qsg/cdwove
    private String E_APPCODE;// = "d2808c1338ce01f3e3efdb486f9effb9";
    private String E_SECRET;// = "/OUmYXYo5O5CrzXQakeF789PU4RSKVdObVtCSwp28g==";

    public HTTPClient(Context mContext) {
        ApplicationInfo appInfo = null;
        if (APPCODE == null && (E_APPCODE == null&&E_SECRET == null)) {
            Log.i("LDT","如果是阿里云网关接入，请先设置 APPCODE , 如果非阿里云网关接入，请先设置 E_APPCODE, E_SECRET ， 如有疑问请联系 ：13691664797");
        }
    }

    /**
     * 活体检测初始化
     *
     * @param msg
     * @return
     */
    public String ldtInit(String msg) {
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("initMsg", msg);
        body = bodyBuilder.build();
        String url = null;
        if (APPCODE != null) {
            url = "http://eface.market.alicloudapi.com/init";
        } else {
            url = String.format("https://edis.esandcloud.com/gateways?APPCODE=%s&ACTION=%s",E_APPCODE, "livingdetection/livingdetect/init");
        }

        return post(url, body);
    }

    /**
     * 获取活体检测结果
     *
     * @return
     */
    public String ldtAuth(EsLivingDetectResult result) {
        MyLog.error("输出data"+result.getData());
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("token", result.getToken())
                .add("verifyMsg", result.getData());
        body = bodyBuilder.build();
        String url = null;
        if (APPCODE != null) {
            url = "http://eface.market.alicloudapi.com/verify";
        } else {
            url = String.format("https://edis.esandcloud.com/gateways?APPCODE=%s&ACTION=%s",E_APPCODE, "livingdetection/livingdetect/verify");
        }

        return post(url, body);
    }

    /**
     * 实名认证初始化
     *
     * @param msg
     * @return
     */
    public String idInit(String msg,String certName,String certNo) {
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("initMsg", msg)
                .add("certName",certName)
                .add("certNo",certNo);
        body = bodyBuilder.build();

        String url = null;
        if (APPCODE != null) {
            url = "http://apprpv.market.alicloudapi.com/init";
        } else {
            url = String.format("https://edis.esandcloud.com/gateways?APPCODE=%s&ACTION=%s",E_APPCODE, "livingdetection/rpverify/init");
        }

        return post(url, body);
    }

    /**
     * 获取实名认证结果
     * @return
     */
    public String idAuth(EsLivingDetectResult result) {
        MyLog.error("输出data"+result.getData());
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("token", result.getToken())
                .add("verifyMsg", result.getData());
        body = bodyBuilder.build();
        String url = null;
        if (APPCODE != null) {
            url = "http://apprpv.market.alicloudapi.com/verify";
        } else {
            url = String.format("https://edis.esandcloud.com/gateways?APPCODE=%s&ACTION=%s",E_APPCODE, "livingdetection/rpverify/verify");
        }

        return post(url, body);
    }

    /**
     * 刷脸认证初始化
     * @param msg
     * @return
     */
    public String faceCompareInit(String msg) {
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("initMsg", msg)
                .add("withoutReg","true");
        body = bodyBuilder.build();
        String url = null;
        if (APPCODE != null) {
            url = "https://efaceid.market.alicloudapi.com/init";
        } else {
            url = String.format("https://edis.esandcloud.com/gateways?APPCODE=%s&ACTION=%s",E_APPCODE, "livingdetection/faceContrast/init");
        }

        return post(url, body);
    }

    /**
     * 获取刷脸认证结果
     * @return
     */
    public String faceCompareVerify(EsLivingDetectResult result) {
        MyLog.error("输出data"+result.getData());
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("token", result.getToken())
                .add("verifyMsg", result.getData());
        body = bodyBuilder.build();
        String url = null;
        if (APPCODE != null) {
            url = "https://efaceid.market.alicloudapi.com/verify";
        } else {
            url = String.format("https://edis.esandcloud.com/gateways?APPCODE=%s&ACTION=%s",E_APPCODE, "livingdetection/faceContrast/verify");
        }

        return post(url, body);
    }

    /**
     * 人脸搜索初始化
     *
     * @param msg
     * @return
     */
    public String searchInit(String msg) {
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("initMsg", msg);
        body = bodyBuilder.build();
        String url = null;
        if (APPCODE != null) {
            url = "https://searchface.market.alicloudapi.com/init";
        }

        return post(url, body);
    }

    /**
     * 获取人脸搜索结果
     *
     * @return
     */
    public String searchAuth(EsLivingDetectResult result,String dbName) {
        MyLog.error("输出data"+result.getData());
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("token", result.getToken())
                .add("verifyMsg", result.getData())
                .add("dbName",dbName);
        body = bodyBuilder.build();
        String url = null;
        if (APPCODE != null) {
            url = "https://searchface.market.alicloudapi.com/verify";
        }

        return post(url, body);
    }

    private String post(String url, FormBody body){
        String rspStr = null;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            String appcode = null;
            if (APPCODE != null) {
                appcode = APPCODE;
            } else {
                appcode = E_SECRET;
            }

            Request.Builder requestBuilder = new Request.Builder().url(url);
            requestBuilder.addHeader("Authorization", "APPCODE " + appcode);
            requestBuilder.addHeader("X-Ca-Nonce", UUID.randomUUID().toString());
            requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestBuilder.post(body);
            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
            // 打印 header 数据
            MyLog.info("服务器返回header 数据 ：" + response.headers().toString());
            rspStr = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return rspStr;
    }
    public static String post(String url,String msg) {
        String rspStr = null;
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), msg);
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                rspStr = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return rspStr;
        }
    }

    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
