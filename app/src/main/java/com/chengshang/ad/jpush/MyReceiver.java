package com.chengshang.ad.jpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.chengshang.ad.MainActivity;
import com.chengshang.ad.NoticeCenterActivity;
import com.chengshang.ad.R;
import com.chengshang.ad.WebViewActivity;
import com.chengshang.ad.speek.SpeechTool;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import androidx.core.app.NotificationCompat;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
  private static final String TAG = "JIGUANG-Example";

  @Override
  public void onReceive(Context context, Intent intent) {
    try {
      Bundle bundle = intent.getExtras();
      Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

      if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
        Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
        //send the Registration Id to your server...

      } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        processCustomMessage(context, bundle);

      } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
        Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
        //在这里自定义通知声音
        processCustomMessage(context, bundle);
      } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
        Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");

        //打开自定义的Activity
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(extras);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // 获得 flag 节点的值, flag 节点为基本数据节点
        JsonPrimitive url = jsonObject.getAsJsonPrimitive("url");
        //此处判断跳转消息列表还是webView
        JsonPrimitive type = jsonObject.getAsJsonPrimitive("type");
        JsonPrimitive open_type = jsonObject.getAsJsonPrimitive("open_type");
        if (!open_type.getAsString().equals("0")) {
          Intent i = new Intent(context, WebViewActivity.class);
          i.putExtra("open_type", open_type.getAsString());
          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
          context.startActivity(i);
        }
        if (type.getAsString().equals("1")) {
          //正常列表
          Intent i = new Intent(context, NoticeCenterActivity.class);
          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
          context.startActivity(i);
        } else {
          //环信
          Intent i = new Intent(context, WebViewActivity.class);
          i.putExtra("url", url.getAsString());
          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
          context.startActivity(i);
        }
      } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
        Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
        //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

      } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        Logger.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
      } else {
        Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
      }
    } catch (Exception e) {

    }

  }


  // 打印所有的 intent extra 数据
  private static String printBundle(Bundle bundle) {
    StringBuilder sb = new StringBuilder();
    for (String key : bundle.keySet()) {
      if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
        sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
      } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
        sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
      } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
        if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
          Logger.i(TAG, "This message has no Extra data");
          continue;
        }

        try {
          JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
          Iterator<String> it = json.keys();

          while (it.hasNext()) {
            String myKey = it.next();
            sb.append("\nkey:" + key + ", value: [" +
                myKey + " - " + json.optString(myKey) + "]");
          }
        } catch (JSONException e) {
          Logger.e(TAG, "Get message extra JSON error!");
        }

      } else {
        sb.append("\nkey:" + key + ", value:" + bundle.get(key));
      }
    }
    return sb.toString();
  }

  //send msg to MainActivity
  //	private void processCustomMessage(Context context, Bundle bundle) {
  //		if (MainActivity.isForeground) {
  //			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
  //			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
  //			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
  //			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
  //			if (!ExampleUtil.isEmpty(extras)) {
  //				try {
  //					JSONObject extraJson = new JSONObject(extras);
  //					if (extraJson.length() > 0) {
  //						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
  //					}
  //				} catch (JSONException e) {
  //
  //				}
  //
  //			}
  //			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
  //		}
  //	}

  /**
   * 自定义推送的声音
   *
   * @param context
   * @param bundle
   */
  private void processCustomMessage(Context context, Bundle bundle) throws JSONException {
    /**
     *  这个地方特殊说明一下
     *  因为我们发出推送大部分是分为两种，一种是在极光官网直接发送推送，第二是根据后台定义
     * 的NOTIFICATION发送通知，所以在这里你要根据不同的方式获取不同的内容
     **/
    //如果是使用后台定的NOTIFICATION发送推送，那么使用下面的方式接收消息
    String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);  //扩展消息
    String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);  //推送标题
    String alert = bundle.getString(JPushInterface.EXTRA_ALERT);   //推送消息

    //如果是直接使用极光官网发送推送，那么使用这个来接收消息内容
    // String title = bundle.getString(JPushInterface.EXTRA_TITLE);
    //String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
    //  String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
    NotificationCompat.Builder notification;
    JsonParser parser = new JsonParser();
    JsonElement jsonElement = parser.parse(extras);
    JsonObject jsonObject = jsonElement.getAsJsonObject();


    //此处判断提示语音
    JsonPrimitive type = jsonObject.getAsJsonPrimitive("type");

    if(type ==null) {
      Log.i("====clerk", "====");
      notification = new NotificationCompat.Builder(context, "店员");
      //这一步必须要有而且setSmallIcon也必须要，没有就会设置自定义声音不成功
      notification.setAutoCancel(true).setSmallIcon(R.drawable.jpush_notification_icon);
      Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.new_order);
      context.grantUriPermission("com.android.systemui", uri,
          Intent.FLAG_GRANT_READ_URI_PERMISSION);
      notification.setSound(uri, AudioManager.STREAM_ALARM);
      Log.i("===URI===", uri.toString());
      JPushInterface.setChannel(context, "店员");
//        JsonPrimitive url = jsonObject.getAsJsonPrimitive("url");
      SpeechTool.getInstance(context, 0).initSpeech(alert);
    }

    Log.d(TAG, "processCustomMessage: " + type.getAsString());
    // 判断 extra 的 type 字段
    switch (type.getAsString()) {
      case "apppoint"://服务者
      {
        Log.i("====apppoint", "====");
        notification = new NotificationCompat.Builder(context, "服务者和配送员");
        //这一步必须要有而且setSmallIcon也必须要，没有就会设置自定义声音不成功
        notification.setAutoCancel(true).setSmallIcon(R.drawable.jpush_notification_icon);
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.fuwutishi);
        context.grantUriPermission("com.android.systemui", uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION);
        notification.setSound(uri, AudioManager.STREAM_ALARM);
        Log.i("===URI===", uri.toString());
        JPushInterface.setChannel(context, "服务者和配送员");
        break;
      }
      case "clerk"://老店员
      case "staff"://新店员
      {
        Log.i("====clerk", "====");
        notification = new NotificationCompat.Builder(context, "店员");
        //这一步必须要有而且setSmallIcon也必须要，没有就会设置自定义声音不成功
        notification.setAutoCancel(true).setSmallIcon(R.drawable.jpush_notification_icon);
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.new_order);
        context.grantUriPermission("com.android.systemui", uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION);
        notification.setSound(uri, AudioManager.STREAM_ALARM);
        Log.i("===URI===", uri.toString());
        JPushInterface.setChannel(context, "店员");
//        JsonPrimitive url = jsonObject.getAsJsonPrimitive("url");
        SpeechTool.getInstance(context, 0).initSpeech(alert);
        break;
      }
      case "merchant"://店长
      {
        Log.i("====merchant", "====");
        notification = new NotificationCompat.Builder(context, context.getPackageName());
        notification.setAutoCancel(true).setSmallIcon(R.drawable.jpush_notification_icon);
        notification.setDefaults(Notification.DEFAULT_ALL);
//                JsonPrimitive flagJson2 = jsonObject.getAsJsonPrimitive("type");
        //				notification.setDefaults(Notification.DEFAULT_ALL);
        //				Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.new_order);
        //				context.grantUriPermission("com.android.systemui",uri ,
        //						Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //				notification.setSound(uri, AudioManager.STREAM_ALARM);
        //				Log.i("===URI===",uri.toString());
        break;
      }
      case "deliver"://配送员
      {
        Log.i("====deliver", "====");
        notification = new NotificationCompat.Builder(context, "服务者和配送员");
        //这一步必须要有而且setSmallIcon也必须要，没有就会设置自定义声音不成功
        notification.setAutoCancel(true).setSmallIcon(R.drawable.jpush_notification_icon);
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.fuwutishi);
        context.grantUriPermission("com.android.systemui", uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION);
        notification.setSound(uri, AudioManager.STREAM_ALARM);
        Log.i("===URI===", uri.toString());
        JPushInterface.setChannel(context, "服务者和配送员");
        break;
      }
      default:
        notification = new NotificationCompat.Builder(context, context.getPackageName());
        //这一步必须要有而且setSmallIcon也必须要，没有就会设置自定义声音不成功
        notification.setAutoCancel(true).setSmallIcon(R.drawable.jpush_notification_icon);
        notification.setDefaults(Notification.DEFAULT_ALL);
        break;
    }


    //打开自定义的Activity
    // 获得 flag 节点的值, flag 节点为基本数据节点
//    JsonPrimitive flagJson = jsonObject.getAsJsonPrimitive("url");
//    //此处判断跳转消息列表还是webView
//    JsonPrimitive flagJson1 = jsonObject.getAsJsonPrimitive("type");
//    Intent mIntent;
//    if (flagJson1.getAsString().equals("1")) {
//      //正常列表
//      mIntent = new Intent(context, NoticeCenterActivity.class);
//      mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
//
//      notification.setContentIntent(pendingIntent)
//          .setAutoCancel(true)
//          .setContentText(alert)
//          .setContentTitle(title.equals("") ? "title" : title)
//          .setSmallIcon(R.drawable.jpush_notification_icon);
//      //获取推送id
//      int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//      //bundle.get(JPushInterface.EXTRA_ALERT);推送内容
//      //最后刷新notification是必须的
//      NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//      notificationManager.notify(notifactionId, notification.build()); //在此处放入推送id —notifactionId
//      //			context.startActivity(i);
//    } else if (flagJson1.getAsString().equals("2")) {
//      SpeechTool.getInstance(context, 0).initSpeech(alert);
//    } else {
//      //环信
//      mIntent = new Intent(context, WebViewActivity.class);
//      try {
//        mIntent.putExtra("url", java.net.URLDecoder.decode(flagJson.getAsString(), "utf-8"));
//      } catch (UnsupportedEncodingException e) {
//        e.printStackTrace();
//      }
//      mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
//
//      notification.setContentIntent(pendingIntent)
//          .setAutoCancel(true)
//          .setContentText(alert)
//          .setContentTitle(title.equals("") ? "title" : title)
//          .setSmallIcon(R.drawable.jpush_notification_icon);
//      //获取推送id
//      int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//      //bundle.get(JPushInterface.EXTRA_ALERT);推送内容
//      //最后刷新notification是必须的
//      NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//      notificationManager.notify(notifactionId, notification.build()); //在此处放入推送id —notifactionId
//      //			context.startActivity(i);
//    }
//    //这是点击通知栏做的对应跳转操作，可以根据自己需求定义
//    //		Intent mIntent = new Intent(context, MainActivity.class);
//    //		mIntent.putExtra("msgId", json.getString("msgId"));
//    //		mIntent.putExtras(bundle);
//    //		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  }

}
