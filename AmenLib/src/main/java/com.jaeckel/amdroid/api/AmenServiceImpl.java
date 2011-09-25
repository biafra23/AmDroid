package com.jaeckel.amdroid.api;

import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.Dispute;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.api.model.UserInfo;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

//    this.httpclient = WebClientDevWrapper.wrapClient(httpclient);

    login(authName, authPassword);
    return this;
  }

  private void login(String authName, String authPassword) {

    prepareLogin();
    signIn(authName, authPassword);

  }

  @Override
  public List<Amen> getFeed(long sinceId, int limit) {

    ArrayList<Amen> result = new ArrayList<Amen>();

    HashMap<String, String> params = new HashMap<String, String>();
    if (sinceId > 0) {
      params.put("last_amen_id", "" + sinceId);
    }
    params.put("limit", "" + limit);

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "amen.json", params, cookie, csrfToken);

    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      JSONTokener feedTokener = new JSONTokener(responseString);
      log.trace("Parsed JSON: " + feedTokener.toString());
      JSONArray amens = (JSONArray) feedTokener.nextValue();

      log.trace("Array contains " + amens.length() + " amens");
      for (int i = 0; i < amens.length(); i++) {
        Amen current = new Amen(amens.getJSONObject(i));
        log.trace("Parsed Amen: " + current);
        log.trace("JSONed Amen: " + current.json());
        result.add(current);
      }

      responseEntity.consumeContent();

    } catch (IOException e) {
      throw new RuntimeException("getFeed  failed", e);
    } catch (JSONException e) {
      throw new RuntimeException("parsing feed  failed", e);
    }


    return result;
  }

  @Override
  public boolean follow(User u) {


    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean unfollow(User u) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean amen(Amen a) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean dispute(Amen a, String disputeString) {

    try {

      final Dispute dispute = new Dispute(a, "Fnord");

      log.trace("dispute: " + dispute);
      log.trace("dispute: " + dispute.json());

      HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json",
                                                                     dispute.json(),
                                                                     cookie, csrfToken);
      HttpResponse response = httpclient.execute(httpPost);
      HttpEntity responseEntity = response.getEntity();

      BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
      String line;
      while ((line = br.readLine()) != null) {

        log.trace("dispute | " + line);
      }

      responseEntity.consumeContent();

    } catch (IOException e) {

      throw new RuntimeException("dispute failed", e);
    }

    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public long createAmen(Amen a) {

    return 0;
  }

  @Override
  public long takeBack(Amen a) {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public List<Amen> getAmenForUser(User u) {
    return getUserInfo(u).getRecentAmen();
  }

  @Override
  public UserInfo getUserInfo(User u) {
    UserInfo result;

    HashMap<String, String> params = new HashMap<String, String>();

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/users/" + u.getId() + ".json", params, cookie, csrfToken);

    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      JSONTokener feedTokener = new JSONTokener(responseString);
      log.trace("Parsed JSON: " + feedTokener.toString());
      result = new UserInfo((JSONObject) feedTokener.nextValue());

      log.trace("UserInfo: " + result);


      responseEntity.consumeContent();

    } catch (IOException e) {
      throw new RuntimeException("getFeed  failed", e);
    } catch (JSONException e) {
      throw new RuntimeException("parsing feed  failed", e);
    }

    return result;  //To change body of implemented methods use File | Settings | File Templates.
  }

  private String makeStringFromEntity(HttpEntity responseEntity) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
    StringBuilder builder = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null) {

      builder.append(line);
      log.trace("getFeed | " + line);
    }

    return builder.toString();
  }


  public List<Amen> getFeed() {
    return getFeed(0, 25);
  }


  private void signIn(String authName, String authPassword) {


    Map<String, String> params = new HashMap<String, String>();
    params.put("utf-8", "✓");
    params.put("authenticity_token", csrfToken);
    params.put("user[remember_me]", "" + 1);
    params.put("user[email]", authName);
    params.put("user[password]", authPassword);
    params.put("commit", "Sign in");

    try {

      HttpUriRequest httpPost = RequestFactory.createPOSTRequest(serviceUrl + "sign-in", params, cookie, csrfToken);

      HttpResponse response = httpclient.execute(httpPost);
      HttpEntity responseEntity = response.getEntity();

      BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
      String line;
      while ((line = br.readLine()) != null) {

        log.trace("signIn | " + line);
      }

      responseEntity.consumeContent();

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
      log.trace("cookie: " + cookie);
      final HttpEntity responseEntity = response.getEntity();
      BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
      String line;
      while ((line = br.readLine()) != null) {
        //System.out.println (line);
        if (line.matches(".*name=\"csrf-token\".*")) {

          line = line.replaceFirst(".*content=\"", "");
          line = line.replaceFirst("\" name.*", "");
          log.trace(line);

          csrfToken = line;

          responseEntity.consumeContent();
          break;
        }
      }

    } catch (IOException e) {
      throw new RuntimeException("initial connection failed", e);
    }
  }

  private String extractCookie(String value) {

    int semicolonIndex = value.indexOf(";");
    return value.substring(0, semicolonIndex);
  }

  public String getCsrfToken() {
    return csrfToken;
  }

  public String getCookie() {
    return cookie;
  }

  @Override
  public User getMe() {
    User result = null;

    HashMap<String, String> params = new HashMap<String, String>();

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/feed", params, cookie, csrfToken);

    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
      String line;
      while ((line = br.readLine()) != null) {

//        log.trace("getMe | " + line);
        if (line.matches(".*window.loggedInUser.*")) {

          String patternStr = ".*\"id\":(\\d+),.*";
          Pattern pattern = Pattern.compile(patternStr);
          Matcher matcher = pattern.matcher(line);
          boolean matchFound = matcher.find();
          if (matchFound) {
            String idString = matcher.group(1);

            responseEntity.consumeContent();

            log.trace("my id: " + idString);
            UserInfo me = getUserInfo(new User(idString));
            result = new User(me);

            break;
          }


        }
      }
      if (result == null) {
        responseEntity.consumeContent();
      }


    } catch (IOException e) {
      throw new RuntimeException("getMe  failed", e);

    }

    return result;
  }
}

