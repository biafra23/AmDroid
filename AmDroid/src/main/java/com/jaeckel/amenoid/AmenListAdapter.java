package com.jaeckel.amenoid;

import java.util.List;

import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.model.Amen;
import com.jaeckel.amenoid.api.model.MediaItem;
import com.jaeckel.amenoid.api.model.Statement;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.Log;
import com.jaeckel.amenoid.util.StyleableSpannableStringBuilder;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * User: biafra
 * Date: 9/25/11
 * Time: 5:58 AM
 */
public class AmenListAdapter extends ArrayAdapter<Amen> {

  private LayoutInflater inflater;
  private Typeface       amenTypeThin;
  private Typeface       amenTypeBold;
  private int            textViewResourceId;

  private final static String TAG = AmenListAdapter.class.getSimpleName();

  public AmenListAdapter(Context context, int textViewResourceId, List<Amen> objects) {
    super(context, textViewResourceId, objects);
    inflater = LayoutInflater.from(context);
    amenTypeThin = AmenoidApp.getInstance().getAmenTypeThin();
    amenTypeBold = AmenoidApp.getInstance().getAmenTypeBold();
    this.textViewResourceId = textViewResourceId;
//    setNotifyOnChange(true);
  }

  static class ViewHolder {
    TextView  user;
    ImageView userImage;
    ImageView mediaPhoto;
    ImageView objektPhoto;
    View      objektPhotoWrapper;
    TextView  statementView;
    TextView  amenCountView;
    TextView  commentsCountView;
    TextView  sinceView;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View row = convertView;
    Amen amen = getItem(position);
    ViewHolder holder;

//    final Statement stmt = amen.getStatement();
    if (row == null) {

      holder = new ViewHolder();
      row = inflater.inflate(textViewResourceId, parent, false);

      holder.user = (TextView) row.findViewById(R.id.user);
      holder.statementView = (TextView) row.findViewById(R.id.statement);
      holder.userImage = (ImageView) row.findViewById(R.id.user_image);
      holder.mediaPhoto = (ImageView) row.findViewById(R.id.media_photo);
      holder.objektPhoto = (ImageView) row.findViewById(R.id.objekt_photo);
      holder.objektPhotoWrapper = row.findViewById(R.id.objekt_photo_wrapper);
      holder.sinceView = (TextView) row.findViewById(R.id.since);
      holder.amenCountView = (TextView) row.findViewById(R.id.amen_count);
      holder.commentsCountView = (TextView) row.findViewById(R.id.comments_count);
      row.setTag(holder);

    } else {

      holder = (ViewHolder) row.getTag();
    }

    if (holder.user != null) {
      holder.user.setTypeface(amenTypeThin);
      String from = amen.getUser().getName();

      if (amen.isAmen() && amen.getReferringAmen() != null) {
        from = from + " amen'd " + amen.getReferringAmen().getUser().getName();
      }
      if (amen.isDispute() && amen.getReferringAmen() != null) {
        from = from + " disputes " + amen.getReferringAmen().getUser().getName();
      }
      holder.user.setText(from);
    }


    holder.statementView.setTypeface(amenTypeBold);
    holder.statementView.setText(styleAmenWithColor(amen, getContext()));

    if (holder.userImage != null) {
      String pictureUrl = amen.getUser().getPhoto();
      if (TextUtils.isEmpty(pictureUrl)) {
        pictureUrl = amen.getUser().getPicture();
        if (!TextUtils.isEmpty(pictureUrl)) {
          pictureUrl = pictureUrl + "?type=normal";
        }
      }
//    Log.d("AmenListAdapter", "pictureUrl: " + pictureUrl);
      holder.userImage.setImageResource(R.drawable.placeholder);
      holder.userImage.setTag(pictureUrl);

    }

    //amen Media items
    final List<MediaItem> amenMediaItems = amen.getMedia();
    holder.mediaPhoto.setVisibility(View.GONE);

    if (amenMediaItems != null && amenMediaItems.size() > 0) {

      for (MediaItem item : amenMediaItems) {
        String mediaUrl = item.getContentUrl();
        Log.d(TAG, "amen mediaItem type: " + item.getType() + " url: " + mediaUrl);

        if (item.getType().contains("photo")) {
          holder.mediaPhoto.setVisibility(View.VISIBLE);
          holder.mediaPhoto.setImageResource(R.drawable.placeholder);
          holder.mediaPhoto.setTag(mediaUrl);
          // use only the first photo
          break;
        }
      }
    }

    //obkekt  objektMediaItems
    final List<MediaItem> objektMediaItems = amen.getStatement().getObjekt().getMedia();
    holder.objektPhotoWrapper.setVisibility(View.GONE);

    if (objektMediaItems != null && objektMediaItems.size() > 0) {

      for (MediaItem item : objektMediaItems) {
        String mediaUrl = item.getContentUrl();

        Log.d(TAG, " objekt mediaItem type: " + item.getType() + " url: " + mediaUrl);

        if (item.getType().contains("photo")) {

          holder.objektPhotoWrapper.setVisibility(View.VISIBLE);
          holder.objektPhoto.setImageResource(R.drawable.placeholder);
          holder.objektPhoto.setTag(mediaUrl);
          holder.objektPhotoWrapper.setVisibility(View.VISIBLE);
          // use only the first photo
          break;
        }
      }
    }
    long now = System.currentTimeMillis();
    if (amen.getCreatedAt() != null) {
      long createdAtDate = amen.getCreatedAt().getTime();

      long since = now - createdAtDate;

      since = since / 1000;

//      Log.d("AmenListAdapter", "since: " + since);

      TextView sinceView = holder.sinceView;

      sinceView.setText(renderShortDeltaT(since));

    }
    TextView amenCountView = holder.amenCountView;
    if (amenCountView != null) {
      amenCountView.setTypeface(amenTypeThin);

      long count = 0;
      if (amen.getStatement().getAgreeingNetworkCount() != null) {

        count = amen.getStatement().getAgreeingNetworkCount();

      } else {
        count = amen.getStatement().getTotalAmenCount();
      }
      if (count > 0) {
        amenCountView.setText(count + " Amen");
        amenCountView.setVisibility(View.VISIBLE);
      } else {
        amenCountView.setVisibility(View.INVISIBLE);
      }


    }
    TextView commentsCountView = holder.commentsCountView;
    if (commentsCountView != null && amen.getCommentsCount() != null) {
      commentsCountView.setTypeface(amenTypeThin);

      long count = amen.getCommentsCount();
      if (amen.getCommentsCount() > 0) {
        commentsCountView.setText(" / " + count + " Comments");
        commentsCountView.setVisibility(View.VISIBLE);
      } else {
        commentsCountView.setVisibility(View.INVISIBLE);
      }
    }

    return row;
  }


  private CharSequence renderShortDeltaT(long since) {

    if (since < 60) {
      return since + "s";

    } else if (since < 60 * 60) {
      return (since / 60) + "m";

    } else if (since < 60 * 60 * 24) {
      return (since / 60 / 60) + "h";

    } else if (since < 60 * 60 * 24 * 7) {
      return (since / 60 / 60 / 24) + "d";

    } else if (since < 60 * 60 * 24 * 7 * 4) {
      return (since / 60 / 60 / 24 / 7) + "w";

    } else if (since < 60 * 60 * 24 * 7 * 4 * 12) {
      return (since / 60 / 60 / 24 / 7 / 4) + "mo";
    }

    return "";
  }

  public static CharSequence styleAmenWithColor(Statement stmt, boolean isDispute, String disputingName, Context context) {

    StyleableSpannableStringBuilder statementBuilder = new StyleableSpannableStringBuilder(context);

    statementBuilder
      .appendBold(stmt.getObjekt().getName())
      .append(" ");

    if (stmt.getObjekt().getKindId() == AmenService.OBJEKT_KIND_THING) {
      //Thing
      statementBuilder.appendOrange("is the ");
      if (stmt.getTopic().isBest()) {
        statementBuilder.appendOrange(" Best ");
      } else {
        statementBuilder.appendOrange(" Worst ");
      }
      statementBuilder
        .appendOrange(stmt.getTopic().getDescription())
        .appendOrange(" ")
        .appendOrange(stmt.getTopic().getScope());


    } else if (stmt.getObjekt().getKindId() == AmenService.OBJEKT_KIND_PLACE) {
      //Place
      statementBuilder.appendBlue("is the ");
      if (stmt.getTopic().isBest()) {
        statementBuilder.appendBlue(" Best Place for ");
      } else {
        statementBuilder.appendBlue(" Worst Place for ");
      }
      statementBuilder
        .appendBlue(stmt.getTopic().getDescription())
        .appendBlue(" ")
        .appendBlue(stmt.getTopic().getScope());

    } else if (stmt.getObjekt().getKindId() == AmenService.OBJEKT_KIND_PERSON) {
      statementBuilder.appendGreen("is the ");
      if (stmt.getTopic().isBest()) {
        statementBuilder.appendGreen(" Best ");
      } else {
        statementBuilder.appendGreen(" Worst ");
      }
      statementBuilder
        .appendGreen(stmt.getTopic().getDescription())
        .appendGreen(" ")
        .appendGreen(stmt.getTopic().getScope());
    }

    if (isDispute && disputingName != null) {

      statementBuilder.appendGray(" not ")
                      .appendGray(disputingName);

    }

    return statementBuilder;
  }

  public static CharSequence styleAmenWithColor(Amen amen, Context context) {

    Statement stmt = amen.getStatement();
    boolean isDispute = amen.isDispute();
    String disputingName = null;
    if (isDispute && amen.getReferringAmen() != null) {
      disputingName = amen.getReferringAmen().getStatement().getObjekt().getName();
    }
    return styleAmenWithColor(stmt, isDispute, disputingName, context);
  }


}
