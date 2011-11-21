package com.jaeckel.amenoid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.InvalidCredentialsException;
import com.jaeckel.amenoid.api.model.DateSerializer;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.util.AmenLibTask;

import java.util.Date;

/**
 * User: biafra
 * Date: 11/9/11
 * Time: 12:11 AM
 */
public class SettingsActivity extends Activity {

  private SharedPreferences prefs;
  private EditText          emailField;
  private EditText          passwordField;
  private Button            signInButton;
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
    setTitle("Amenoid/Signing in");

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
}