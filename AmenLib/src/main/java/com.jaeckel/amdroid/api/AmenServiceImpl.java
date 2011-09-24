package com.jaeckel.amdroid.api;

import com.jaeckel.amdroid.api.model.Amen;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:35 PM
 */
public class AmenServiceImpl implements AmenService {

  final Logger log = LoggerFactory.getLogger("Amen");
  private String authName;
  private String authPassword;
  private String serviceUrl;

  private String csrfToken;
  private String cookie;

  private HttpClient httpclient = new DefaultHttpClient();

  @Override
  public AmenService init(String authName, String authPassword) {

    this.serviceUrl = "https://getamen.com/";
    this.authName = authName;
    this.authPassword = authPassword;

    login(authName, authPassword);
    return this;
  }

  private void login(String authName, String authPassword) {

    prepareLogin();

    signIn(authName, authPassword);

    List<Amen> amens = getFeed();

  }

  private List<Amen> getFeed() {

    ArrayList<Amen> result = new ArrayList<Amen>();

    HttpGet httpGet = new HttpGet(serviceUrl + "amen.json");

    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("_", "" + new Date().getTime()));

    try {
      HttpResponse response = httpclient.execute(httpGet);

      final HttpEntity responseEntity = response.getEntity();

      JSONTokener feedTokener = new JSONTokener(new InputStreamReader(responseEntity.getContent(), "utf-8"));

      log.debug("Parsed JSON: " + feedTokener.toString());

      JSONArray amens = (JSONArray) feedTokener.nextValue();

      log.debug("Array contains " + amens.length() + " amens");
      
      for (int i = 0; i < amens.length(); i++) {

        Amen current = new Amen(amens.getJSONObject(i));

        log.debug("Parsed Amen: " + current);
      }


    } catch (IOException e) {
      throw new RuntimeException("getFeed  failed", e);
    } catch (JSONException e) {
      throw new RuntimeException("parsing feed  failed", e);
    }

    return result;
  }

//  private Amen parseAmen(JSONObject amensJSONObject) throws JSONException {
//
//    log.debug("jsonObject: " + amensJSONObject.toString());
//    Amen amen = new Amen();
//    final String created_at = (String) amensJSONObject.get("created_at");
//    Date date = parseIso8601DateNoBind(created_at);
//    amen.setCreatedAt(date);
//
//    amen.setId(amensJSONObject.getInt("id"));
//    amen.setKindId(amensJSONObject.getInt("kind_id"));
//    amen.setUserId(amensJSONObject.getInt("user_id"));
//
//    User user = parseUser(amensJSONObject.getJSONObject("user"));
//    amen.setUser(user);
//
//    Statement statement = parseStatement(amensJSONObject.getJSONObject("statement"));
//    amen.setStatement(statement);
//    return amen;
//  }

//  private Statement parseStatement(JSONObject statement) {
//    return null;  //To change body of created methods use File | Settings | File Templates.
//  }
//
//  private User parseUser(JSONObject user) {
//      return null;  //To change body of created methods use File | Settings | File Templates.
//  }

//  private Date parseIso8601Date(String dateString, Amen current) {
//
//    Calendar cal = javax.xml.bind.DatatypeConverter.parseDateTime(dateString);
//
//    return cal.getTime();
//  }



  private void signIn(String authName, String authPassword) {

    HttpPost httpPost = new HttpPost(serviceUrl + "sign-in");
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
    nameValuePairs.add(new BasicNameValuePair("utf-8", "✓"));
    nameValuePairs.add(new BasicNameValuePair("authenticity_token", csrfToken));
    nameValuePairs.add(new BasicNameValuePair("user[remember_me]", "" + 1));
    nameValuePairs.add(new BasicNameValuePair("user[email]", authName));
    nameValuePairs.add(new BasicNameValuePair("user[password]", authPassword));
    nameValuePairs.add(new BasicNameValuePair("commit", "Sign in"));

    try {
      httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      HttpResponse response = httpclient.execute(httpPost);
      HttpEntity responseEntity = response.getEntity();

      BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
      String line;
      while ((line = br.readLine()) != null) {

        log.debug(line);
      }

    } catch (IOException e) {

      throw new RuntimeException("sign in failed", e);
    }
  }

  private void prepareLogin() {

    HttpGet httpGet = new HttpGet(serviceUrl);
    try {
      HttpResponse response = httpclient.execute(httpGet);


      Header cookieHeader = response.getFirstHeader("Set-Cookie");
      cookie = extractCookie(cookieHeader.getValue());
      log.debug("cookie: " + cookie);
      final HttpEntity responseEntity = response.getEntity();
      BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
      String line;
      while ((line = br.readLine()) != null) {
        //System.out.println (line);
        if (line.matches(".*name=\"csrf-token\".*")) {

          line = line.replaceFirst(".*content=\"", "");
          line = line.replaceFirst("\" name.*", "");
          log.debug(line);

          csrfToken = line;

          responseEntity.consumeContent();
          break;
        }
      }

    } catch (IOException e) {
      throw new RuntimeException("initial connection failed", e);  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  private String extractCookie(String value) {

    int semicolonIndex = value.indexOf(";");
    return value.substring(0, semicolonIndex);

  }

  public String getCsrfToken() {
    return csrfToken;
  }

//  public void setCsrfToken(String csrfToken) {
//    this.csrfToken = csrfToken;
//  }

  public String getCookie() {
    return cookie;
  }

//  public void setCookie(String cookie) {
//    this.cookie = cookie;
//  }
}

