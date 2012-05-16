package com.jaeckel.amenoid;

import java.util.Date;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.InvalidCredentialsException;
import com.jaeckel.amenoid.api.SignupFailedException;
import com.jaeckel.amenoid.api.model.DateSerializer;
import com.jaeckel.amenoid.api.model.User;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.AmenLibTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author biafra
 * @date 5/13/12 11:36 PM
 */
public class SignupDialog extends DialogFragment implements TextView.OnEditorActionListener {


  private String name;
  private String email;
  private String password;

  private EditText nameEditText;
  private EditText emailEditText;
  private EditText passwordEditText;

  private Button cancelButton;
  private Button submitButton;

  private SharedPreferences prefs;

//  private EditText mEditText;

  private static final String TAG = SignupDialog.class.getSimpleName();

  public SignupDialog() {
    // Empty constructor required for DialogFragment
  }

  public interface SignupDialogListener {
    void onFinishSignupDialog(String name, String email, String password);
  }

  @Override
  public void onCreate(Bundle save) {
    super.onCreate(save);
    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {


    View view = inflater.inflate(R.layout.fragment_sign_up, container);
    nameEditText = (EditText) view.findViewById(R.id.name_edit_text);
    if (!TextUtils.isEmpty(name)) {
      nameEditText.setText(name);
    }
    emailEditText = (EditText) view.findViewById(R.id.email_edit_text);
    if (!TextUtils.isEmpty(email)) {
      emailEditText.setText(email);
    }
    passwordEditText = (EditText) view.findViewById(R.id.passowrd_edit_text);
    if (!TextUtils.isEmpty(password)) {
      passwordEditText.setText(password);
    }
    cancelButton = (Button) view.findViewById(R.id.button_cancel);
    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        SignupDialog.this.dismiss();
      }
    });
    submitButton = (Button) view.findViewById(R.id.button_submit);
    submitButton.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View view) {

        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        new SignupAsyncTask(getActivity(), name, email, password).execute();
      }
    });
    getDialog().setTitle("Sign up for getamen.com");

    nameEditText.setNextFocusDownId(R.id.email_edit_text);
    emailEditText.setNextFocusDownId(R.id.passowrd_edit_text);

    // Show soft keyboard automatically
    nameEditText.requestFocus();
    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    passwordEditText.setOnEditorActionListener(this);

    return view;
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (EditorInfo.IME_ACTION_DONE == actionId) {
      // Return input text to dialogListener

      return true;
    }
    if (EditorInfo.IME_ACTION_NEXT == actionId) {
      Log.d(TAG, "Focus next!");
      // sendfocus down to the next EditText

      name = nameEditText.getText().toString();
      email = emailEditText.getText().toString();
      password = passwordEditText.getText().toString();

      new SignupAsyncTask(getActivity(), name, email, password);

      return true;
    }
    return false;
  }

  private void returnValues() {

    SignupDialogListener dialogListener = (SignupDialogListener) getActivity();
    dialogListener.onFinishSignupDialog(nameEditText.getText().toString(),
                                        emailEditText.getText().toString(),
                                        passwordEditText.getText().toString());
    this.dismiss();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  //----------------------------------------------
  //             SignupAsyncTask
  //----------------------------------------------

  private class SignupAsyncTask extends AmenLibTask<Void, Integer, User> {

    private ProgressDialog              signupProgressDialog;
    private InvalidCredentialsException loginFailed;
    private SignupFailedException       signupFailed;

    private final static String TAG = "LoginAsyncTask";

    final private String name;
    final private String email;
    final private String password;

    private Activity activity;

    private Gson gson = new GsonBuilder()
      .registerTypeAdapter(Date.class, new DateSerializer())
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .serializeNulls()
      .create();


    public SignupAsyncTask(Activity activity, String name, String email, String password) {
      super(activity);
      this.activity = activity;
      this.name = name;
      this.email = email;
      this.password = password;
    }

    @Override
    protected void onPreExecute() {
      Log.d(TAG, "onPreExecute()");

      if (AmenoidApp.DEVELOPER_MODE) {
        Toast.makeText(activity, "SignupAsyncTask.onPreExecute", Toast.LENGTH_SHORT).show();
      }
      signupProgressDialog = ProgressDialog.show(activity, "",
                                                 "Signing up. Please wait...", true);

      signupProgressDialog.show();
    }

    @Override
    protected User wrappedDoInBackground(Void... voids) {

      Log.d(TAG, "wrappedDoInBackground()");

      AmenService amenService = null;
      User createdUser = null;
      try {
        amenService = AmenoidApp.getInstance().getService();

        createdUser = amenService.signup(name, email, password);
        if (createdUser.getId() > 0) {

          //Save credentials used to createdUser account
          SharedPreferences.Editor editor = prefs.edit();
          editor.putString(Constants.PREFS_USER_NAME, name);
          editor.putString(Constants.PREFS_PASSWORD, password);
          editor.commit();
        }

        amenService = AmenoidApp.getInstance().getService(name, password);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREFS_AUTH_TOKEN, amenService.getAuthToken());
        editor.putString(Constants.PREFS_ME, gson.toJson(amenService.getMe()));
        editor.commit();

      } catch (InvalidCredentialsException e) {

        loginFailed = e;

      } catch (SignupFailedException e) {
        e.printStackTrace();

        signupFailed = e;
      }
      Log.d(TAG, "wrappedDoInBackground()");
      return createdUser;
    }

    @Override
    protected void wrappedOnPostExecute(User user) {

      Log.d(TAG, "wrappedOnPostExecute()");

      if (signupProgressDialog != null) {
        signupProgressDialog.hide();
        signupProgressDialog = null;
      }

      if (loginFailed != null) {

        Toast.makeText(activity, loginFailed.getMessage(), Toast.LENGTH_LONG).show();

      } else if (signupFailed != null) {

        Toast.makeText(activity, signupFailed.getField() + ": " + signupFailed.getMsg(), Toast.LENGTH_LONG).show();

      } else {

        AmenListActivity.setShouldRefresh(true);
        //go back automatically after successful login
        if (user.getId() > 0) {
          returnValues();
        }
      }


    }

//    @Override
//    protected void onCancelled() {
//      Log.d(TAG, "cancelled");
//      if (signupProgressDialog != null) {
//        signupProgressDialog.hide();
//        signupProgressDialog = null;
//      }
//    }
  }


}
