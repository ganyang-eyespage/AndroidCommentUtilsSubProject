package com.eyespage.utils;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by jerome on 12/3/14.
 */
public class JsonUtil {
  private static JsonUtil instance;
  private ObjectMapper mapper;

  private JsonUtil() {
    mapper = new ObjectMapper();
    mapper.setDeserializationConfig(mapper.getDeserializationConfig().without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES));
  }

  public static JsonUtil get() {
    synchronized (JsonUtil.class) {
      if (instance == null) instance = new JsonUtil();
      return instance;
    }
  }

  public ObjectMapper getMapper() {
    return mapper;
  }
}
