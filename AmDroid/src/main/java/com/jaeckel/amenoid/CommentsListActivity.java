package com.jaeckel.amenoid;

import com.jaeckel.amenoid.api.model.Amen;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author biafra
 * @date 4/25/12 10:10 PM
 */
public class CommentsListActivity extends ListActivity {
  private Amen currentAmen;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent startingIntent = getIntent();
    currentAmen = startingIntent.getParcelableExtra(Constants.EXTRA_AMEN);

    CommentListAdapter adapter = new CommentListAdapter(this, R.layout.list_item_comment, currentAmen.getComments());

    setContentView(R.layout.comments);


    setListAdapter(adapter);

  }
}