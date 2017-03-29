package com.eyespage.utils;

import android.text.TextUtils;
import android.util.Base64;
import com.eyespage.lib.log.Log;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {
  public static final String TAG = "Encrypt";
  public static final String DEFAULT_CHARSET = "UTF-8";
  public static final String LAUNCHER_AES_KEY = "8b318785b67d4ae69f92ec14192a5436";
  private static final String AES_KEY = "30069bb8c011423e049dfce3a8d21b45";
  private static final String AES_ENCRYPT_MODE = "AES/CBC/PKCS5Padding";

  /**
   * generate a random aes key
   *
   * @throws NoSuchAlgorithmException
   */
  public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
    KeyGenerator kgen = KeyGenerator.getInstance("AES");
    kgen.init(256);
    return kgen.generateKey();
  }

  public static String getAESKey() {
    return AES_KEY;
  }

  public static String base64Encode(String raw) {
    if (!TextUtils.isEmpty(raw)) {
      return Base64.encodeToString(raw.getBytes(), Base64.DEFAULT);
    }
    return "";
  }
  // ============================================ encrypt ============================================ //

  /**
   * 当前使用的加密
   * AES加密
   *
   * @param encryptBytes 待加密的byte[]
   * @return 加密后的byte[]
   * @throws Exception
   */
  public static byte[] aesEncrypt(byte[] encryptBytes) throws Exception {
    return aesEncrypt(encryptBytes, getAESKey(), generateIV());
  }

  public static byte[] aesEncryptByKey(byte[] encryptBytes, String key) throws Exception {
    return aesEncrypt(encryptBytes, key, generateIV());
  }

  /**
   * AES加密
   *
   * @param encryptBytes 待加密的byte[]
   * @return 加密后的byte[]
   * @throws Exception
   */
  public static byte[] aesEncrypt(byte[] encryptBytes, byte[] iv) throws Exception {
    return aesEncrypt(encryptBytes, getAESKey(), iv);
  }

  /**
   * AES加密
   *
   * @param encryptBytes 待加密的byte[]
   * @param encryptKey 加密密钥
   * @return 加密后的byte[]
   * @throws Exception
   */
  public static byte[] aesEncrypt(byte[] encryptBytes, String encryptKey) throws Exception {
    return aesEncrypt(encryptBytes, encryptKey, null);
  }

  /**
   * AES加密
   *
   * @param content 待加密的内容
   * @param encryptKey 加密密钥
   * @param IV iv
   * @return 加密后的byte[]
   * @throws Exception
   */
  public static byte[] aesEncrypt(byte[] content, String encryptKey, byte[] IV) throws Exception {
    Cipher cipher = Cipher.getInstance(AES_ENCRYPT_MODE);
    if (IV != null) {
      IvParameterSpec ips = new IvParameterSpec(IV);
      cipher.init(Cipher.ENCRYPT_MODE,
          new SecretKeySpec(encryptKey.getBytes(defaultCharset()), "AES"), ips);
      // we should add our iv ahead
      byte[] rawContent = cipher.doFinal(content);
      byte[] result = new byte[rawContent.length + IV.length];
      System.arraycopy(IV, 0, result, 0, IV.length);
      System.arraycopy(rawContent, 0, result, IV.length, rawContent.length);
      return result;
    } else {
      cipher.init(Cipher.ENCRYPT_MODE,
          new SecretKeySpec(encryptKey.getBytes(defaultCharset()), "AES"));
      return cipher.doFinal(content);
    }
  }

  // ============================================ decrypt ============================================ //

  /**
   * * 当前使用的加密
   * AES解密
   *
   * @param decryptBytes 待解密的byte[]
   * @return 解密后的byte[]
   * @throws Exception
   */
  public static byte[] aesDecrypt(byte[] decryptBytes) throws Exception {
    return aesDecrypt(decryptBytes, true);
  }

  /**
   * AES解密
   *
   * @param encryptBytes 待解密的byte[]
   * @return 解密后的byte[]
   * @throws Exception
   */
  public static byte[] aesDecrypt(byte[] encryptBytes, byte[] iv) throws Exception {
    return aesDecrypt(encryptBytes, getAESKey(), iv);
  }

  /**
   * AES解密
   *
   * @param encryptBytes 待解密的byte[]
   * @param decryptKey 解密密钥
   * @return 解密后的byte[]
   * @throws Exception
   */
  public static byte[] aesDecrypt(byte[] encryptBytes, String decryptKey) throws Exception {
    return aesDecrypt(encryptBytes, decryptKey, null);
  }

  /**
   * AES解密
   *
   * @param decryptBytes 待解密的byte[]
   * @return 解密后的byte[]
   * @throws Exception
   */
  public static byte[] aesDecrypt(byte[] decryptBytes, boolean hasIV) throws Exception {
    if (hasIV) {
      byte[] iv = new byte[16];
      byte[] data = new byte[decryptBytes.length - iv.length];
      System.arraycopy(decryptBytes, 0, iv, 0, iv.length);
      System.arraycopy(decryptBytes, iv.length, data, 0, data.length);
      return aesDecrypt(data, getAESKey(), iv);
    }
    return aesDecrypt(decryptBytes, getAESKey(), null);
  }

  /**
   * AES解密
   *
   * @param decryptBytes 待解密的byte[]
   * @param decryptKey 解密密钥
   * @return 解密后的byte[]
   * @throws Exception
   */
  public static byte[] aesDecrypt(byte[] decryptBytes, String decryptKey, byte[] IV)
      throws Exception {
    Cipher cipher = Cipher.getInstance(AES_ENCRYPT_MODE);
    if (IV != null) {
      IvParameterSpec ips = new IvParameterSpec(IV);
      cipher.init(Cipher.DECRYPT_MODE,
          new SecretKeySpec(decryptKey.getBytes(defaultCharset()), "AES"), ips);
    } else {
      cipher.init(Cipher.DECRYPT_MODE,
          new SecretKeySpec(decryptKey.getBytes(defaultCharset()), "AES"));
    }
    return cipher.doFinal(decryptBytes);
  }

  private static byte[] generateIV() throws NoSuchAlgorithmException {
    final byte[] nonce = new byte[16];
    Random rand = SecureRandom.getInstance("SHA1PRNG");
    rand.nextBytes(nonce);
    return nonce;
  }

  private static Charset defaultCharset() {
    try {
      return Charset.forName(DEFAULT_CHARSET);
    } catch (UnsupportedCharsetException e) {
      return Charset.defaultCharset();
    }
  }

  public static byte[] double2ByteArray(double value) {
    byte[] bytes = new byte[8];
    ByteBuffer.wrap(bytes).putDouble(value);
    return bytes;
  }

  public static double byteArr2Double(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getDouble();
  }

  public static byte[] long2ByteArray(long value) {
    byte[] bytes = new byte[8];
    ByteBuffer.wrap(bytes).putLong(value);
    return bytes;
  }

  public static long byteArr2Long(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getLong();
  }

  public static byte[] int2ByteArray(int value) {
    byte[] bytes = new byte[4];
    ByteBuffer.wrap(bytes).putInt(value);
    return bytes;
  }

  public static int byteArr2Int(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getInt();
  }

  public static byte[] aesEncryptDouble(double d) throws Exception {
    return aesEncrypt(double2ByteArray(d));
  }

  public static double aesDecryptDouble(byte[] arr) throws Exception {
    return byteArr2Double(aesDecrypt(arr));
  }

  public static byte[] aesEncryptLong(long l) throws Exception {
    return aesEncrypt(long2ByteArray(l));
  }

  public static long aesDecryptLong(byte[] arr) throws Exception {
    return byteArr2Long(aesDecrypt(arr));
  }

  public static byte[] aesEncryptInt(int i) throws Exception {
    return aesEncrypt(int2ByteArray(i));
  }

  public static int aesDecryptInt(byte[] arr) throws Exception {
    return byteArr2Int(aesDecrypt(arr));
  }

  public static byte[] aesDecryptByKey(byte[] decryptBytes, String key) throws Exception {
    byte[] iv = new byte[16];
    byte[] data = new byte[decryptBytes.length - iv.length];
    System.arraycopy(decryptBytes, 0, iv, 0, iv.length);
    System.arraycopy(decryptBytes, iv.length, data, 0, data.length);
    return aesDecrypt(data, key, iv);
  }

  public static void main(String[] args) {
    String json =
        "{\"adid\":\"3687f66d-85d9-412a-8239-e34295e4e702\",\"events\":[{\"apps\":[{\"appname\":\"相册\",\"bundleid\":\"com.sec.android.gallery3d\"},{\"appname\":\"Play 商店\",\"bundleid\":\"com.android.vending\"},{\"appname\":\"S 健康\",\"bundleid\":\"com.sec.android.app.shealth\"},{\"appname\":\"音乐\",\"bundleid\":\"com.sec.android.app.music\"},{\"appname\":\"相机\",\"bundleid\":\"com.sec.android.app.camera\"},{\"appname\":\"联系人\",\"bundleid\":\"com.android.contacts\"},{\"appname\":\"电话\",\"bundleid\":\"com.android.contacts\"},{\"appname\":\"电子邮件\",\"bundleid\":\"com.samsung.android.email.provider\"},{\"appname\":\"信息\",\"bundleid\":\"com.android.mms\"},{\"appname\":\"设置\",\"bundleid\":\"com.android.settings\"},{\"appname\":\"移动热点\",\"bundleid\":\"com.android.settings\"},{\"appname\":\"紧急警报\",\"bundleid\":\"com.sec.android.app.cmas\"},{\"appname\":\"日历\",\"bundleid\":\"com.android.calendar\"},{\"appname\":\"语音邮件\",\"bundleid\":\"com.samsung.vvm\"},{\"appname\":\"YouTube\",\"bundleid\":\"com.google.android.youtube\"},{\"appname\":\"豌豆荚\",\"bundleid\":\"com.wandoujia.phoenix2\"},{\"appname\":\"百度地图\",\"bundleid\":\"com.baidu.BaiduMap\"},{\"appname\":\"饿了么\",\"bundleid\":\"me.ele\"},{\"appname\":\"Chrome Dev\",\"bundleid\":\"com.chrome.dev\"},{\"appname\":\"Chrome Beta\",\"bundleid\":\"com.chrome.beta\"},{\"appname\":\"亚马逊购物\",\"bundleid\":\"com.amazon.mShop.android\"},{\"appname\":\"Samsung Milk Music\",\"bundleid\":\"com.samsung.mdl.radio\"},{\"appname\":\"S Voice\",\"bundleid\":\"com.samsung.voiceserviceplatform\"},{\"appname\":\"计算器\",\"bundleid\":\"com.sec.android.app.popupcalculator\"},{\"appname\":\"我的文件\",\"bundleid\":\"com.sec.android.app.myfiles\"},{\"appname\":\"录音机\",\"bundleid\":\"com.sec.android.app.voicenote\"},{\"appname\":\"视频\",\"bundleid\":\"com.samsung.android.video\"},{\"appname\":\"时钟\",\"bundleid\":\"com.sec.android.app.clockpackage\"},{\"appname\":\"go90\",\"bundleid\":\"com.verizonmedia.go90.enterprise\"},{\"appname\":\"安装向导\",\"bundleid\":\"com.sec.android.app.setupwizard\"},{\"appname\":\"帮助\",\"bundleid\":\"com.samsung.helphub\"},{\"appname\":\"备忘录\",\"bundleid\":\"com.samsung.android.app.memo\"},{\"appname\":\"Peel Smart Remote\",\"bundleid\":\"tv.peel.app\"},{\"appname\":\"Caller Name ID\",\"bundleid\":\"com.cequint.ecid\"},{\"appname\":\"爱奇艺\",\"bundleid\":\"com.qiyi.video\"},{\"appname\":\"携程旅行\",\"bundleid\":\"ctrip.android.view\"},{\"appname\":\"大众点评\",\"bundleid\":\"com.dianping.v1\"},{\"appname\":\"高德地图\",\"bundleid\":\"com.autonavi.minimap\"},{\"appname\":\"Vysor\",\"bundleid\":\"com.koushikdutta.vysor\"},{\"appname\":\"百度糯米\",\"bundleid\":\"com.nuomi\"},{\"appname\":\"美团外卖\",\"bundleid\":\"com.sankuai.meituan.takeoutnew\"},{\"appname\":\"美团\",\"bundleid\":\"com.sankuai.meituan\"},{\"appname\":\"猫眼电影\",\"bundleid\":\"com.sankuai.movie\"},{\"appname\":\"百度外卖\",\"bundleid\":\"com.baidu.lbs.waimai\"},{\"appname\":\"豆果美食\",\"bundleid\":\"com.douguo.recipe\"},{\"appname\":\"豆瓣\",\"bundleid\":\"com.douban.frodo\"},{\"appname\":\"娱票儿\",\"bundleid\":\"com.tencent.movieticket\"},{\"appname\":\"淘票票\",\"bundleid\":\"com.taobao.movie.android\"},{\"appname\":\"酷狗音乐\",\"bundleid\":\"com.kugou.android\"},{\"appname\":\"网易电影\",\"bundleid\":\"com.netease.movie\"},{\"appname\":\"艺龙酒店\",\"bundleid\":\"com.elong.hotel.ui\"},{\"appname\":\"虾米音乐\",\"bundleid\":\"fm.xiami.main\"},{\"appname\":\"支付宝\",\"bundleid\":\"com.eg.android.AlipayGphone\"},{\"appname\":\"Lifon\",\"bundleid\":\"com.eyespage.lifon\"}],\"type\":4}]}";
    try {
      byte[] body =
          EncryptUtil.aesEncryptByKey(json.getBytes("UTF-8"),EncryptUtil.LAUNCHER_AES_KEY);
      byte[] response = EncryptUtil.aesDecrypt(body);
      Log.d(TAG, "reverse=" + new String(response));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

