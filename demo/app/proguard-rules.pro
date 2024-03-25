# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留JNI类不被混淆
-keep class com.esandinfo.livingdetection.jni.* {*;}

# 保留uni-app插件类不被混淆
-keep class com.esandinfo.livingdetection.uniappmodule.* {*;}

# 保留对外接口不被混淆
-keep class com.esandinfo.livingdetection.EsLivingDetectionManager {*;}
-keep class com.esandinfo.livingdetection.EsVideoManager {*;}
-keep class com.esandinfo.livingdetection.bean.** {*;}
-keep class com.esandinfo.livingdetection.biz.** {*;}
-keep class com.esandinfo.livingdetection.constants.** {*;}
-keep class com.esandinfo.livingdetection.util.MyLog {*;}
-keep class com.esandinfo.livingdetection.util.AppExecutors {*;}
-keep class com.esandinfo.livingdetection.util.GsonUtil {*;}
-keep class com.esandinfo.livingdetection.util.StringUtil {*;}
-keep class com.esandinfo.livingdetection.util.HTTPClient {*;}
-keep class com.esandinfo.livingdetection.activity.** {*;}
-keep class com.esandinfo.ocr.lib.**.** {*;}
-keep class com.esandinfo.ocr.lib.** {*;}

# 保留uni-app插件库
-keep class com.taobao.weex.** {*;}
-keep class io.dcloud.** {*;}

-ignorewarnings
