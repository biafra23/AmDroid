package com.jaeckel.amenoid.util;

/**
 * @author biafra
 * @date 4/19/12 10:28 PM
 */

import com.github.ignition.core.widgets.RemoteImageView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class AspectRatioImageView extends RemoteImageView {

  public AspectRatioImageView(Context context, String imageUrl, boolean autoLoad) {
    super(context, imageUrl, autoLoad);
  }

  public AspectRatioImageView(Context context, String imageUrl, Drawable progressDrawable, Drawable errorDrawable, boolean autoLoad) {
    super(context, imageUrl, progressDrawable, errorDrawable, autoLoad);
  }

  public AspectRatioImageView(Context context, AttributeSet attributes) {
    super(context, attributes);
  }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
      if (getDrawable() != null && getDrawable().getIntrinsicWidth() != 0) {
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
      } else {
        setMeasuredDimension(0, 0);
      }

    }
}