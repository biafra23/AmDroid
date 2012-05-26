package com.jaeckel.amenoid;

import java.util.Date;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaeckel.amenoid.api.AmenService;
import com.jaeckel.amenoid.api.InvalidCredentialsException;
import com.jaeckel.amenoid.api.model.DateSerializer;
import com.jaeckel.amenoid.app.AmenoidApp;
import com.jaeckel.amenoid.fragments.AmenListFragment;
import com.jaeckel.amenoid.util.AmenLibTask;
import com.jaeckel.amenoid.util.Log;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
  private TextView          passwordReset;

  private final static String TAG = SettingsActivity.class.getSimpleName();

  private Gson gson = new GsonBuilder()
    .registerTypeAdapter(Date.class, new DateSerializer())
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .serializeNulls()
    .create();

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.settings);
    passwordReset = (TextView) findViewById(R.id.forgot_password);
    passwordReset.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        resetPassword(null);
      }
    });
    setTitle("Signing in");
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

    emailField = (EditText) findViewById(R.id.email_field);
    String email = prefs.getString(Constants.PREFS_EMAIL, "");
    if (TextUtils.isEmpty(email)) {
      //For legacy users that have the email in the username field
      email = prefs.getString(Constants.PREFS_USER_NAME, "");
      if (!TextUtils.isEmpty(email) && email.contains("@")) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREFS_EMAIL, email);
        editor.commit();
      }
    }
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

        showEditDialog();

      }

    });
  }

  private void showEditDialog() {
    FragmentManager fm = getSupportFragmentManager();
    SignupDialog signupDialog = new SignupDialog();
    signupDialog.setEmail(emailField.getText().toString());
    signupDialog.setPassword(passwordField.getText().toString());

    if (Build.VERSION.SDK_INT <= 10 /* Build.VERSION_CODES.GINGERBREAD_MR1 */) {
      //2.3 or lower
      signupDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_HoloEverywhereDark_Sherlock);

    }

    signupDialog.show(fm, "fragment_sign_up");


  }

  @Override public void onFinishSignupDialog(final String name, final String email, final String password) {

    emailField.setText(email);
    passwordField.setText(password);

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
      loginAsyncTask.executeOnThreadPool();


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
      AmenListFragment.setShouldRefresh(true);
    }
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    Log.d(TAG, "onOptionsItemSelected -> item.getItemId(): " + item.getItemId());

    final Intent amenListIntent = new Intent(this, AmenDetailFragmentActivity.class);
    amenListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    switch (item.getItemId()) {

      case android.R.id.home: {
        startActivity(amenListIntent);
        return true;
      }
    }
    return false;
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

      loginProgressDialog = ProgressDialog.show(SettingsActivity.this, "",
                                                "Logging in. Please wait...", true);
      loginProgressDialog.show();
    }

    @Override
    protected AmenService wrappedDoInBackground(Void... voids) {

      Log.d(TAG, "wrappedDoInBackground()");

      String email = prefs.getString(Constants.PREFS_USER_NAME, null);
      String password = prefs.getString(Constants.PREFS_PASSWORD, null);
      AmenService amenService = null;
      try {

        amenService = AmenoidApp.getInstance().getService(email, password);

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
      AmenListFragment.setShouldRefresh(true);
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

  public void resetPassword(View v) {
    Log.d(TAG, "resetPassword");

    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://getamen.com/forgot-password"));
    startActivity(browserIntent);
  }
}