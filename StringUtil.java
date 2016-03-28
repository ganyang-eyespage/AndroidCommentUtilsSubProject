package com.eyespage.utils;

import java.net.URLEncoder;

/**
 * Created by jerome on 7/13/15.
 */
public class StringUtil {
  public static final CharSequence EMPTY = "";

  public static boolean equals(String str1, String str2) {
    return str1 == null ? str2 == null : str1.equals(str2);
  }

  public static String encodeUrl(String url) {
    if (url != null) {
      try {
        return URLEncoder.encode(url, "UTF-8");
      } catch (Exception e){
        return URLEncoder.encode(url);
      }
    }
    return "";
  }

  public static String encodeUrlParam(String param) {
    return encodeUrl(param).replaceAll("\\+", "%20");
  }
}
