package com.jaeckel.amdroid.api;


import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 12:52 AM
 */
public class RequestFactory {

  final static Logger log = LoggerFactory.getLogger("Amen");

  public static HttpUriRequest createJSONPOSTRequest(String serviceUrl, String body) {

    HttpPost httpPost = new HttpPost(serviceUrl);

    httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
    httpPost.addHeader("X-Requested-With", "XMLHttpRequest");

    try {

      httpPost.setEntity(new ByteArrayEntity(body.getBytes("UTF8")));

    } catch (UnsupportedEncodingException e) {

      throw new RuntimeException("Unsupported Encoding", e);
    }

    return httpPost;
  }

  public static HttpUriRequest createGETRequest(String serviceUrl, Map<String, String> params) {

    String queryString = createQueryString(params, false);
    HttpGet httpGet = new HttpGet(serviceUrl + queryString);
    log.debug("createGETRequest: " + httpGet.getURI());
    return httpGet;

  }

  private static String createQueryString(Map<String, String> params, boolean addTimeStamp) {
    StringBuilder nameValuePairs = new StringBuilder();
    nameValuePairs.append("?");
    if (params != null) {
      for (String key : params.keySet()) {
        nameValuePairs.append("&")
                      .append(key)
                      .append("=")
                      .append(URLEncoder.encode(params.get(key)));
      }
    }
    if (addTimeStamp) {
      nameValuePairs.append("&_=" + new Date().getTime());
    }
    return nameValuePairs.toString();
  }

  public static HttpUriRequest createDELETERequest(String serviceUrl, Map<String, String> params) {

    String queryString = createQueryString(params, false);

    HttpDelete httpDelete = new HttpDelete(serviceUrl + queryString);

    return httpDelete;

  }
}
