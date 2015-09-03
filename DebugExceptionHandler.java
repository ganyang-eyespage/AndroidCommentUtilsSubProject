package com.eyespage.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import com.eyespage.lib.log.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 异常日志捕获类
 *
 * @author cylee
 */
public class DebugExceptionHandler implements UncaughtExceptionHandler {
  /** 崩溃日志保存的文件路径*/
  public static final String CRASH_LOG_FILE_PATH = Environment.getExternalStorageDirectory()+
      "/L/log/EyespageDebugException.log";
  private final static String TAG = DebugExceptionHandler.class.getSimpleName();
  /** 系统默认的UncaughtException处理类 */
  private UncaughtExceptionHandler mDefaultHandler;
  private static DebugExceptionHandler mInstance;
  private Context mContext;

  private DebugExceptionHandler(Context ctx) {
    mContext = ctx;
    mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler(this);
  }

  public static DebugExceptionHandler getInstance(Context ctx) {
    if (mInstance == null) {
      mInstance = new DebugExceptionHandler(ctx);
    }
    return mInstance;
  }

  @Override public void uncaughtException(Thread thread, Throwable ex) {
    handleException(ex);
    if (mDefaultHandler != null) {
      mDefaultHandler.uncaughtException(thread, ex);
    }
  }

  /**
   * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
   *
   * @return true:如果处理了该异常信息;否则返回false
   */
  private boolean handleException(Throwable ex) {
    if (ex == null) {
      return true;
    }
    // 保存错误日志文件
    saveExceptionInfo(ex);

    return true;
  }

  private String GetBuildParams(final String params) {
    try {
      Field fd = android.os.Build.class.getField(params);
      Build bd = new android.os.Build();
      if (null != fd) {
        return fd.get(bd).toString();
      }
    } catch (Exception e) {
    }
    return "";
  }

  /**
   * 保存错误日志文件
   */
  synchronized private void saveExceptionInfo(Throwable e) {
    File f = new File(CRASH_LOG_FILE_PATH);
    FileOutputStream fs = null;
    try {
      fs = new FileOutputStream(f, true);
      fs.write(getDate().getBytes());
      fs.write('\n');

      try {
        PackageInfo appInfo =
            mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        fs.write(appInfo.versionName.getBytes());
      } catch (Exception e2) {
      }

      try {
        StringBuilder userAgent = new StringBuilder();
        userAgent.append(GetBuildParams("MANUFACTURER"));
        userAgent.append("|");
        userAgent.append(GetBuildParams("MODEL"));
        userAgent.append("|");
        userAgent.append(GetBuildParams("PRODUCT"));
        userAgent.append("|ANDROID");
        userAgent.append(android.os.Build.VERSION.RELEASE);
        fs.write(userAgent.toString().getBytes());
        fs.write('\n');
      } catch (Exception e3) {
      }

      PrintStream ps = new PrintStream(fs);
      e.printStackTrace(ps);
      fs.write('\n');
      fs.write('\n');
    } catch (FileNotFoundException e1) {
    } catch (IOException e2) {
    } finally {
      Log.e(TAG, "", e);
      if (fs != null) {
        try {
          fs.close();
        } catch (IOException e1) {
        }
      }
    }
  }

  public static String getDate() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sf.format(cal.getTime());
  }
}
