package com.jaeckel.amenoid;

import com.actionbarsherlock.app.SherlockListActivity;
import com.jaeckel.amenoid.api.model.Amen;

import android.content.Intent;
import android.os.Bundle;

/**
 * @author biafra
 * @date 4/25/12 10:10 PM
 */
public class CommentsListActivity extends SherlockListActivity {
  private Amen currentAmen;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent startingIntent = getIntent();
    currentAmen = startingIntent.getParcelableExtra(Constants.EXTRA_AMEN);

    CommentListAdapter adapter = new CommentListAdapter(this, R.layout.list_item_comment, currentAmen.getComments());

    setContentView(R.layout.comments);


    setListAdapter(adapter);

  }

//  //
//    // GetAmenTask
//    //
//    private class GetAmenTask extends AmenLibTask<Long, Integer, Amen> {
//
//      public GetAmenTask(Activity context) {
//        super(context);
//      }
//
//      protected Amen wrappedDoInBackground(Long... amenIds) throws IOException {
//
//        Amen amen = service.getAmenForId(amenIds[0]);
//        Log.d(TAG, "Amen returned from getAmenForId(): " + amen);
//
//        return amen;
//
//      }
//
//      @Override
//      protected void onPreExecute() {
//      }
//
//      protected void wrappedOnPostExecute(Amen result) {
//
//        if (result != null) {
//          currentAmen = result;
//
//          Log.d(TAG, "Current (NEW!) Amen: " + currentAmen);
//          StringBuilder commentsText = new StringBuilder();
//          //commentsText.append("Comment(s):\n");
//
//          for (Comment comment : currentAmen.getComments()) {
//            Log.d(TAG, "comment: " + comment);
//            commentsText.append(formatCommentDate(comment.getCreatedAt()));
//            commentsText.append(": ");
//            commentsText.append(comment.getUser().getName());
//            commentsText.append(": ");
//            commentsText.append(comment.getBody());
//            commentsText.append("\n");
//          }
//          commentsTextView.setText(commentsText.toString());
//
//          setAmenButtonListener();
//          currentStatement = result.getStatement();
//          populateFormWithAmen(true);
//          final List<User> users = currentStatement.getAgreeingNetwork();
//          thumbs = new ThumbnailAdapter(AmenDetailActivity.this, new UserListAdapter(AmenDetailActivity.this, android.R.layout.activity_list_item, users), cache, IMAGE_IDS);
//          setListAdapter(thumbs);
//        }
//      }
//    }
}