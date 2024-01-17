package com.esand.activity.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveDataUtil {
    private static final String CERT_NO = "CERT_NO";
    private static final String CERT_NAME = "CERT_NAME";
    private static final String ESANDCLOUD_PERFERENCE_FILE = "ESANDCLOUD_PERFERENCE_FILE";

    /**
     * 保存身份证号码
     * @param context
     * @param certNo
     * @return
     */
    public static boolean saveCertNo(Context context, String certNo) {
        boolean result;
        SharedPreferences sharedPreferences = context.getSharedPreferences(ESANDCLOUD_PERFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CERT_NO, certNo);
        result = editor.commit();
        return result;
    }

    /**
     * 获取身份证号码
     *
     * @param context
     * @return
     */
    public static String getCertNo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ESANDCLOUD_PERFERENCE_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CERT_NO, "");
    }

    /**
     * 保存身份证姓名
     *
     * @param context
     * @param certNo
     * @return
     */
    public static boolean saveCertName(Context context, String certNo) {
        boolean result;
        SharedPreferences sharedPreferences = context.getSharedPreferences(ESANDCLOUD_PERFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CERT_NAME, certNo);
        result = editor.commit();
        return result;
    }

    /**
     * 获取身份证姓名
     *
     * @param context
     * @return
     */
    public static String getCertName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ESANDCLOUD_PERFERENCE_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CERT_NAME, "");
    }
}
