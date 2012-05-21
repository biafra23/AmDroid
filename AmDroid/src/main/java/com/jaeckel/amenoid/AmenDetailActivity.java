package com.jaeckel.amenoid;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Comment;
import com.jaeckel.amenoid.api.model.MediaItem;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.api.model.Topic;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.cwac.cache.CacheBase;
import com.jaeckel.amenoid.cwac.cache.SimpleWebImageCache;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailAdapter;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailBus;
import com.jaeckel.amenoid.cwac.thumbnail.ThumbnailMessage;
import com.jaeckel.amenoid.util.AmenLibTask;
import com.jaeckel.amenoid.util.Log;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 10:27 AM
 */
public class AmenDetailActivity extends SherlockListActivity {

  private static final String TAG = "AmenDetailActivity";
  private Amen             currentAmen;
  private Statement        currentStatement;
  private Topic            topicWithRankedStatements;
  private TextView         statementView;
  private TextView         userView;
  private TextView         amenCount;
  private TextView         commentsCount;
  private Button           amenTakeBackButton;
  private Button           hellNoButton;
  private UserListAdapter  adapter;
  private ThumbnailAdapter thumbs;
  private AmenService      service;
  private static final int[]  IMAGE_IDS = {R.id.user_image};
  private              String lastError = null;
  private SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache;
  private Typeface                                            amenTypeThin;
  private Typeface                                            amenTypeBold;
  private TextView                                            commentsTextView;
  private ShareActionProvider                                 mShareActionProvider;

  @Override
  public boolean onSearchRequested() {
    return super.onSearchRequested();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.d(TAG, "onConfigurationChanged(): " + newConfig);
//    setContentView(R.layout.myLayout);
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "--> onCreate");

    service = AmenoidApp.getInstance().getService();
    cache = AmenoidApp.getInstance().getCache();

    setContentView(R.layout.details);
    setTitle("Amendetails");

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);


    amenTypeThin = AmenoidApp.getInstance().getAmenTypeThin();
    amenTypeBold = AmenoidApp.getInstance().getAmenTypeBold();

    ListView list = (ListView) findViewById(android.R.id.list);
    View header = getLayoutInflater().inflate(R.layout.details_header, null, false);
    list.addHeaderView(header);

    commentsTextView = (TextView) findViewById(R.id.comments);
    commentsTextView.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {
        Intent intent = new Intent(AmenDetailActivity.this, CommentsListActivity.class);
        intent.putExtra(Constants.EXTRA_AMEN, currentAmen);
        startActivity(intent);
      }
    });
    Intent startingIntent = getIntent();
    currentAmen = startingIntent.getParcelableExtra(Constants.EXTRA_AMEN);


    Log.d(TAG, "Current (OLD!) Amen: " + currentAmen);

    if (currentAmen == null) {

      // when coming from scorecard or subjectcard. They contain only statements
      currentStatement = startingIntent.getParcelableExtra(Constants.EXTRA_STATEMENT);

      // start downloading statement again to get the first_amen_id
      new GetStatementTask(this).execute(currentStatement.getId());

    } else {

      currentStatement = currentAmen.getStatement();
      new GetAmenTask(this).execute(currentAmen.getId());
    }


//    header.setOnClickListener(new View.OnClickListener() {
//      public void onClick(View view) {
//        startScoreBoardActivity();
//
//      }
//    });

    final List<User> users = currentStatement.getAgreeingNetwork();
//    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
//    for (User u : users) {
//      Log.d(TAG, "AgreeingNetwork: " + u);
//    }
    thumbs = new ThumbnailAdapter(this, new UserListAdapter(this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
    setListAdapter(thumbs);


    ImageView mediaPhotoImageView = (ImageView) header.findViewById(R.id.media_photo);
    mediaPhotoImageView.setVisibility(View.GONE);

    if (currentAmen != null) {

      final List<MediaItem> currentAmenMedia = currentAmen.getMedia();
      if (currentAmenMedia != null && currentAmenMedia.size() > 0) {

        for (MediaItem amenMediaItem : currentAmenMedia) {

          final String mediaUrl = amenMediaItem.getContentUrl();
          Log.d(TAG, "amenMediaItem.getContentUrl(): " + mediaUrl);
          if (amenMediaItem.getType().contains("photo")) {

            mediaPhotoImageView.setVisibility(View.VISIBLE);
            int result = cache.getStatus(mediaUrl);
            if (result == CacheBase.CACHE_MEMORY) {
              Log.d(TAG, "cache.getStatus(" + mediaUrl + "): CACHE_MEMORY");
              mediaPhotoImageView.setImageDrawable(cache.get(mediaUrl));
            } else {
              mediaPhotoImageView.setImageResource(R.drawable.placeholder);
              ThumbnailMessage msg = cache.getBus().createMessage(thumbs.toString());
              msg.setImageView(mediaPhotoImageView);
              msg.setUrl(mediaUrl);
              try {
                cache.notify(msg.getUrl(), msg);
              } catch (Throwable t) {
                Log.e(TAG, "Exception trying to fetch image", t);
                throw new RuntimeException(t);
              }
            }
          }
        }
      } else {
        Log.d(TAG, "                  currentAmen: " + currentAmen);
        if (currentAmen != null) {
          Log.d(TAG, "       currentAmen.getMedia(): " + currentAmenMedia);
        }
        if (currentAmen != null && currentAmenMedia != null) {
          Log.d(TAG, "currentAmen.getMedia().size(): " + currentAmenMedia.size());
        }

      }
    }


    ImageView objektPhotoImageView = (ImageView) header.findViewById(R.id.objekt_photo);
    View objektPhotoImageViewWrapper = (View) header.findViewById(R.id.objekt_photo_wrapper);
    objektPhotoImageViewWrapper.setVisibility(View.GONE);

    final List<MediaItem> objektMediaItems = currentStatement.getObjekt().getMedia();

    if (objektMediaItems != null && objektMediaItems.size() > 0) {
      for (MediaItem objektItem : objektMediaItems) {
        String type = objektItem.getType();
        if (type.contains("photo")) {

          final String mediaUrl = objektItem.getContentUrl();
          Log.d(TAG, "objektItem.getContentUrl(): " + mediaUrl);
          objektPhotoImageViewWrapper.setVisibility(View.VISIBLE);

          int result = cache.getStatus(mediaUrl);

          if (result == CacheBase.CACHE_MEMORY) {

            Log.d(TAG, "cache.getStatus(" + mediaUrl + "): CACHE_MEMORY");
            objektPhotoImageView.setImageDrawable(cache.get(mediaUrl));

          } else {


            mediaPhotoImageView.setImageResource(R.drawable.placeholder);

            ThumbnailMessage msg = cache.getBus().createMessage(thumbs.toString());

            msg.setImageView(mediaPhotoImageView);
            msg.setUrl(mediaUrl);

            try {

              cache.notify(msg.getUrl(), msg);

            } catch (Throwable t) {
              Log.e(TAG, "Exception trying to fetch image", t);
              throw new RuntimeException(t);
            }
          }
        }
      }

    } else {
      Log.d(TAG, "       currentStatement.getObjekt().getMedia(): " + objektMediaItems);
      if (objektMediaItems != null) {
        Log.d(TAG, "currentStatement.getObjekt().getMedia().size(): " + objektMediaItems.size());
      }
    }


    Intent resultIntent = new Intent();
    resultIntent.putExtra(Constants.EXTRA_STATEMENT_ID, currentStatement.getId());
    setResult(AmenListActivity.REQUEST_CODE_AMEN_DETAILS, resultIntent);

    final View commentLayout = findViewById(R.id.comment_edit_layout);
    Button addComment = (Button) findViewById(R.id.add_comment);
    addComment.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {

        commentLayout.setVisibility(View.VISIBLE);
      }

    });

    final EditText commentField = (EditText) findViewById(R.id.comment_edit_text);
    Button saveComment = (Button) findViewById(R.id.save_comment);
    saveComment.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {

        new CreateCommentTask(AmenDetailActivity.this).execute(commentField.getText().toString());

        commentField.setText("");
        commentLayout.setVisibility(View.GONE);
      }

    });
  }

  private void startScoreBoardActivity() {
    Intent intent = new Intent(this, ScoreBoardActivity.class);
    intent.putExtra(Constants.EXTRA_TOPIC, currentStatement.getTopic());
    intent.putExtra(Constants.EXTRA_OBJEKT_KIND, currentStatement.getObjekt().getKindId());
    startActivity(intent);
  }

  public void onResume() {
    super.onResume();
    statementView = (TextView) findViewById(R.id.statement);
    statementView.setTypeface(amenTypeBold);

    userView = (TextView) findViewById(R.id.user);
    userView.setTypeface(amenTypeThin);

    amenCount = (TextView) findViewById(R.id.amen_count);
    amenCount.setTypeface(amenTypeThin);

    commentsCount = (TextView) findViewById(R.id.comments_count);
    commentsCount.setTypeface(amenTypeThin);

    amenTakeBackButton = (Button) findViewById(R.id.amen_take_back);
    amenTakeBackButton.setTypeface(amenTypeBold);
    amenTakeBackButton.setEnabled(false);
    hellNoButton = (Button) findViewById(R.id.hell_no);
    hellNoButton.setTypeface(amenTypeBold);
    hellNoButton.setEnabled(false);
    populateFormWithAmen(true);

    if (!AmenoidApp.getInstance().isSignedIn()) {
      amenTakeBackButton.setEnabled(false);
      hellNoButton.setEnabled(false);
    }


  }


  private void populateFormWithAmen(boolean updateName) {

    if (currentAmen == null) {
      Log.d(TAG, "currentAmen: " + currentAmen);
      Log.d(TAG, "currentStatement: " + currentStatement);
      statementView.setText(AmenListAdapter.styleAmenWithColor(currentStatement, false, null, this));
    } else {
      statementView.setText(AmenListAdapter.styleAmenWithColor(currentAmen, this));
    }


//    statementView.setText(currentAmen.getStatement().toDisplayString());
    //TODO: find a better way to have the original? name here
    if (updateName) {
      if (currentAmen != null && currentAmen.getUser() != null) {
        userView.setText(currentAmen.getUser().getName() + ", " + format(currentAmen.getCreatedAt()));
      } else if (currentStatement.getFirstPoster() != null) {
        userView.setText(currentStatement.getFirstPoster().getName() + ", " + format(currentStatement.getFirstPostedAt()));
      }

    }

    amenCount.setText(currentStatement.getTotalAmenCount() + " Amen");
    StringBuilder agreeing = new StringBuilder();
    for (User user : currentStatement.getAgreeingNetwork()) {
      agreeing.append(user.getName() + ", ");
    }
    if (amened(currentStatement)) {
      amenTakeBackButton.setText("Take Back");
    } else {
      amenTakeBackButton.setText("Amen!");
    }
    if (currentAmen != null && currentAmen.getId() != null) {

      setAmenButtonListener();

      String commentsCountText = "";
      if (currentAmen.getCommentsCount() != null && currentAmen.getCommentsCount() == 1) {
        commentsCountText = " / " + currentAmen.getCommentsCount() + " comment";
      }

      if (currentAmen.getCommentsCount() != null && currentAmen.getCommentsCount() > 1) {
        commentsCountText = " / " + currentAmen.getCommentsCount() + " comments";
      }

      commentsCount.setText(commentsCountText);

    }

  }

  private void setAmenButtonListener() {
    if (service.getMe() != null) {
      amenTakeBackButton.setEnabled(true);
      amenTakeBackButton.setOnClickListener(new View.OnClickListener() {

        public void onClick(View view) {

          amenTakeBackButton.setEnabled(false);

          if (amened(currentStatement)) {

            Log.d(TAG, "Back taking: " + currentAmen);
            new AmenDetailActivity.TakeBackTask(AmenDetailActivity.this).execute(currentStatement.getId());

          } else {
            Log.d(TAG, "amening: " + currentAmen);
            new AmenDetailActivity.AmenTask(AmenDetailActivity.this).execute(currentAmen.getId());

          }
          populateFormWithAmen(false);

        }
      });

      hellNoButton.setEnabled(true);
      hellNoButton.setOnClickListener(new View.OnClickListener() {

        public void onClick(View view) {
          //TODO: show hellno form here to let user select different objekt


          populateFormWithAmen(false);

          Intent intent = new Intent(AmenDetailActivity.this, DisputeActivity.class);

          intent.putExtra(Constants.EXTRA_AMEN, currentAmen);
          startActivity(intent);
        }
      });
    }
  }

  private boolean amened(Statement currentStatement) {
    for (User u : currentStatement.getAgreeingNetwork()) {
      if (AmenoidApp.getInstance().isSignedIn() && service.getMe() != null && u.getId() == service.getMe().getId()) {
        return true;
      }
    }
    return false;
  }

  public static String format(Date firstPostedAt) {
    SimpleDateFormat fmt = new SimpleDateFormat("dd. MMMMM yyyy - HH:mm");
    if (firstPostedAt != null) {
      return fmt.format(firstPostedAt);
    }
    return "<date unknown>";
  }

  public void onPause() {
    super.onPause();
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {

    User user = (User) getListAdapter().getItem(position - 1);

    Log.d(TAG, "======> Selected User: " + user);
    Intent intent = new Intent(this, UserDetailActivity.class);
    intent.putExtra(Constants.EXTRA_USER, user);
    startActivity(intent);
  }


  //
  // TakeBackTask
  //
  private class TakeBackTask extends AmenLibTask<Long, Integer, Amen> {

    public TakeBackTask(Activity context) {
      super(context);
    }

    protected Amen wrappedDoInBackground(Long... statementId) throws IOException {
      try {
        service.takeBack(statementId[0]);
        Amen amen = new Amen(service.getStatementForId(statementId[0]));

        amen.setId(currentAmen.getId());
        Log.d(TAG, "Amen returned from amen(): " + amen);
        return amen;
      } catch (RuntimeException e) {
        lastError = e.getMessage();
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPreExecute() {
      amenTakeBackButton.setEnabled(false);
    }

    protected void wrappedOnPostExecute(Amen result) {

      if (result != null) {

        currentAmen = result;
        currentStatement = currentAmen.getStatement();
        populateFormWithAmen(false);
        Toast.makeText(AmenDetailActivity.this, "Taken Back.", Toast.LENGTH_SHORT).show();

        final List<User> users = currentStatement.getAgreeingNetwork();
        //    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
        setListAdapter(thumbs);
      }
      if (service.getMe() != null) {
        amenTakeBackButton.setEnabled(true);
      }

    }

  }

  //
  // AmenTask
  //
  private class AmenTask extends AmenLibTask<Long, Integer, Amen> {

    public AmenTask(Activity context) {
      super(context);
    }

    protected Amen wrappedDoInBackground(Long... amenId) throws IOException {
      Amen amen = service.amen(amenId[0]);
      Log.d(TAG, "Amen returned from amen(): " + amen);

      createShareIntent(amen.getStatement());

      return amen;
    }

    @Override
    protected void onPreExecute() {
      amenTakeBackButton.setEnabled(false);
    }

    protected void wrappedOnPostExecute(Amen result) {
      if (result != null) {

        currentAmen = result;
        currentStatement = currentAmen.getStatement();
        populateFormWithAmen(false);
        Toast.makeText(AmenDetailActivity.this, "Amen'd.", Toast.LENGTH_SHORT).show();

        final List<User> users = currentStatement.getAgreeingNetwork();
        //    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
        setListAdapter(thumbs);

      }
      if (service.getMe() != null) {
        amenTakeBackButton.setEnabled(true);

      }
    }
  }


  //
  // StatementTask
  //
  private class GetStatementTask extends AmenLibTask<Long, Integer, Statement> {

    public GetStatementTask(Activity context) {
      super(context);
    }

    protected Statement wrappedDoInBackground(Long... statementIds) throws IOException {

      Statement statement = service.getStatementForId(statementIds[0]);
      Log.d(TAG, "Statement returned from statement(): " + statement);
      createShareIntent(statement);

      return statement;

    }

    @Override
    protected void onPreExecute() {
    }

    protected void wrappedOnPostExecute(Statement result) {

      if (result != null) {
        currentAmen = new Amen(result);

        currentAmen.setId(result.getFirstAmenId());
        setAmenButtonListener();
//        Toast.makeText(AmenDetailActivity.this, "setId on currentAmen", Toast.LENGTH_SHORT).show();
        currentStatement = result;
        populateFormWithAmen(false);

        // amen button freischalten

        final List<User> users = currentStatement.getAgreeingNetwork();
        //    adapter = new UserListAdapter(this, android.R.layout.simple_list_item_1, users);
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
        setListAdapter(thumbs);

        new GetAmenTask(AmenDetailActivity.this).execute(result.getFirstAmenId());
      }
    }
  }

  //
  // GetAmenTask
  //
  private class GetAmenTask extends AmenLibTask<Long, Integer, Amen> {

    public GetAmenTask(Activity context) {
      super(context);
    }

    protected Amen wrappedDoInBackground(Long... amenIds) throws IOException {

      Amen amen = service.getAmenForId(amenIds[0]);
      Log.d(TAG, "Amen returned from getAmenForId(): " + amen);

      return amen;

    }

    @Override
    protected void onPreExecute() {
    }

    protected void wrappedOnPostExecute(Amen result) {

      if (result != null) {
        currentAmen = result;

        Log.d(TAG, "Current (NEW!) Amen: " + currentAmen);
        StringBuilder commentsText = new StringBuilder();
        //commentsText.append("Comment(s):\n");

        for (Comment comment : currentAmen.getComments()) {
          Log.d(TAG, "comment: " + comment);
          commentsText.append(formatCommentDate(comment.getCreatedAt()));
          commentsText.append(": ");
          commentsText.append(comment.getUser().getName());
          commentsText.append(": ");
          commentsText.append(comment.getBody());
          commentsText.append("\n");
        }
        commentsTextView.setText(commentsText.toString());

        setAmenButtonListener();
        currentStatement = result.getStatement();
        populateFormWithAmen(true);
        final List<User> users = currentStatement.getAgreeingNetwork();
        thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
        setListAdapter(thumbs);
      }
    }
  }


  public static String formatCommentDate(Date firstPostedAt) {
    SimpleDateFormat fmt = new SimpleDateFormat("yy-MM-dd HH:mm");
    if (firstPostedAt != null) {
      return fmt.format(firstPostedAt);
    }
    return "<unknown>";
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    Log.d(TAG, "--> onCreateOptionsMenu");

    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.menu_detail, menu);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      // Get the menu item.
      MenuItem shareMenuItem = menu.findItem(R.id.share_details);
      // Get the provider and hold onto it to set/change the share intent.
      mShareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();
      // Set history different from the default before getting the action
      // view since a call to MenuItem.getActionView() calls
      // onCreateActionView() which uses the backing file name. Omit this
      // line if using the default share history file is desired.
      mShareActionProvider.setShareHistoryFileName("custom_share_history.xml");

      if (currentAmen != null) {
        createShareIntent(currentAmen.getStatement());
      } else if (currentStatement != null) {
        createShareIntent(currentStatement);
      }
    }

//    if (!AmenoidApp.getInstance().isSignedIn()) {
//      MenuItem amenSth = menu.findItem(R.id.amen);
//      amenSth.setEnabled(false);
//    }
    return true;
  }

  private void createShareIntent(Statement statement) {
    if (statement == null) {
      return;
    }
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    String amenText = statement.toDisplayString();
    shareIntent.setType("text/plain");
    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, amenText + " #getamen https://getamen.com/statements/" + statement.getId());
    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(shareIntent);
    }
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    Log.d(TAG, "onOptionsItemSelected -> item.getItemId(): " + item.getItemId());

    final Intent amenListIntent = new Intent(this, AmenListActivity.class);
    amenListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    switch (item.getItemId()) {

      case android.R.id.home: {
        startActivity(amenListIntent);
        return true;
      }
//      case R.id.timeline: {
//        startActivity(amenListIntent);
//        return true;
//      }

      case R.id.scoreboard: {
        startScoreBoardActivity();
        return true;
      }
      case R.id.share_details: {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

          String amenText = currentAmen.getStatement().toDisplayString();
          Intent sharingIntent = new Intent(Intent.ACTION_SEND);
          sharingIntent.setType("text/plain");
          sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, amenText + " #getamen https://getamen.com/statements/" + currentAmen.getStatement().getId());

          startActivity(Intent.createChooser(sharingIntent, "Share using"));

          return true;

        } else {

          return false;
        }
      }
//      case R.id.amen:
//        startActivity(new Intent(this, ChooseStatementTypeActivity.class));
//        return true;

      case R.id.subject_page: {
        Intent intent = new Intent(this, SubjectPageActivity.class);

//        Toast.makeText(this, "id: " + currentAmen.getStatement().getObjekt().getId(), Toast.LENGTH_SHORT).show();

        intent.putExtra(Constants.EXTRA_OBJEKT_ID, getCurrentStatement().getObjekt().getId());
        startActivity(intent);
        return true;
      }
//      default: {
//        Log.d(TAG, "Unexpected item.getItemId(): " + item.getItemId());
//        startActivity(amenListIntent);
//        return true;
//      }

    }
    return false;
  }

  private Statement getCurrentStatement() {
    if (currentAmen != null && currentAmen.getStatement() != null) {
      return currentAmen.getStatement();
    }
    return currentStatement;
  }

  private class CreateCommentTask extends AmenLibTask<String, Integer, Comment> {

    public CreateCommentTask(Activity context) {
      super(context);
    }

    @Override
    protected Comment wrappedDoInBackground(String... commentBodies) throws IOException {
      Comment result = null;
      for (String commentBody : commentBodies) {
        result = service.createComment(currentAmen.getId(), commentBody);
      }
      new GetAmenTask(AmenDetailActivity.this).execute(currentAmen.getId());

      return result;
    }

    @Override
    protected void wrappedOnPostExecute(Comment result) {


    }

  }


}
