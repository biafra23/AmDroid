package com.jaeckel.amenoid.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.DateSerializer;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.api.model.ServerError;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.Topic;
import com.jaeckel.amenoid.api.model.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:35 PM
 */
public class AmenServiceImpl implements AmenService {

  private final static Logger log        = LoggerFactory.getLogger("Amen");
  private final static String serviceUrl = "https://getamen.com/";
//  private final static String serviceUrl = "https://staging.getamen.com/";

  private String authName;
  private String authPassword;

  private User        me;
  private ServerError lastError;
  private String      authToken;

  private HttpClient httpclient;

  public AmenServiceImpl() {
    HttpParams params = new BasicHttpParams();
    params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
    params.setParameter(CoreProtocolPNames.USER_AGENT, "Amenoid/1.0 HttpClient/4.0.1 Android");

    final SchemeRegistry schemeRegistry = new SchemeRegistry();
    Scheme scheme = new Scheme("https", SSLSocketFactory.getSocketFactory(), 443);
    schemeRegistry.register(scheme);

    httpclient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);

  }

  private Gson gson = new GsonBuilder()
    .registerTypeAdapter(Date.class, new DateSerializer())
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .serializeNulls()
    .create();

  @Override
  public AmenService init(String authName, String authPassword) throws IOException {
    log.debug("init");

    this.authName = authName;
    this.authPassword = authPassword;

    User result = authenticate(authName, authPassword);
    if (result == null) {
      log.error("authentication failed");
    } else {
      me = result;
    }
    return this;
  }

  @Override
  public AmenService init(String authToken, User me) {

    this.me = me;
    this.authToken = authToken;
    return this;  //To change body of implemented methods use File | Settings | File Templates.
  }


  private User authenticate(String authName, String authPassword) throws IOException {

    User user;
    String authJSON = "{\"password\":\"" + authPassword + "\",\"email\":\"" + authName + "\"}";

//    log.trace("authJSON: " + authJSON);

    HttpPost httpPost = new HttpPost(serviceUrl + "authentication.json");
//    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-Type", "application/json");

    try {

      httpPost.setEntity(new ByteArrayEntity(authJSON.getBytes("UTF8")));


      HttpResponse response = httpclient.execute(httpPost);
      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);
      if (responseString.startsWith("{\"error\"")) {
        //TODO: rethink this! will cause trouble in multi threaded environment. What would Buddha recommend? An Exception?
        lastError = gson.fromJson(responseString, ServerError.class);
        throw new InvalidCredentialsException(lastError.getError());
      }
      user = gson.fromJson(responseString, User.class);
      authToken = user.getAuthToken();

    } catch (UnsupportedEncodingException e) {

      throw new RuntimeException("Unsupported Encoding", e);
    } catch (ClientProtocolException e) {

      throw new RuntimeException("Exception while authenticating", e);
    } catch (IOException e) {

      throw new IOException(e);
    }
    return user;
  }

  public List<Amen> getFeed(int type) throws IOException {
    return getFeed(0, 25, type);
  }

  @Override
  public List<Amen> getFeed(long sinceId, int limit, int type) throws IOException {
    return getFeed(0, sinceId, limit, type);
  }

  public List<Amen> getFeed(long beforeId, long sinceId, int limit, int type) throws IOException {
    log.debug("getFeed");
    ArrayList<Amen> result = new ArrayList<Amen>();

    HashMap<String, String> params = createAuthenticatedParams();
    if (sinceId > 0) {
      params.put("last_amen_id", "" + sinceId);
    }
    if (sinceId > 0) {
      params.put("first_amen_id", "" + beforeId);
    }
    params.put("limit", "" + limit);

    String interesting = "";
    if (type == FEED_TYPE_RECENT) {
      interesting = "/recent";
    } else if (type == FEED_TYPE_POPULAR) {
      interesting = "/popular";
    }
    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "amen" + interesting + ".json", params);


    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("getFeed produced error: " + responseString);
    }
    Type collectionType = new TypeToken<List<Amen>>() {
    }.getType();
    result = gson.fromJson(responseString, collectionType);


    return result;
  }

  @Override
  public boolean follow(User u) throws IOException {

    log.debug("follow");
    boolean result = false;

    String json = "{\"user_id\":" + u.getId() + ",\"kind_id\":1, \"auth_token\":\"" + authToken + "\"}";

    HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "follows.json", json);

    HttpResponse response = httpclient.execute(httpPost);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("follow produced error: " + responseString);
    }

    if (" ".equals(responseString)) {
      result = true;
    }


    return result;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean unfollow(User u) throws IOException {

    log.debug("unfollow");
    boolean result = false;

    Map<String, String> params = createAuthenticatedParams();

    HttpUriRequest httpDelete = RequestFactory.createDELETERequest(serviceUrl + "follows/" + u.getId() + ".json", params);

    HttpResponse response = httpclient.execute(httpDelete);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);

    if (" ".equals(responseString)) {
      result = true;
    }

    return result;
  }

  @Override
  public Amen amen(Long amenId) throws IOException {
    log.debug("amen(" + amenId + ")");
    Amen a = null;
    String json = "{\"referring_amen_id\":" + amenId + ",\"kind_id\":1, \"auth_token\":\"" + authToken + "\"}";


    HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json", json);
    HttpResponse response = httpclient.execute(httpPost);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("amen produced error: " + responseString);
    }

    a = gson.fromJson(responseString, Amen.class);


    return a;
  }

  @Override
  public Amen amen(Statement statement) throws IOException {
    log.debug("amen(Statement)");

    Amen a = null;
    String json = "{\"referring_amen_id\":" + statement.getId() + ",\"kind_id\":1, \"auth_token\":\"" + authToken + "\"}";


    HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json", json);

    HttpResponse response = httpclient.execute(httpPost);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("amen produced error: " + responseString);
    }
    a = gson.fromJson(responseString, Amen.class);


    return a;
  }

  @Override
  public Long dispute(Amen dispute) throws IOException {

    HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json", addAuthTokenToJSON(dispute, authToken));
    HttpResponse response = httpclient.execute(httpPost);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);

    log.trace("dispute: responseString: " + responseString);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("dispute produced error: " + responseString);
    }
    Amen a = gson.fromJson(responseString, Amen.class);

    if (a != null && a.getKindId() == AMEN_KIND_DISPUTE) {

      final boolean sameObjekt = a.getStatement().getObjekt().getName().equals(dispute.getStatement().getObjekt().getName());

      if (sameObjekt) {
        return a.getId();
      } else {
        log.trace("Not the same Objekt");
      }

    } else {
      return null;
    }


    return null;
  }

  @Override
  public Amen addStatement(Statement statement) throws IOException {

    final String body = addAuthTokenToJSON(new Amen(statement), authToken);

    HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json", body);
    HttpResponse response = httpclient.execute(httpPost);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException(responseString);
    }

    return gson.fromJson(responseString, Amen.class);
  }

  @Override
  public Amen getAmenForId(Long id) throws IOException {

    Amen amen;
    HashMap<String, String> params = createAuthenticatedParams();
    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/amen/" + id + ".json", params);

    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("getAmenForId produced error: " + responseString);
    }
    amen = gson.fromJson(responseString, Amen.class);

    return amen;
  }

  @Override
  public Statement getStatementForId(Long id) throws IOException {
    log.debug("getStatementForId");
    Statement statement;
    HashMap<String, String> params = createAuthenticatedParams();
    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/statements/" + id + ".json", params);

    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);

    statement = gson.fromJson(responseString, Statement.class);

    return statement;
  }

  @Override
  public Topic getTopicsForId(Long id, Long includeStatementId) throws IOException {

    log.debug("getTopicsForId");
    Topic topic;
    HashMap<String, String> params = createAuthenticatedParams();
    if (includeStatementId != null) {
      params.put("include_statement_id", "" + includeStatementId);
    }

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/topics/" + id + ".json", params);

    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);

    topic = gson.fromJson(responseString, Topic.class);


    return topic;
  }

  @Override
  public boolean takeBack(Long statementId) throws IOException {
    log.debug("takeBack(): statementId: " + statementId);
    boolean result = false;
    HashMap<String, String> params = createAuthenticatedParams();

    final String url = serviceUrl + "amen/" + statementId + ".json";
    log.trace("DELETE " + url);
    HttpUriRequest httpDelete = RequestFactory.createDELETERequest(url, params);

    log.trace("httpDelete: " + httpDelete);

    HttpResponse response = httpclient.execute(httpDelete);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);

    if (" ".equals(responseString)) {
      result = true;
    }

    return result;
  }

  private HashMap<String, String> createAuthenticatedParams() {
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("auth_token", authToken);
    return params;
  }

  @Override
  public List<Amen> getAmenForUser(Long userId, Long lastAmenId) throws IOException {

    log.debug("getAmenForUser(User)");
    if (lastAmenId == null) {
      return getUserForId(userId).getRecentAmen();
    }

    log.debug("getFeed");
    ArrayList<Amen> result;

    HashMap<String, String> params = createAuthenticatedParams();
    if (lastAmenId > 0) {
      params.put("last_amen_id", "" + lastAmenId);
    }

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "users/" +userId + "/amen.json", params);

    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("getAmenForUser produced error: " + responseString);
    }
    Type collectionType = new TypeToken<List<Amen>>() {
    }.getType();
    result = gson.fromJson(responseString, collectionType);


    return result;


  }

  @Override
  public User getUserForId(Long userId) throws IOException {
    log.debug("getUserInfo(User)");
    User result;

    HashMap<String, String> params = createAuthenticatedParams();

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/users/" + userId + ".json", params);


    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    result = gson.fromJson(responseString, User.class);

    return result;  //To change body of implemented methods use File | Settings | File Templates.
  }

  private String makeStringFromEntity(HttpEntity responseEntity) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), "utf-8"));
    StringBuilder builder = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null) {

      log.trace("makeStringFromEntity | " + line);

      if ("<!DOCTYPE html>".equals(line)) {
        //no JSON => Server error
        log.error("Received HTML!");
        return "{\"error\": \"Server error\"}";
      }
      builder.append(line);

    }

    return builder.toString();
  }


  private String extractCookie(String value) {

    int semicolonIndex = value.indexOf(";");
    return value.substring(0, semicolonIndex);
  }


  @Override
  public User getMe() {

    return me;

  }

  public List<User> followers(Long id) throws IOException {

    List<User> result = new ArrayList<User>();
    log.debug("followers()");
    HashMap<String, String> params = createAuthenticatedParams();
//    params.put("limit", "" + 40);
    params.put("last_user_id", "" + 11181);
    //https://getamen.com/users/12665/followers.json
    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/users/" + id + "/followers.json", params);


    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);

    Type collectionType = new TypeToken<Collection<User>>() {
    }.getType();
    result = gson.fromJson(responseString, collectionType);

    return result;
  }

  public List<User> following(Long id) throws IOException {

    List<User> result = new ArrayList<User>();
    log.debug("followers()");
    HashMap<String, String> params = createAuthenticatedParams();
    //    params.put("limit", "" + 40);
//        params.put("last_user_id", "" + 11181);
    //https://getamen.com/users/12665/followers.json
    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/users/" + id + "/following.json", params);


    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);

    Type collectionType = new TypeToken<Collection<User>>() {
    }.getType();
    result = gson.fromJson(responseString, collectionType);


    return result;
  }

  public List<Objekt> objektsForQuery(CharSequence query, int kindId, Double lat, Double lon) throws IOException {
    List<Objekt> result = null;

    log.debug("objektsForQuery() lat: " + lat + " lon: " + lon);

    HashMap<String, String> params = createAuthenticatedParams();
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

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "objekts.json", params);
    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);

    Type collectionType = new TypeToken<Collection<Objekt>>() {
    }.getType();
    result = gson.fromJson(responseString, collectionType);

    return result;
  }

  @Override
  public String getAuthToken() {
    return authToken;
  }
  @Override
  public void removeAuthToken() {
    authToken = null;
  }

  @Override
  public List<Amen> getAmenForObjekt(Long objektId) throws IOException {

    List<Amen> result;
 //   https://getamen.com/things/97282
    log.debug("AmenForObjekt() if: " + objektId);

        HashMap<String, String> params = createAuthenticatedParams();


        HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "o/" + objektId + "/amens.json", params);
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();

        final String responseString = makeStringFromEntity(responseEntity);

        Type collectionType = new TypeToken<Collection<Amen>>() {
        }.getType();
        result = gson.fromJson(responseString, collectionType);

    
    return result;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public static String addAuthTokenToJSON(Amen amen, String authToken) {

    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    JsonElement element = gson.toJsonTree(amen);

    JsonObject object = element.getAsJsonObject();

    object.addProperty("auth_token", authToken);

    return object.toString();
  }


}

