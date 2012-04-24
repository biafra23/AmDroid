package com.jaeckel.amenoid.api;

import java.io.BufferedReader;
import java.io.File;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Comment;
import com.jaeckel.amenoid.api.model.DateSerializer;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.api.model.ServerError;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.Topic;
import com.jaeckel.amenoid.api.model.User;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.entity.ByteArrayEntity;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.params.CoreProtocolPNames;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:35 PM
 */
public class AmenServiceImpl implements AmenService {

  private final static Logger log        = LoggerFactory.getLogger("Amen");
  //  private final static String serviceUrl = "http://getamen.com/";
  private final static String serviceUrl = "https://getamen.com/";
//  private final static String serviceUrl = "https://staging.getamen.com/";

  private String authName;
  private String authPassword;

  private User        me;
  private ServerError lastError;
  private String      authToken;

  private HttpClient httpclient;

  public AmenServiceImpl(HttpClient httpClient) {
//    HttpParams params = new BasicHttpParams();
//    params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
//    params.setParameter(CoreProtocolPNames.USER_AGENT, "Amenoid/1.0 HttpClient/4.0.1 Android");
//
//    final SchemeRegistry schemeRegistry = new SchemeRegistry();
//    Scheme scheme = new Scheme("https", SSLSocketFactory.getSocketFactory(), 443);
//    schemeRegistry.register(scheme);
//
//    httpclient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
    this.httpclient = httpClient;

//    this.httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Amen/1.2.0 CFNetwork/548.1.4 Darwin/11.0.0");
    this.httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Amenoid/1.0 HttpClient/4.2.1 Android");

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
    log.debug("responseString: [" + responseString + "]");


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
    log.debug("responseString: [" + responseString + "]");
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

    //remove ranked_statements
//    dispute.getStatement().setAgreeingNetwork(new ArrayList<User>());

    Map<String, String> map = new HashMap<String, String>();
//    map.put("share_to_facebook", "false");
//    map.put("share_to_twitter", "false");
//    map.put("has_photo", "false");
//    map.put("shared_to_twitter_on_client", "false");
//    map.put("local_time", "2012-04-22T00:01:24+020");

    map.put("auth_token", authToken);

    String amenJsonString = addKeyValueToJSON(dispute, map);

    HttpUriRequest httpPost = RequestFactory.createJSONPOSTRequest(serviceUrl + "amen.json", amenJsonString);
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
    params.put("full", "true");

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/amen/" + id + ".json", params);

    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("getAmenForId produced error: " + responseString);
    }
    amen = gson.fromJson(responseString, Amen.class);

    if (amen.getCommentsCount() > 5) {
      //get all the comments
      ArrayList<Comment> comments = getCommentsForAmenId(amen.getId());
      amen.setComments(comments);
    }

    return amen;
  }

  @Override
  public ArrayList<Comment> getCommentsForAmenId(Long amenId) throws IOException {
    ArrayList<Comment> result;
    Map params = createAuthenticatedParams();

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/amen/" + amenId + "/comments", params);

    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("getAmenForId produced error: " + responseString);
    }
    Type collectionType = new TypeToken<List<Comment>>() {
    }.getType();
    result = gson.fromJson(responseString, collectionType);

    return result;
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
  public Topic getTopicsForId(String id, Long includeStatementId) throws IOException {

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

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "users/" + userId + "/amen.json", params);

//    Header cookie = new BasicHeader("set-cookie", "_getamen_session_production=BAh7BkkiD3Nlc3Npb25faWQGOgZFRkkiJWQ1NGJjNzJhOTkyZTg5MzQ5NjhhZmFhNzRmNDE3Yzk2BjsAVA%3D%3D--59eb5438c0f48c1314caf043f4cd2a36c12ed26e");
//    httpGet.addHeader(cookie);

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
  public List<Amen> getAmenForUser(String userName, Long lastAmenId) throws IOException {

    log.debug("getAmenForUser(User)");

    log.debug("getFeed");
    ArrayList<Amen> result;
    User u;
    HashMap<String, String> params = createAuthenticatedParams();
    if (lastAmenId > 0) {
      params.put("last_amen_id", "" + lastAmenId);
    }

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + userName + ".json", params);

    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("getAmenForUser produced error: " + responseString);
    }
    Type collectionType = new TypeToken<List<Amen>>() {
    }.getType();
    u = gson.fromJson(responseString, User.class);


    return u.getRecentAmen();


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

  @Override
  public User getUserForId(String userName) throws IOException {
    log.debug("getUserInfo(UserName)");
    User result;

    HashMap<String, String> params = createAuthenticatedParams();

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + userName + ".json", params);


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


    return result;
  }

  public static String addKeyValueToJSON(Amen amen, Map<String, String> map) {

    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    JsonElement element = gson.toJsonTree(amen);

    JsonObject object = element.getAsJsonObject();

    for (Map.Entry<String, String> entry : map.entrySet()) {
      object.addProperty(entry.getKey(), entry.getValue());
    }

    return object.toString();
  }

  public static String addAuthTokenToJSON(Amen amen, String authToken) {
    Map<String, String> map = new HashMap<String, String>();
    map.put("auth_token", authToken);
    return addKeyValueToJSON(amen, map);
  }

  @Override
  public List<Amen> search(String query) throws IOException {

    List<Amen> result;
    //   https://getamen.com/things/97282
    log.debug("search(): " + query);

    HashMap<String, String> params = createAuthenticatedParams();
    params.put("q", query);

    HttpUriRequest httpGet = RequestFactory.createGETRequest(serviceUrl + "/search.json", params);
    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);

    Type collectionType = new TypeToken<Collection<Amen>>() {
    }.getType();
    result = gson.fromJson(responseString, collectionType);

    return result;
  }

  @Override
  public Comment createComment(int amenId, String body) {

    Comment result = null;
    log.debug("createComment(): " + body);

    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("auth_token", authToken);
    jsonObject.addProperty("body", body);
    jsonObject.addProperty("amen_id", amenId);

    HttpUriRequest jsonPostRequest = RequestFactory.createJSONPOSTRequest(serviceUrl + "/comments.json", jsonObject.toString());


    HttpResponse response = null;
    try {
      response = httpclient.execute(jsonPostRequest);

      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);

      Type collectionType = new TypeToken<Comment>() {
      }.getType();
      result = gson.fromJson(responseString, collectionType);

    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    return result;
  }

  @Override
  public Boolean deleteComment(int commentId) {

    return false;
  }

  public Amen getAmenByUrl(String url) throws IOException {

    Amen amen;
    HashMap<String, String> params = createAuthenticatedParams();

    HttpUriRequest httpGet = RequestFactory.createGETRequest(url, params);

    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity responseEntity = response.getEntity();

    final String responseString = makeStringFromEntity(responseEntity);
    if (responseString.startsWith("{\"error\":")) {
      throw new RuntimeException("getAmenForId produced error: " + responseString);
    }
    amen = gson.fromJson(responseString, Amen.class);

    return amen;

  }

  public Boolean addImageToAmen(Long amenId, File image) {

    MultipartEntity multipartEntity = new MultipartEntity();


    FileBody bin = new FileBody(image, "photo.jpg", "image/jpg", "UTF-8");
    multipartEntity.addPart("photo", bin);

    try {
      multipartEntity.addPart("amen_id", new StringBody("" + amenId));
      multipartEntity.addPart("auth_token", new StringBody(authToken));

    } catch (UnsupportedEncodingException e) {

      throw new RuntimeException("Unsupported Encoding", e);
    }

    HttpPost httpPost = new HttpPost(serviceUrl + "amen_photos.json");
    httpPost.setEntity(multipartEntity);

    HttpResponse response = null;
    try {
      response = httpclient.execute(httpPost);

      HttpEntity responseEntity = response.getEntity();

      final String responseString = makeStringFromEntity(responseEntity);
      if (responseString.startsWith("{\"error\":")) {
        throw new RuntimeException("amen produced error: " + responseString);
      }
    } catch (IOException e) {
      throw new RuntimeException("IOException", e);
    }

    return true;
  }
}

