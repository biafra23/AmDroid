package com.jaeckel.amenoid;

import java.util.List;

import com.github.ignition.core.widgets.RemoteImageView;
import com.jaeckel.amenoid.api.model.Category;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author biafra
 * @date 7/6/12 11:19 PM
 */
public class CategoryListAdapter extends ArrayAdapter<Category> {
/*
   <ImageView android:id="@+id/category_icon"
               android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:src="@drawable/abs__ab_solid_dark_holo"
               android:contentDescription="Categroy Icon"
            />
    <TextView android:id="@+id/category_name"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
            />
    <TextView android:id="@+id/category_subtitle"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
            />
 */

  private int            textViewResourceId;
  private LayoutInflater inflater;


  public CategoryListAdapter(Context context, int textViewResourceId, List<Category> objects) {
    super(context, textViewResourceId, objects);

    inflater = LayoutInflater.from(context);
    this.textViewResourceId = textViewResourceId;
  }

  public CategoryListAdapter(Context context, int resource, int textViewResourceId, List<Category> objects) {
    super(context, resource, textViewResourceId, objects);

    inflater = LayoutInflater.from(context);
    this.textViewResourceId = textViewResourceId;
  }


  static class ViewHolder {
    RemoteImageView categoryIcon;
    TextView        categoryName;
    TextView        categorySubtitle;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View row = convertView;
    Category category = getItem(position);
    ViewHolder holder;

    if (row == null) {

      holder = new ViewHolder();
      row = inflater.inflate(textViewResourceId, parent, false);

      holder.categoryIcon = (RemoteImageView) row.findViewById(R.id.category_icon);
      holder.categoryName = (TextView) row.findViewById(R.id.category_name);
      holder.categorySubtitle = (TextView) row.findViewById(R.id.category_subtitle);

      row.setTag(holder);

    } else {

      holder = (ViewHolder) row.getTag();
    }

    holder.categoryName.setText(category.getName());
    holder.categorySubtitle.setText(category.getSubtitle());

    if (!TextUtils.isEmpty(category.getIcon())) {
      holder.categoryIcon.setImageUrl(category.getIcon());
      holder.categoryIcon.loadImage();
    }

    return row;
  }


}
