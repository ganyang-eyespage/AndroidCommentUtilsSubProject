package com.eyespage.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

public class IntentFactory {

  public static Intent getCallIntent(String number) {
    Intent localIntent = new Intent(Intent.ACTION_CALL);
    localIntent.setData(Uri.parse("tel:" + number));
    return localIntent;
  }

  public static Intent getContactPageIntent(String paramString) {
    Intent localIntent = new Intent(Intent.ACTION_VIEW);
    localIntent.setData(Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, paramString));
    return localIntent;
  }

  public static Intent getDialIntent() {
    return new Intent(Intent.ACTION_DIAL);
  }

  public static Intent getDialIntent(String number) {
    return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
  }

  public static Intent getImageCapureIntent() {
    return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  }

  public static Intent getEmailIntent(String paramString) {
    return new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + paramString));
  }

  private static Intent getNewSMSIntent(String paramString1, String paramString2) {
    Intent localIntent = new Intent(Intent.ACTION_SENDTO);
    localIntent.setType("text/plain");
    if (paramString1 != null) localIntent.setData(Uri.parse("smsto:" + paramString1));
    if (!TextUtils.isEmpty(paramString2)) localIntent.putExtra("sms_body", paramString2);
    return localIntent;
  }

  public static Intent getSkypeIntent(Context paramContext, String paramString) {
    Intent localIntent = new Intent(Intent.ACTION_VIEW);
    localIntent.setData(Uri.parse("skype:" + paramString + "?call&video=false"));
    return localIntent;
  }

  public static Intent getSmsIntent(Context context, String paramString) {
    return getSmsIntent(context, paramString, "");
  }

  public static Intent getBrowserIntent(Context context) {
    String defaultUri = "https://www.google.com";
    Intent intent = new Intent(Intent.ACTION_VIEW,
        ((defaultUri != null) ? Uri.parse(defaultUri) : getDefaultBrowserUri())).addCategory(
        Intent.CATEGORY_BROWSABLE);
    return intent;
  }

  public static Intent getWebSiteIntent(Context context, String url) {
    return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
  }

  private static Uri getDefaultBrowserUri() {
    String url = "https://www.google.com";
    if (url.indexOf("{CID}") != -1) url = url.replace("{CID}", "android-google");
    return Uri.parse(url);
  }

  public static Intent getSmsIntent(Context context, String paramString1, String paramString2) {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setType("vnd.android-dir/mms-sms");
    return intent;
  }

  public static Intent getAccessibilitySettingIntent() {
    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
    return intent;
  }

  public static Intent getNLServiceIntent() {
    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }

  public static Intent getLaunchIntent(String pkgName, String clsName) {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(clsName)) {
      return intent;
    }
    ComponentName componentName = new ComponentName(pkgName, clsName);
    intent.setComponent(componentName);
    return intent;
  }

  public static Intent getImagePickIntent() {
    Intent it = new Intent(Intent.ACTION_PICK);
    it.setAction(Intent.ACTION_GET_CONTENT);
    it.setType("image/*");
    return Intent.createChooser(it, "Choose from...");
  }
}
