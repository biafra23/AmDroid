package com.jaeckel.amdroid.api;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 12:52 AM
 */
public class RequestFactory {

  public static HttpUriRequest createPOSTRequest(String serviceUrl, Map<String, String> params, String cookie, String csrfToken) {


    HttpPost httpPost = new HttpPost(serviceUrl);

    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
    for (String key : params.keySet()) {
      nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
    }
    httpPost.addHeader("X-CSRF-Token", csrfToken);
    httpPost.addHeader("Cookie", cookie);

    try {

      httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Unsupported Encoding", e);
    }

    return httpPost;
  }

  public static HttpUriRequest createJSONPOSTRequest(String serviceUrl, String body, String cookie, String csrfToken) {

    HttpPost httpPost = new HttpPost(serviceUrl);

    httpPost.addHeader("X-CSRF-Token", csrfToken);
    httpPost.addHeader("Cookie", cookie);
    httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
    httpPost.addHeader("X-Requested-With", "XMLHttpRequest");

    try {

      httpPost.setEntity(new ByteArrayEntity(body.getBytes("UTF8")));

    } catch (UnsupportedEncodingException e) {

      throw new RuntimeException("Unsupported Encoding", e);
    }

    return httpPost;
  }

  public static HttpUriRequest createGETRequest(String serviceUrl, Map<String, String> params, String cookie, String csrfToken) {

    StringBuilder nameValuePairs = new StringBuilder();
    nameValuePairs.append("?_=" + new Date().getTime());
    if (params != null) {
      for (String key : params.keySet()) {
        nameValuePairs.append("&")
                      .append(key)
                      .append("=")
                      .append(URLEncoder.encode(params.get(key)));
      }
    }
    HttpGet httpGet = new HttpGet(serviceUrl + nameValuePairs.toString());
    httpGet.addHeader("X-CSRF-Token", csrfToken);
    httpGet.addHeader("Cookie", cookie);

    return httpGet;

  }
  
  public static HttpUriRequest createDELETERequest(String serviceUrl, Map<String, String> params, String cookie, String csrfToken) {

      StringBuilder nameValuePairs = new StringBuilder();
//      nameValuePairs.append("?_=" + new Date().getTime());
      if (params != null) {
        for (String key : params.keySet()) {
          nameValuePairs.append("&")
                        .append(key)
                        .append("=")
                        .append(URLEncoder.encode(params.get(key)));
        }
      }
      HttpDelete httpDelete = new HttpDelete(serviceUrl + nameValuePairs.toString());
      httpDelete.addHeader("X-CSRF-Token", csrfToken);
      httpDelete.addHeader("Cookie", cookie);

      return httpDelete;

    }
}
