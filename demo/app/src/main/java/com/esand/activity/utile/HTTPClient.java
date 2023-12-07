package com.esand.activity.utile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
    // TODO 此处需要替换成您的APPCODE (在 build.gradle文件中，appcode要放在服务器端，请妥善保管，切勿泄漏)
    private String FACE_APPCODE;

    public HTTPClient(Context mContext) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            FACE_APPCODE = appInfo.metaData.getString("FACE_APPCODE");
            if (FACE_APPCODE == null || FACE_APPCODE.trim().equals("")) {
                Log.i("LDT","APPCODE 为空，请在build.gradle中配置APPCODE, 如有疑问可联系微信： esand_info");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发起认证初始化操作
     *
     * @param msg
     * @return
     */
    public String authInit(String msg) {
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("initMsg", msg);
        body = bodyBuilder.build();
        return post("http://eface.market.alicloudapi.com/init", body);
    }

    /**
     * 发起认证初始化操作
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
        return post("https://searchface.market.alicloudapi.com/init", body);
    }

    /**
     * 发起认证初始化操作
     *
     * @param msg
     * @return
     */
    public String RPauthInit(String msg,String certName,String certNo) {
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("initMsg", msg)
                .add("certName",certName)
                .add("certNo",certNo);
        body = bodyBuilder.build();
        return post("http://apprpv.market.alicloudapi.com/init", body);
    }
    /**
     * 认证操作
     * @return
     */
    public String RPauth(EsLivingDetectResult result) {
        MyLog.error("输出data"+result.getData());
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("token", result.getToken())
                .add("verifyMsg", result.getData());
        body = bodyBuilder.build();
        return post("http://apprpv.market.alicloudapi.com/verify", body);
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
        return post("https://efaceid.market.alicloudapi.com/init", body);
    }

    /**
     * 认证操作
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
        return post("https://efaceid.market.alicloudapi.com/verify", body);
    }

    /**
     * 认证操作
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
        return post("https://searchface.market.alicloudapi.com/verify", body);
    }
    /**
     * 认证操作
     *
     * @return
     */
    public String auth(EsLivingDetectResult result) {
        MyLog.error("输出data"+result.getData());
        FormBody body;
        String rspStr = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("token", result.getToken())
                .add("verifyMsg", result.getData());
        body = bodyBuilder.build();
        return post("http://eface.market.alicloudapi.com/verify", body);
    }

    private String post(String url, FormBody body){
        String rspStr = null;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            Request.Builder requestBuilder = new Request.Builder().url(url);
            requestBuilder.addHeader("Authorization", "APPCODE " + FACE_APPCODE);
            requestBuilder.addHeader("X-Ca-Nonce", UUID.randomUUID().toString());
            requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestBuilder.post(body);
            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
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
