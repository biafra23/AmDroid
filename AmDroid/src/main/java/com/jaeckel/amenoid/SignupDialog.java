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


  private EditText nameEditText;
  private EditText emailEditText;
  private EditText passwordEditText;
//  private EditText mEditText;

  public SignupDialog() {
    // Empty constructor required for DialogFragment
  }

  public interface SignupDialogListener {
    void onFinishSignupDialog(String name, String email, String password);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {


    setStyle(0, R.style.Theme_HoloEverywhereDark_Sherlock);

    View view = inflater.inflate(R.layout.fragment_sign_up, container);
    nameEditText = (EditText) view.findViewById(R.id.name_edit_text);
    emailEditText = (EditText) view.findViewById(R.id.email_edit_text);
    passwordEditText = (EditText) view.findViewById(R.id.passowrd_edit_text);

    getDialog().setTitle("Sign up for getamen.com");


    // Show soft keyboard automatically
    nameEditText.requestFocus();
    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    passwordEditText.setOnEditorActionListener(this);

    return view;
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (EditorInfo.IME_ACTION_DONE == actionId) {
      // Return input text to activity
      SignupDialogListener activity = (SignupDialogListener) getActivity();
      activity.onFinishSignupDialog(nameEditText.getText().toString(),
                                    emailEditText.getText().toString(),
                                    passwordEditText.getText().toString());
      this.dismiss();
      return true;
    }
    return false;
  }

}
