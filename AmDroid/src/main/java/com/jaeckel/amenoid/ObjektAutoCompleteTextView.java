package com.jaeckel.amenoid;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import com.jaeckel.amenoid.api.model.Objekt;

/**
 * User: biafra
 * Date: 10/4/11
 * Time: 12:51 AM
 */
public class ObjektAutoCompleteTextView extends AutoCompleteTextView {

  public ObjektAutoCompleteTextView(Context context) {
    super(context);
  }

  public ObjektAutoCompleteTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ObjektAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  protected CharSequence convertSelectionToString (Object selectedItem) {

    return ((Objekt)selectedItem).getName();
  }

}
