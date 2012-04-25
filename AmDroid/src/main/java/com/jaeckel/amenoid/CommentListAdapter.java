package com.jaeckel.amenoid;

import java.io.IOException;
import java.util.List;

import com.jaeckel.amenoid.api.model.Comment;
import com.jaeckel.amenoid.app.AmenoidApp;

import android.content.Context;
import android.util.Log;
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


  public CommentListAdapter(Context context, int textViewResourceId, List<Comment> objects) {
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

    if (AmenoidApp.getInstance().getService().getMe().getId() == comment.getUser().getId()) {

      holder.delete.setVisibility(View.VISIBLE);
      holder.delete.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {
          Log.d("CommentListAdapter", "trash as trash can!!!: id: " + comment_id);

          try {

            AmenoidApp.getInstance().getService().deleteComment(comment_id);

          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      });
    } else {
      holder.delete.setVisibility(View.GONE);
    }

    return row;
  }

}
