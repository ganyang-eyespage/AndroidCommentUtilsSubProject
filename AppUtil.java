/**
 * AppUtils.java
 * com.eyespage.launcher.app.extension.common.util
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-5-26 		Jerome Song
 *
 * Copyright (c) 2014, JEROME All Rights Reserved.
 */

package com.eyespage.utils;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import com.eyespage.lib.R;
import com.eyespage.lib.log.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ClassName:AppUtils
 *
 * @author Jerome Song
 * @Date 2014-5-26 下午8:49:37
 * @see
 */
public class AppUtil {
  
  public static final String UNKNOWN = "Unknown";
  public static final String GOOGLE_MAPS_PKG_NAME = "com.google.android.apps.maps";
  private static final String TAG = "AppUtils";

  public static boolean isPackageInstalled(String packagename, Context context) {
    PackageManager pm = context.getPackageManager();
    try {
      pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
      return true;
    } catch (NameNotFoundException e) {
      return false;
    }
  }

  public static Drawable getAppIcon(Context context, String pkgName) {
    try {
      Drawable icon = context.getPackageManager().getApplicationIcon(pkgName);
      return icon;
    } catch (NameNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Drawable getActivityIcon(PackageManager manager, Intent intent) {
    try {
      ResolveInfo info = manager.resolveActivity(intent, 0);
      if (info != null) {
        return info.loadIcon(manager);
      }
    } catch (Exception e) {
    }
    return null;
  }

  public static String getClsNameFromPkgname(Context context, String pkgName) {
    Intent intent = getLaunchIntent(context, pkgName);
    if (intent != null && intent.getComponent() != null) {
      return intent.getComponent().getClassName();
    }
    return null;
  }

  public static Intent getLaunchIntent(Context context, String pkgName) {
    if (context == null) return null;
    return context.getPackageManager().getLaunchIntentForPackage(pkgName);
  }

  @Deprecated
  public static String getAppName(Context context, String pkgName) {
    String applicationName;
    ApplicationInfo ai;
    PackageManager mPm = context.getPackageManager();
    try {
      try {
        ai = mPm.getApplicationInfo(pkgName, 0);
      } catch (final NameNotFoundException e) {
        ai = null;
      }
      applicationName = (String) (ai != null ? mPm.getApplicationLabel(ai) : UNKNOWN);
    } catch (Exception e) {
      applicationName = UNKNOWN;
      e.printStackTrace();
    }
    applicationName = applicationName.trim();
    return trimIncludingNonbreakingSpace(applicationName);
  }

  public static String trimIncludingNonbreakingSpace(String s) {
    return s.replaceFirst("^[\\x00-\\x200\\xA0]+", "").replaceFirst("[\\x00-\\x20\\xA0]+$", "");
  }

  public static boolean startActivity(Context context, Intent intent) {
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      context.getApplicationContext().startActivity(intent);
      return true;
    } catch (SecurityException e) {
      Toast.makeText(context, R.string.activity_security_error, Toast.LENGTH_SHORT).show();
    } catch (ActivityNotFoundException e) {
      Toast.makeText(context, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
      Toast.makeText(context, R.string.activity_unknown_error, Toast.LENGTH_SHORT).show();
    }
    return false;
  }

  public static ArrayList<String> getRecentTasks(Context context) {
    ArrayList<String> recentPackages;
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
      recentPackages = new ArrayList<String>();
    } else {
      recentPackages = getRunningPackagesCompat(context, am);
    }
    return recentPackages;
  }

  static ArrayList<String> getRunningPackagesCompat(Context context, ActivityManager am) {
    List<ActivityManager.RecentTaskInfo> list =
        am.getRecentTasks(10, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
    ArrayList<String> recentPackages = new ArrayList<String>();
    Log.d(TAG, "recent tasks=" + list.size());
    Log.d(TAG, "recent tasks=" + list.toString());
    for (ActivityManager.RecentTaskInfo recentTaskInfo : list) {
      ComponentName componentName =
          (recentTaskInfo.baseIntent != null ? recentTaskInfo.baseIntent.getComponent() : null);
      Log.d(TAG, "recent tasks="
          + (componentName != null ? componentName.getPackageName() : "null")
          + "***"
          + (componentName != null ? componentName.getClassName() : "null")
          + "\n");
      if (componentName != null
          && !context.getPackageName().equals(componentName.getPackageName())
          && !componentName.getPackageName().startsWith("android")
          && !componentName.getPackageName().startsWith("com.google.android.googlequicksearchbox")
          && !componentName.getPackageName().startsWith("com.android")) {
        recentPackages.add(componentName.getPackageName());
      }
    }
    return recentPackages;
  }

  static ArrayList<String> getRunningPackages(Context context, ActivityManager am) {
    final Set<String> activePackages = new HashSet<String>();
    final List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
    for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
      if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND
          || processInfo.importance
          == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
        activePackages.addAll(Arrays.asList(processInfo.pkgList));
      }
    }
    ArrayList<String> recentPackages = new ArrayList<String>();
    for (String activePackage : activePackages) {
      if (!context.getPackageName().equals(activePackage)
          && !activePackage.startsWith("android")
          && !activePackage.startsWith("com.google.android.googlequicksearchbox")
          && !activePackage.startsWith("com.android")) {
        recentPackages.add(activePackage);
      }
    }
    Log.d(TAG, "recent runningtasks=" + recentPackages.toString());
    return recentPackages;
  }

  public static Intent getGoogleMapNavIntent(Context context, String address) {
    Uri uri = Uri.parse(String.format("http://maps.google.com/maps?daddr=%s", Uri.encode(address)));
    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
    if (getLaunchIntent(context, GOOGLE_MAPS_PKG_NAME) != null) {
      mapIntent.setPackage(GOOGLE_MAPS_PKG_NAME);
    }
    mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    return mapIntent;
  }

  public static String getTopAppPkgName(Context context) {
    ActivityManager activityMgr =
        (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT >= 21) {
      List runningProcessesList = activityMgr.getRunningAppProcesses();
      if ((runningProcessesList == null) || (runningProcessesList.size() == 0)) return null;
      if (runningProcessesList.size() > 0) {
        return ((ActivityManager.RunningAppProcessInfo) runningProcessesList.get(0)).processName;
      }
      return null;
    }
    List RunningTaskList = activityMgr.getRunningTasks(2);
    if ((RunningTaskList == null) || (RunningTaskList.size() == 0)) return null;
    if (RunningTaskList.size() > 0) {
      return ((ActivityManager.RunningTaskInfo) RunningTaskList.get(
          0)).topActivity.getPackageName();
    }
    return null;
  }
}