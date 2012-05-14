package com.jaeckel.amenoid.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Comment;
import com.jaeckel.amenoid.api.model.Objekt;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.Topic;
import com.jaeckel.amenoid.api.model.User;

/**
 * User: biafra
 * Date: 9/23/11
 * Time: 8:35 PM
 */
public interface AmenService {

  public final int AMEN_KIND_STATEMENT = 0;
  public final int AMEN_KIND_AMEN      = 1;
  public final int AMEN_KIND_DISPUTE   = 2;

  public final static int OBJEKT_KIND_PERSON = 0;
  public final static int OBJEKT_KIND_PLACE  = 1;
  public final static int OBJEKT_KIND_THING  = 2;

  public final static int FEED_TYPE_FOLLOWING = 0;
  public final static int FEED_TYPE_RECENT    = 1;
  public final static int FEED_TYPE_POPULAR   = 2;

  public User getMe();

  public String getAuthToken();

  public AmenService init(String username, String password) throws IOException;

  public AmenService init(String authToken, User me);

  public List<Amen> getFeed(int type) throws IOException;

  public List<Amen> getFeed(long sinceId, int pageSize, int type) throws IOException;

  public List<Amen> getFeed(long beforeId, long sinceId, int pageSize, int type) throws IOException;

  public boolean follow(User u) throws IOException;

  public boolean unfollow(User u) throws IOException;

  public Amen amen(Statement a) throws IOException;

  public Amen amen(Long amenId) throws IOException;

  public Long dispute(Amen dispute) throws IOException;

  public boolean takeBack(Long statementId) throws IOException;

  public List<Amen> getAmenForUser(Long userId, Long lastAmenId) throws IOException;

  public List<Amen> getAmenForUser(String userName, Long lastAmenId) throws IOException;

  public User getUserForId(Long id) throws IOException;

  public User getUserForId(String id) throws IOException;

  public Amen addStatement(Statement statement) throws IOException;

  public Statement getStatementForId(Long id) throws IOException;

  public Amen getAmenForId(Long id) throws IOException;

  public Amen getAmenByUrl(String url) throws IOException;

  public Topic getTopicsForId(String id, Long includeStatementId) throws IOException;

  public List<User> followers(Long id) throws IOException;

  public List<User> following(Long id) throws IOException;

  public List<Objekt> objektsForQuery(CharSequence query, int kindId, Double lat, Double lon) throws IOException;

  //sign out
  public void removeAuthToken();

  //Subject Cards

  public List<Amen> getAmenForObjekt(Long objId) throws IOException;

  public List<Amen> search(String query) throws IOException;

  public Comment createComment(long amenId, String body) throws IOException;

  public Boolean deleteComment(long commentId) throws IOException;

  public ArrayList<Comment> getCommentsForAmenId(Long amenId) throws IOException;

  public Boolean addImageToAmen(Long amenId, File image);

  public User signup(String name, String email, String password) throws SignupFailedException;

}
