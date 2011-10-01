package com.jaeckel.amdroid.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;

/**
 * User: biafra
 * Date: 9/30/11
 * Time: 2:20 AM
 */
public class StyleableSpannableStringBuilder extends SpannableStringBuilder {


  private static final String TAG = "StyleableSpannableStringBuilder";

  public StyleableSpannableStringBuilder appendWithStyle(CharacterStyle c, CharSequence text) {
    super.append(text);
    int startPos = length() - text.length();
    setSpan(c, startPos, length(), 0);
    return this;
  }

  public StyleableSpannableStringBuilder appendBold(CharSequence text) {
    Log.d(TAG, "appendBold");
    return appendWithStyle(new StyleSpan(Typeface.BOLD), text);
  }
  public StyleableSpannableStringBuilder appendGreen(CharSequence text) {
    Log.d(TAG, "appendGreen");
    return appendWithStyle(new StyleSpan(Color.GREEN), text);
  }
  public StyleableSpannableStringBuilder appendOrange(CharSequence text) {
    Log.d(TAG, "appendOrange");
    return appendWithStyle(new StyleSpan(Color.RED), text);
  }
  public StyleableSpannableStringBuilder appendBlue(CharSequence text) {
    Log.d(TAG, "appendBlue");
    return appendWithStyle(new StyleSpan(Color.BLUE), text);
  }

  public StyleableSpannableStringBuilder appendSuper(CharSequence text) {
    Log.d(TAG, "appendSuper");
    return appendWithStyle(new StyleSpan(Color.BLUE), text);
  }
}
