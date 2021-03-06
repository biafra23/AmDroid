package com.jaeckel.amenoid.api;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.entity.ByteArrayEntity;

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
      log.debug("createJSONPOSTRequest ->  url: " + serviceUrl);
      log.trace("createJSONPOSTRequest -> Body: " + body);
      httpPost.setEntity(new ByteArrayEntity(body.getBytes("UTF8")));

    } catch (UnsupportedEncodingException e) {

      throw new RuntimeException("Unsupported Encoding", e);
    }

    return httpPost;
  }

  public static HttpUriRequest createGETRequest(String serviceUrl, Map<String, String> params) {

    String queryString = createQueryString(params, false);
    HttpGet httpGet = new HttpGet(serviceUrl + queryString);
    log.debug("RequestFactory | createGETRequest: " + httpGet.getURI());
    return httpGet;

  }

  private static String createQueryString(Map<String, String> params, boolean addTimeStamp) {
    StringBuilder nameValuePairs = new StringBuilder();
    if (addTimeStamp || params != null) {
      nameValuePairs.append("?");
    }
    if (params != null) {
      for (String key : params.keySet()) {
        final String value = params.get(key);
        if (value == null) {

          log.error("Value for key " + key + " was null");

        } else {
          nameValuePairs.append("&")
                        .append(key)
                        .append("=")
                        .append(URLEncoder.encode(value));
        }
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
