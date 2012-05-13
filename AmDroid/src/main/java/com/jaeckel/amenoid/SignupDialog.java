package com.jaeckel.amenoid;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author biafra
 * @date 5/13/12 11:36 PM
 */
public class SignupDialog extends DialogFragment implements TextView.OnEditorActionListener {


  private EditText mEditText;

  public SignupDialog() {
    // Empty constructor required for DialogFragment
  }

  public interface SignupDialogListener {
    void onFinishSignupDialog(String inputText);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {


    View view = inflater.inflate(R.layout.fragment_sign_up, container);
    mEditText = (EditText) view.findViewById(R.id.txt_your_name);
    getDialog().setTitle("Hello");

    // Show soft keyboard automatically
    mEditText.requestFocus();
    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    mEditText.setOnEditorActionListener(this);

    return view;
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (EditorInfo.IME_ACTION_DONE == actionId) {
      // Return input text to activity
      SignupDialogListener activity = (SignupDialogListener) getActivity();
      activity.onFinishSignupDialog(mEditText.getText().toString());
      this.dismiss();
      return true;
    }
    return false;
  }

}
