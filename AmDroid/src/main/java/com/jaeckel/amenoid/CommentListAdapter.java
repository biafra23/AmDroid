package com.jaeckel.amenoid;

import java.util.List;

import com.jaeckel.amenoid.api.model.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    TextView user;
    TextView body;
    TextView delete;
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
      holder.delete = (TextView) row.findViewById(R.id.delete);

      row.setTag(holder);

    } else {

      holder = (ViewHolder) row.getTag();
    }

    holder.user.setText(comment.getUser().getName());
    holder.body.setText(comment.getBody());

    return row;
  }

}
