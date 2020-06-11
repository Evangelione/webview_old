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
## ------------------------------------- 混淆基础配置 ---------------------------------------------
-optimizationpasses 5                               # 指定代码的压缩级别
-dontusemixedcaseclassnames                         # 混淆时不会产生形形色色的类名
-dontskipnonpubliclibraryclasses                    # 指定不去忽略非公共的库类
-dontskipnonpubliclibraryclassmembers               # 指定不去忽略包可见的库类的成员
-dontpreverify                                      # 不预校验
-ignorewarnings                                     # 屏蔽警告
-verbose                                            # 混淆时记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*    #优化
-keepattributes *Annotation*                        # 保护代码中的Annotation不被混淆
-keepattributes Signature                           # 避免混淆泛型, 这在JSON实体映射时非常重要
-keepattributes SourceFile,LineNumberTable          # 抛出异常时保留代码行号

# ------------------------------------- 不需要混淆的第三方类库 ---------------------------------

# 不需要混淆android-support-v4.jar
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

# ------------------------------------- 不需要混淆的系统组件和本地实体类 ----------------------------
-keepclassmembers class com.ad.chengshang.wxapi.WeChatInfo{ *; }

## Android底层组件和类不要混淆
-keep class **.R$*{*;}

# 保留了继承自Activity、Application这些类的子类
# 因为这些子类有可能被外部调用
# 比如第一行就保证了所有Activity的子类不要被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.view.View

-keepclassmembers class * {
    void *(android.view.View);
        *** *Click*(...);
        *** *Event(...);
}

-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.fragment.app.DialogFragment
-keep public class * extends androidx.appcompat.app.AppCompatActivity

#EventBus3.0配置
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}


#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

##okio混淆
-keep class okio.** {*;}
-dontwarn okio.**

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

-keep class com.umeng.** {*;}
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.** {*;}
-dontwarn com.baidu.**

-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-keep class com.tencent.mm.opensdk.** {
*;
}

-keep class com.tencent.wxop.** {
*;
}

-keep class com.tencent.mm.sdk.** {
*;
}

-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**

-keepclassmembers class com.ad.chengshang.common.AndroidInterface{ *; }

-keepclassmembers class com.ad.chengshang.wxapi.WXEntryActivity{ *; }

-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}