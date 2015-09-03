package com.eyespage.utils;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

public class SplitWords {
  public static final String DELIMITERS = " \t\n\r\f~!@#$%^&*()_+|`-=\\{}[]:\";'<>?,./'";

  public static String[] split(String source, boolean b) {
    StringTokenizer stringTokenizer = new StringTokenizer(source, DELIMITERS);
    Vector vector = new Vector();
    Vector vectorForAllUpperCase = new Vector();
    flag0:
    while (stringTokenizer.hasMoreTokens()) {
      String token = stringTokenizer.nextToken();
      boolean allUpperCase = true;
      for (int i = 0; i < token.length(); i++) {
        if (!Character.isUpperCase(token.charAt(i))) {
          allUpperCase = false;
        }
      }
      if (allUpperCase) {
        vectorForAllUpperCase.addElement(token);
        continue flag0;
      }
      int index = 0;
      flag1:
      while (index < token.length()) {
        flag2:
        while (true) {
          index++;
          if ((index == token.length()) || (!Character.isLowerCase(token.charAt(index)) && (index
              > 0 && Character.isLowerCase(token.charAt(index - 1)))) || (Character.isLowerCase(
              token.charAt(index)) && (index > 1
              && !Character.isLowerCase(token.charAt(index - 1))
              && !Character.isLowerCase(token.charAt(index - 2)))) || (isChinese(
              token.charAt(index)) && (index > 0 && !isChinese(token.charAt(index - 1)))) || (b
              && isChinese(token.charAt(index))) || (!isChinese(token.charAt(index)) && (index > 0
              && isChinese(token.charAt(index - 1))))) {
            break flag2;
          }
        }
        vector.addElement(token.substring(0, index).toLowerCase());
        token = token.substring(index);
        index = 0;
        continue flag1;
      }
    }

    for (int i = 0; i < vectorForAllUpperCase.size(); i++) {
      vector.addElement(vectorForAllUpperCase.elementAt(i));
    }

    String[] array = new String[vector.size()];
    Enumeration enumeration = vector.elements();
    int index = 0;
    while (enumeration.hasMoreElements()) {
      array[index] = (String) enumeration.nextElement();
      index++;
    }

    return array;
  }

  public static boolean isChinese(char c) {
    return Character.toString(c).matches("[\u4E00-\u9FA5]+");
  }

  public static String[] splitCn(String source) {
    SplitWords sw = new SplitWords();
    Set<String> out = new LinkedHashSet<String>();
    String[] strings = sw.split(source, false);
    out.addAll(Arrays.asList(strings));
    for (String s : strings) {
      out.addAll(Arrays.asList(split(s, true)));
    }
    String[] result = new String[out.size()];
    out.toArray(result);
    return result;
  }
  public static String removeDelimiters(String input){
    StringTokenizer st = new StringTokenizer(input,DELIMITERS);
    StringBuilder builder = new StringBuilder();
    while(st.hasMoreTokens()){
      builder.append(st.nextToken());
      builder.append(" ");
    }
    return builder.toString().trim();
  }

  
  public static void main(String args[]) {
    SplitWords sw = new SplitWords();
    //Set<String> out = new LinkedHashSet<String>();
    //String[] strings = sw.split("中国字ABC&你好 SpeedVPN WeChat TIGERvpn", false);
    //out.addAll(Arrays.asList(strings));
    //for (String s : strings) {
    //  out.addAll(Arrays.asList(split(s, true)));
    //}
    System.out.println(Arrays.toString(sw.splitCn("中国字ABC&你好 WeChat TIGERVpn ")));
  }
  
} 