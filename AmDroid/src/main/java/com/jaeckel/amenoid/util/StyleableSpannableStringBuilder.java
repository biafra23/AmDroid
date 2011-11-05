package com.jaeckel.amenoid.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import com.jaeckel.amenoid.R;

/**
 * User: biafra
 * Date: 9/30/11
 * Time: 2:20 AM
 */
public class StyleableSpannableStringBuilder extends SpannableStringBuilder {

  private Context context;

  private static final String TAG = "StyleableSpannableStringBuilder";

  public StyleableSpannableStringBuilder(Context context) {
    this.context = context;
  }


  public StyleableSpannableStringBuilder appendWithStyle(ForegroundColorSpan c, CharSequence text) {
    super.append(text);
    int startPos = length() - text.length();
    setSpan(c, startPos, length(), 0);
    return this;
  }

  public StyleableSpannableStringBuilder appendWithStyle(CharacterStyle c, CharSequence text) {
    super.append(text);
    int startPos = length() - text.length();
    setSpan(c, startPos, length(), 0);
    return this;
  }

  public StyleableSpannableStringBuilder appendBold(CharSequence text) {
//    Log.d(TAG, "appendBold: " + text);
    return appendWithStyle(new StyleSpan(Typeface.BOLD), text);
  }

  public StyleableSpannableStringBuilder appendGreen(CharSequence text) {
//    Log.d(TAG, "appendGreen: " + text);
//    return appendWithStyle(new ForegroundColorSpan(Color.GREEN), text);
    return appendWithStyle(new ForegroundColorSpan(context.getResources().getColor(R.color.green)), text);
  }

  public StyleableSpannableStringBuilder appendOrange(CharSequence text) {
//    Log.d(TAG, "appendOrange: " + text);
//    final ForegroundColorSpan c = new ForegroundColorSpan(Color.RED);
    final ForegroundColorSpan c = new ForegroundColorSpan(context.getResources().getColor(R.color.orange));
    return appendWithStyle(c, text);
  }

  public StyleableSpannableStringBuilder appendBlue(CharSequence text) {
//    Log.d(TAG, "appendBlue: " + text);
//    return appendWithStyle(new ForegroundColorSpan(Color.BLUE), text);
    return appendWithStyle(new ForegroundColorSpan(context.getResources().getColor(R.color.blue)), text);
  }

  public StyleableSpannableStringBuilder appendLightGray(CharSequence text) {
//    Log.d(TAG, "appendBlue: " + text);
    return appendWithStyle(new ForegroundColorSpan(Color.LTGRAY), text);
  }

  public StyleableSpannableStringBuilder appendGray(CharSequence text) {
//    Log.d(TAG, "appendBlue: " + text);
    if (text != null) {
      return appendWithStyle(new ForegroundColorSpan(Color.GRAY), text);
    }
    return null;
  }
}
