/**
 * 创建人：SongZhiyong
 * 创建时间：2013-1-3
 */
package com.eyespage.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.eyespage.lib.log.Log;

/**
 * 系统联网状态工具类
 *
 * @author SongZhiyong
 */
public class NetUtil {
  private static final String TAG = "NetUtil";

  /**
   * 检查android联网状态
   */
  public static boolean checkNetworkState(final Context context) {
    boolean flag = false;
    ConnectivityManager manager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (manager != null) {
      NetworkInfo info = manager.getActiveNetworkInfo();
      if (info != null) {
        flag = info.isAvailable();
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
          WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
          WifiInfo wifiInfo = wifiManager.getConnectionInfo();
          if (wifiInfo != null) {
            int wifiState = wifiInfo.getRssi();
            Log.i(TAG, "net rssi:" + wifiState);
            if (wifiState < -60) {
              //ToastUtil.showShort(context, "您当前网络环境较差，请检查网络！");
              //return false;
            }
          }
        }
      }
    }
    return flag;
  }

  public static boolean isWifiOn(final Context context) {
    boolean flag = false;
    ConnectivityManager connManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if (mWifi.isConnected() && mWifi.isAvailable()) {
      flag = true;
    }
    return flag;
  }

  public static NetworkInfo getActiveNetworkInfo(Context context) {
    ConnectivityManager connectivity =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
    return networkInfo;
  }

  /**
   * getWifiAddress:获取wifimac地址
   *
   * @param @param context
   * @return String
   * @throws
   * @since 9:56:09 AM
   */
  public static String getWifiAddress(Context context) {
    WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo info = manager.getConnectionInfo();
    String address = info.getMacAddress();
    return address;
  }

  public static String getDefaultAPN(Context context) {
    final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
    c.moveToFirst();
    int index = c.getColumnIndex("_id");
    index = c.getColumnIndex("appName");
    String name = c.getString(index);
    return name;
  }

  // network available cannot ensure Internet is available
  public static boolean isNetWorkAvailable(final Context context) {
    Runtime runtime = Runtime.getRuntime();
    try {
      Process pingProcess = runtime.exec("/system/bin/ping -c 1 www.baidu.com");
      int exitCode = pingProcess.waitFor();
      return (exitCode == 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}
