package com.jaeckel.amdroid.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jaeckel.amdroid.api.model.Amen;
import com.jaeckel.amdroid.api.model.DateSerializer;
import com.jaeckel.amdroid.api.model.Objekt;
import com.jaeckel.amdroid.api.model.Statement;
import com.jaeckel.amdroid.api.model.Topic;
import com.jaeckel.amdroid.api.model.User;
import com.jaeckel.amdroid.api.model.UserInfo;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

  private Gson gson = new GsonBuilder()
    .registerTypeAdapter(Date.class, new DateSerializer())
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .serializeNulls()
    .create();

  @Override
  public AmenService init(String authName, String authPassword) {
    log.debug("init");
    this.serviceUrl = "https://getamen.com/";
    this.authName = authName;
    this.authPassword = authPassword;

//    this.httpclient = WebClientDevWrapper.wrapClient(httpclient);

    login(authName, authPassword);
    return this;
  }

  private void login(String authName, String authPassword) {
    log.debug("login");
    prepareLogin();
    signIn(authName, authPassword);

  }

  @Override
  public List<Amen> getFeed(long sinceId, int limit) {
    log.debug("getFeed");
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

      Type collectionType = new TypeToken<List<Amen>>() {
      }.getType();
      result = gson.fromJson(responseString, collectionType);

    } catch (IOException e) {
      throw new RuntimeException("getFeed  failed", e);
    }


    return result;
  }

  @Override
  public boolean follow(User u) {
    log.debug("follow");

    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean unfollow(User u) {
    log.debug("unfollow");
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Amen amen(Long amenId) {
    log.debug("amen(Long)");
    Amen a = null;
    String json = "{\"referring_amen_id\":" + amenId + ",\"kind_id\":1}";


    HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json",
                                                                   json,
                                                                   cookie, csrfToken);
    try {
      HttpResponse response = httpclient.execute(httpPost);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      a = gson.fromJson(responseString, Amen.class);

    } catch (IOException e) {

      throw new RuntimeException("Amening failed", e);

    }

    return a;
  }

  @Override
  public Amen amen(Statement statement) {
    log.debug("amen(Statement)");
    Amen a = null;
    String json = "{\"referring_amen_id\":" + statement.getId() + ",\"kind_id\":1}";


    HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json",
                                                                   json,
                                                                   cookie, csrfToken);
    try {
      HttpResponse response = httpclient.execute(httpPost);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);
      a = gson.fromJson(responseString, Amen.class);

    } catch (IOException e) {

      throw new RuntimeException("Amening failed", e);

    }

    return a;
  }

  @Override
  public Long dispute(Amen dispute) {
    log.debug("dispute");
    try {

      log.trace("       dispute: " + dispute);
      log.trace("dispute.json(): " + dispute.json());

      HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json",
                                                                     dispute.json(),
                                                                     cookie, csrfToken);
      HttpResponse response = httpclient.execute(httpPost);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      log.trace("dispute: responseString: " + responseString);

      Amen a = gson.fromJson(responseString, Amen.class);

      if (a != null && a.getKindId() == AMEN_KIND_DISPUTE) {

        final boolean sameObjekt = a.getStatement().getObjekt().getName().equals( dispute.getStatement().getObjekt().getName());

        if (sameObjekt) {
          return a.getId();
        } else {
          log.trace("Not the same Objekt");
        }

      } else {
        return null;
      }

    } catch (IOException e) {

      throw new RuntimeException("dispute failed", e);
    }
    return null;
  }

  @Override
  public void addStatement(Statement statement) {
    log.debug("addStatement");
    final String body = new Amen(statement).json();
    log.trace("Body: " + body);

//thing
//{"statement":{"objekt":{"name":"Eureka","kind_id":2},"topic":{"best":true,"description":"TV Program","scope":"Ever"}},"kind_id":0}
//
// //place
//{"statement":{"objekt":{"key":["foursquare","4c8243eedc018cfa3c37cd6c"],"name":"Suppe & Mucke","kind_id":1},"topic":{"best":true,"description":"having Suppe and Mucke","scope":"in Berlin"}},"kind_id":0}

//person
//{"statement":{"objekt":{"key":["freebase","/en/charlie_sheen"],"name":"Charlie Sheen","kind_id":0},"topic":{"best":true,"description":"Actor","scope":"Ever"}},"kind_id":0}

    try {
      HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json",
                                                                     body,
                                                                     cookie, csrfToken);
      HttpResponse response = httpclient.execute(httpPost);
      HttpEntity responseEntity = response.getEntity();

      BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
      String line;
      while ((line = br.readLine()) != null) {
        log.trace("addStatement | " + line);
      }

      responseEntity.consumeContent();

    } catch (IOException e) {

      throw new RuntimeException("addStatement failed", e);
    }


  }

  @Override
  public Amen getAmenForId(Long id) {
    log.debug("getAmenForId");
    Amen amen;
    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/amen/" + id + ".json", null, cookie, csrfToken);
    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);
      amen = gson.fromJson(responseString, Amen.class);

    } catch (Exception e) {
      throw new RuntimeException("getAmenForId(" + id + ") failed", e);
    }

    return amen;
  }

  @Override
  public Statement getStatementForId(Long id) {
    log.debug("getStatementForId");
    Statement statement;
    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/statements/" + id + ".json", null, cookie, csrfToken);
    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      statement = gson.fromJson(responseString, Statement.class);

    } catch (Exception e) {
      throw new RuntimeException("getAmenForId(" + id + ") failed", e);
    }

    return statement;
  }

  @Override
  public Topic getTopicsForId(Long id) {

    log.debug("getTopicsForId");
    Topic topic;

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/topics/" + id + ".json", null, cookie, csrfToken);

    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      topic = gson.fromJson(responseString, Topic.class);

    } catch (Exception e) {
      throw new RuntimeException("getTopicsForId(" + id + ") failed", e);
    }

    return topic;
  }

  @Override
  public boolean takeBack(Long statementId) {
    log.debug("takeBack(): statementId: " + statementId);
    boolean result = false;

    final String url = serviceUrl + "amen/" + statementId + ".json";
    log.trace("DELETE " + url);
    HttpUriRequest httpDelete = RequestFactory.createDELETERequest(url, null,
                                                                   cookie, csrfToken);

    log.trace("httpDelete: " + httpDelete);

    try {

      HttpResponse response = httpclient.execute(httpDelete);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      if (" ".equals(responseString)) {
        result = true;
      }

    } catch (IOException e) {
      throw new RuntimeException("takeBack  failed", e);
    }

    return result;
  }

  @Override
  public List<Amen> getAmenForUser(Long userId) {
    log.debug("getAmenForUser(User)");
    return getUserInfo(userId).getRecentAmen();
  }

  @Override
  public UserInfo getUserInfo(Long userId) {
    log.debug("getUserInfo(User)");
    UserInfo result;

    HashMap<String, String> params = new HashMap<String, String>();

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/users/" + userId + ".json", params, cookie, csrfToken);

    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);
      result = gson.fromJson(responseString, UserInfo.class);

    } catch (IOException e) {
      throw new RuntimeException("getUserInfo  failed", e);
    }

    return result;  //To change body of implemented methods use File | Settings | File Templates.
  }

  private String makeStringFromEntity(HttpEntity responseEntity) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
    StringBuilder builder = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null) {

      builder.append(line);
      log.trace("makeStringFromEntity | " + line);
    }

    return builder.toString();
  }


  public List<Amen> getFeed() {
    return getFeed(0, 25);
  }


  private void signIn(String authName, String authPassword) {
    log.debug("signIn()");

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
    log.debug("prepareLogin()");
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

    log.debug("getMe()");
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
            UserInfo me = getUserInfo(Long.valueOf(idString));
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

  public List<User> followers(Long id) {

    List<User> result = new ArrayList<User>();
    log.debug("followers()");
    HashMap<String, String> params = new HashMap<String, String>();
//    params.put("limit", "" + 40);
    params.put("last_user_id", "" + 11181);
    //https://getamen.com/users/12665/followers.json
    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/users/" + id + "/followers.json", params, cookie, csrfToken);

    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      Type collectionType = new TypeToken<Collection<User>>() {
      }.getType();
      result = gson.fromJson(responseString, collectionType);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return result;
  }

  public List<User> following(Long id) {

    List<User> result = new ArrayList<User>();
    log.debug("followers()");
    HashMap<String, String> params = new HashMap<String, String>();
    //    params.put("limit", "" + 40);
//        params.put("last_user_id", "" + 11181);
    //https://getamen.com/users/12665/followers.json
    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/users/" + id + "/following.json", params, cookie, csrfToken);

    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      Type collectionType = new TypeToken<Collection<User>>() {
      }.getType();
      result = gson.fromJson(responseString, collectionType);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return result;
  }

  public List<Objekt> objektsForQuery(CharSequence query, int kindId, Double lat, Double lon) {
    List<Objekt> result = null;
    log.debug("followers()");
    HashMap<String, String> params = new HashMap<String, String>();
    if (query != null) {
      params.put("q", query.toString());
    }

    params.put("kind_id", "" + kindId);
    if (lat != null) {
      params.put("lat", "" + lat);
    }
    if (lon != null) {
      params.put("lng", "" + lon);
    }


    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/objekts.json", params, cookie, csrfToken);

    try {

      HttpResponse response = httpclient.execute(httpGet);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      Type collectionType = new TypeToken<Collection<Objekt>>() {
      }.getType();
      result = gson.fromJson(responseString, collectionType);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return result;
  }
}

