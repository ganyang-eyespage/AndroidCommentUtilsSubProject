package com.eyespage.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import com.eyespage.lib.log.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Random;

import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;

public class AndroidUtil {
  private static DisplayMetrics mDisplayMetrics;
  /**
   *  convert the dimen from dp to px
   * @param context
   * @param dipValue
   * @return
   */
  public static int dip2px(Context context, float dipValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dipValue * scale + 0.5f);
  }

  public static int px2dp(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  public static DisplayMetrics getDisplayMetrics(Context context) {
    if (mDisplayMetrics == null) {
      mDisplayMetrics = context.getResources().getDisplayMetrics();
    }
    return mDisplayMetrics;
  }

  public static int getNavigationBarHeight(Context context, int orientation) {
    Resources resources = context.getResources();
    int id = resources.getIdentifier(
        orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height"
            : "navigation_bar_height_landscape", "dimen", "android");
    if (id > 0) {
      return resources.getDimensionPixelSize(id);
    }
    return 0;
  }

  public static int getStatusBarHeight(Context context) {
    int result = 0;
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = context.getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }

  public static int randomColor() {
    Random random = new Random();
    return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
  }

  /**
   * getAppVersion:获取当前版本code
   *
   * @param @param context
   * @return int
   * @throws
   * @since 10:21:23 PM
   */
  public static String getAppVersion(Context context) {
    PackageManager m = context.getPackageManager();
    String app_ver;
    try {
      app_ver = m.getPackageInfo(context.getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException e) {
      throw new AssertionError();
    }
    return app_ver;
  }

  public static int getAppVersionCode(Context context) {
    PackageManager m = context.getPackageManager();
    int app_ver;
    try {
      app_ver = m.getPackageInfo(context.getPackageName(), 0).versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      throw new AssertionError();
    }
    return app_ver;
  }

  public static String getIMEI(Context context) {
    try {
      String a =
          ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
      return a;
    } catch (Exception paramContext) {
    }
    return "";
  }

  public static void createExternalStoragePrivateFile(Context context, String fileName, int resId) {
    File file = new File(context.getExternalFilesDir(null), fileName);
    try {

      InputStream is = context.getResources().openRawResource(resId);
      OutputStream os = new FileOutputStream(file);
      byte[] data = new byte[is.available()];
      is.read(data);
      os.write(data);
      is.close();
      os.close();
    } catch (IOException e) {
      Log.w("ExternalStorage", "Error writing " + file, e);
    }
  }

  public static void deleteExternalStoragePrivateFile(Context context, String fileName) {
    // Get path for the file on external storage.  If external
    // storage is not currently mounted this will fail.
    File file = new File(context.getExternalFilesDir(null), fileName);
    if (file != null) {
      file.delete();
    }
  }

  public static boolean hasExternalStoragePrivateFile(Context context, String fileName) {
    File file = new File(context.getExternalFilesDir(null), fileName);
    if (file != null) {
      return file.exists();
    }
    return false;
  }

  public static void copyFileUsingFileChannels(File sourceFile, File destFile) throws IOException {
    Log.d("copy to sdcard", "copyFileUsingFileChannels");
    destFile.createNewFile();

    FileChannel source = null;
    FileChannel destination = null;

    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (source != null) {
        source.close();
      }
      Log.d("copy to sdcard", destFile.length() + "");
      if (destination != null) {
        destination.close();
      }
    }
  }

  public static int calculateMemoryCacheSize(Context context) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
    int memoryClass = am.getMemoryClass();
    if (largeHeap && SDK_INT >= HONEYCOMB) {
      memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
    }
    // Target ~15% of the available heap.
    return 1024 * 1024 * memoryClass / 7;
  }

  @TargetApi(HONEYCOMB)
  private static class ActivityManagerHoneycomb {
    static int getLargeMemoryClass(ActivityManager activityManager) {
      return activityManager.getLargeMemoryClass();
    }
  }

  /**
   * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
   *
   * @return true 表示开启
   */
  public static final boolean isGPSOpened(final Context context) {
    LocationManager locationManager =
        (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    if (gps || network) {
      return true;
    }
    return false;
  }

  public static final void openGPSSetting(Context context) {
    Intent intent = new Intent();
    intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      context.startActivity(intent);
    } catch (ActivityNotFoundException ex) {
      intent.setAction(android.provider.Settings.ACTION_SETTINGS);
      try {
        context.startActivity(intent);
      } catch (Exception e) {
      }
    }
  }
}