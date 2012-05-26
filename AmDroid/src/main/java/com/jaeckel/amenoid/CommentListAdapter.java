package com.jaeckel.amenoid;

import java.io.IOException;
import java.util.List;

import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.Comment;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.AmenLibTask;
import com.jaeckel.amenoid.util.Log;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author biafra
 * @date 4/25/12 10:15 PM
 */
public class CommentListAdapter extends ArrayAdapter<Comment> {
  private int            textViewResourceId;
  private LayoutInflater inflater;
  private Activity        context;

  public CommentListAdapter(Activity context, int textViewResourceId, List<Comment> objects) {
    super(context, textViewResourceId, objects);
    this.textViewResourceId = textViewResourceId;
    inflater = LayoutInflater.from(context);

  }

  static class ViewHolder {
    TextView  user;
    TextView  body;
    ImageView delete;
    long      id;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View row = convertView;
    Comment comment = getItem(position);
    ViewHolder holder;

    //    final Statement stmt = amen.getStatement();
    if (row == null) {

      holder = new ViewHolder();
      row = inflater.inflate(textViewResourceId, parent, false);

      holder.user = (TextView) row.findViewById(R.id.user);
      holder.body = (TextView) row.findViewById(R.id.body);
      holder.delete = (ImageView) row.findViewById(R.id.delete);

      row.setTag(holder);

    } else {

      holder = (ViewHolder) row.getTag();
    }

    final long comment_id = comment.getId();

    holder.user.setText(comment.getUser().getName());
    holder.body.setText(comment.getBody());

    if (AmenoidApp.getInstance().getService().getMe() != null
        && AmenoidApp.getInstance().getService().getMe().getId() == comment.getUser().getId()) {

      holder.delete.setVisibility(View.VISIBLE);
      holder.delete.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {
          Log.d("CommentListAdapter", "trash as trash can!!!: id: " + comment_id);


          new DeleteCommentTask(context).executeOnThreadPool(comment_id);


        }
      });
    } else {
      holder.delete.setVisibility(View.GONE);
    }

    return row;
  }


  //
  // DeleteCommentTask
  //
  private class DeleteCommentTask extends AmenLibTask<Long, Integer, Amen> {

    public DeleteCommentTask(Activity context) {
      super(context);
    }

    protected Amen wrappedDoInBackground(Long... commentIds) throws IOException {

      Amen amen = null;
      for (Long commentId : commentIds) {
        AmenoidApp.getInstance().getService().deleteComment(commentId);

        amen = AmenoidApp.getInstance().getService().getAmenForId(commentIds[0]);
        Log.d(TAG, "Amen returned from getAmenForId(): " + amen);

      }

      return amen;
    }

    @Override
    protected void onPreExecute() {
    }

    protected void wrappedOnPostExecute(Amen result) {
    }
  }

}
