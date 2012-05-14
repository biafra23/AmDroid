package com.jaeckel.amenoid;

import java.util.Date;

import com.actionbarsherlock.app.SherlockFragmentActivity;
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
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * User: biafra
 * Date: 11/9/11
 * Time: 12:11 AM
 */
public class SettingsActivity extends SherlockFragmentActivity implements SignupDialog.SignupDialogListener {

  private SharedPreferences prefs;
  private EditText          emailField;
  private EditText          passwordField;
  private Button            signInButton;
  private Button            signUpButton;
  private AmenService       service;
  private LoginAsyncTask    loginAsyncTask;

  private Gson gson = new GsonBuilder()
    .registerTypeAdapter(Date.class, new DateSerializer())
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .serializeNulls()
    .create();

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.settings);
    setTitle("Signing in");

    prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

    emailField = (EditText) findViewById(R.id.email_field);
    String email = prefs.getString(Constants.PREFS_USER_NAME, "");
    emailField.setText(email);

    passwordField = (EditText) findViewById(R.id.password_field);
    String password = prefs.getString(Constants.PREFS_PASSWORD, "");
    passwordField.setText(password);

  }

  @Override
  public void onResume() {
    super.onResume();
    signInButton = (Button) findViewById(R.id.signin_button);
    if (TextUtils.isEmpty(prefs.getString(Constants.PREFS_AUTH_TOKEN, ""))) {

      signInButton.setText("Sign In");
      signInButton.setOnClickListener(new SignInOnClickListener());
      passwordField.setEnabled(true);
      emailField.setEnabled(true);

    } else {

      signInButton.setText("Sign Out");
      signInButton.setOnClickListener(new SignOutOnClickListener());

      passwordField.setEnabled(false);
      emailField.setEnabled(false);
    }

    signUpButton = (Button) findViewById(R.id.signup_button);
    signUpButton.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View view) {
        Toast.makeText(SettingsActivity.this, "signup button clicked", Toast.LENGTH_SHORT).show();


        showEditDialog();

      }

    });
  }

  private void showEditDialog() {
    FragmentManager fm = getSupportFragmentManager();
    SignupDialog signupDialog = new SignupDialog();

    signupDialog.show(fm, "fragment_sign_up");


  }

  @Override public void onFinishSignupDialog(final String name, final String email, final String password) {

    Toast.makeText(SettingsActivity.this, "Dialog returend: name" + name + " email: " + email + "password: " + password, Toast.LENGTH_SHORT).show();


    new SignupAsyncTask(SettingsActivity.this, name, email, password).execute();


  }


  private class SignInOnClickListener implements View.OnClickListener {

    public void onClick(View view) {


      SharedPreferences.Editor editor = prefs.edit();

      editor.putString(Constants.PREFS_USER_NAME, emailField.getText().toString());
      editor.putString(Constants.PREFS_PASSWORD, passwordField.getText().toString());

      editor.commit();

      if (loginAsyncTask != null) {
        loginAsyncTask.cancel(true);
      }
      loginAsyncTask = new LoginAsyncTask(SettingsActivity.this);
      loginAsyncTask.execute();


    }
  }

  private class SignOutOnClickListener implements View.OnClickListener {

    public void onClick(View view) {

      SharedPreferences.Editor editor = prefs.edit();
      editor.putString(Constants.PREFS_AUTH_TOKEN, "");
      editor.putString(Constants.PREFS_USER_NAME, "");
      editor.putString(Constants.PREFS_PASSWORD, "");
      editor.putString(Constants.PREFS_LAST_AMENS + ":" + 0, "[]");
      editor.putString(Constants.PREFS_LAST_NEW_AMENS + ":" + 0, "[]");
      editor.putString(Constants.PREFS_LAST_AMENS + ":" + 1, "[]");
      editor.putString(Constants.PREFS_LAST_NEW_AMENS + ":" + 1, "[]");
      editor.putString(Constants.PREFS_ME, "{}");
      editor.commit();

      Toast.makeText(SettingsActivity.this, "Removed authtoken from preferences", Toast.LENGTH_SHORT).show();

      signInButton.setText("Sign In");
      signInButton.setOnClickListener(new SignInOnClickListener());
      passwordField.setEnabled(true);
      emailField.setEnabled(true);

      service = AmenoidApp.getInstance().getService();
      if (service != null) {
        service.removeAuthToken();
      }
      AmenListActivity.setShouldRefresh(true);
    }
  }


//
// LoginAsyncTask
//

  private class LoginAsyncTask extends AmenLibTask<Void, Integer, AmenService> {

    private ProgressDialog              loginProgressDialog;
    private InvalidCredentialsException loginFailed;
    private final static String TAG = "LoginAsyncTask";

    public LoginAsyncTask(Activity context) {
      super(context);
    }

    @Override
    protected void onPreExecute() {
      Log.d(TAG, "onPreExecute()");

      if (AmenoidApp.DEVELOPER_MODE) {
        Toast.makeText(SettingsActivity.this, "LoginAsyncTask.onPreExecute", Toast.LENGTH_SHORT).show();
      }
      loginProgressDialog = ProgressDialog.show(SettingsActivity.this, "",
                                                "Logging in. Please wait...", true);
      loginProgressDialog.show();
    }

    @Override
    protected AmenService wrappedDoInBackground(Void... voids) {

      Log.d(TAG, "wrappedDoInBackground()");

      String username = prefs.getString(Constants.PREFS_USER_NAME, null);
      String password = prefs.getString(Constants.PREFS_PASSWORD, null);
      AmenService amenService = null;
      try {

        amenService = AmenoidApp.getInstance().getService(username, password);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREFS_AUTH_TOKEN, amenService.getAuthToken());
        editor.putString(Constants.PREFS_ME, gson.toJson(amenService.getMe()));
        editor.commit();

      } catch (InvalidCredentialsException e) {

        loginFailed = e;
      }
      Log.d(TAG, "wrappedDoInBackground()");
      return amenService;
    }

    @Override
    protected void wrappedOnPostExecute(AmenService service) {

      Log.d(TAG, "wrappedOnPostExecute()");

      if (loginProgressDialog != null) {
        loginProgressDialog.hide();
        loginProgressDialog = null;
      }
      if (service == null) {
        Log.e(TAG, "wrappedOnPostExecute() service: " + service);
        if (loginFailed != null) {
          Toast.makeText(SettingsActivity.this, loginFailed.getMessage(), Toast.LENGTH_LONG).show();
        }
      } else {
        Log.e(TAG, "wrappedOnPostExecute() service.getAuthToken(): " + service.getAuthToken());
      }
      AmenListActivity.setShouldRefresh(true);
      //go back automatically after successful login
      if (service != null && service.getAuthToken() != null) {
        finish();
      }

    }

    @Override
    protected void onCancelled() {
      Log.d(TAG, "cancelled");
      if (loginProgressDialog != null) {
        loginProgressDialog.hide();
        loginProgressDialog = null;
      }
    }
  }

  //
  // SignupAsyncTask
  //

  private class SignupAsyncTask extends AmenLibTask<Void, Integer, AmenService> {

    private ProgressDialog              signupProgressDialog;
    private InvalidCredentialsException loginFailed;
    private SignupFailedException signupFailed;
    private final static String TAG = "LoginAsyncTask";

    final private String name;
    final private String email;
    final private String password;

    public SignupAsyncTask(Activity context, String name, String email, String password) {
      super(context);
      this.name = name;
      this.email = email;
      this.password = password;
    }

    @Override
    protected void onPreExecute() {
      Log.d(TAG, "onPreExecute()");

      if (AmenoidApp.DEVELOPER_MODE) {
        Toast.makeText(SettingsActivity.this, "SignupAsyncTask.onPreExecute", Toast.LENGTH_SHORT).show();
      }
      signupProgressDialog = ProgressDialog.show(SettingsActivity.this, "",
                                                 "Signing up. Please wait...", true);
      signupProgressDialog.show();
    }

    @Override
    protected AmenService wrappedDoInBackground(Void... voids) {

      Log.d(TAG, "wrappedDoInBackground()");

      AmenService amenService = null;
      try {
        amenService = AmenoidApp.getInstance().getService();

        User createdUser = amenService.signup(name, email, password);
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
//        e.printStackTrace();

        signupFailed = e;
      }
      Log.d(TAG, "wrappedDoInBackground()");
      return amenService;
    }

    @Override
    protected void wrappedOnPostExecute(AmenService service) {

      Log.d(TAG, "wrappedOnPostExecute()");

      if (signupProgressDialog != null) {
        signupProgressDialog.hide();
        signupProgressDialog = null;
      }
      if (service == null) {
        Log.e(TAG, "wrappedOnPostExecute() service: " + service);
        if (loginFailed != null) {
          Toast.makeText(SettingsActivity.this, loginFailed.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (signupFailed != null) {
          Toast.makeText(SettingsActivity.this, signupFailed.getField() + ": " + signupFailed.getMsg(), Toast.LENGTH_LONG).show();

        }
      } else {
        Log.e(TAG, "wrappedOnPostExecute() service.getAuthToken(): " + service.getAuthToken());
      }
      AmenListActivity.setShouldRefresh(true);
      //go back automatically after successful login
      if (service != null && service.getAuthToken() != null) {
        finish();
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