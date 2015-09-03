package com.eyespage.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
  static Toast toast;

  private ToastUtil() {
  }

  private static void makeToastIfNeeded(Context context) {
    if (toast == null) {
      toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }
  }

  public static void showShort(Context context, int resId) {
    makeToastIfNeeded(context);
    toast.setDuration(Toast.LENGTH_SHORT);
    toast.setText(resId);
    toast.show();
  }

  public static void showShort(Context context, String message) {
    makeToastIfNeeded(context);
    toast.setDuration(Toast.LENGTH_SHORT);
    toast.setText(message);
    toast.show();
  }

  public static void showLong(Context context, int resId) {
    makeToastIfNeeded(context);
    toast.setDuration(Toast.LENGTH_LONG);
    toast.setText(resId);
    toast.show();
  }

  public static void showLong(Context context, String message) {
    makeToastIfNeeded(context);
    toast.setDuration(Toast.LENGTH_LONG);
    toast.setText(message);
    toast.show();
  }
}
