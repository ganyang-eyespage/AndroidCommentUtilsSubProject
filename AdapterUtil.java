package com.eyespage.utils;

/**
 * Created by cylee on 15/5/27.
 */
public class AdapterUtil {
  public static final boolean isSamSung() {
    String manufacturer = android.os.Build.MANUFACTURER;
    if (manufacturer != null) {
      return manufacturer.contains("samsung") || manufacturer.contains("SAMSUNG");
    }
    return false;
  }
}
