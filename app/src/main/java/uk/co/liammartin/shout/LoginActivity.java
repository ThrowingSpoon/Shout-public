package uk.co.liammartin.shout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Login screen for Shout! - will take an email and password and check it against
 * details on the server
 */
public class LoginActivity extends AppCompatActivity {

    //Tag for debugging
    public final String TAG = "LoginActivity.java";

    //Default Shared Preferences keys
    final String DEFAULT = "null";
    final String EMAIL_KEY = "email";
    final String PASSWORD_KEY = "password";
    final String USER_ID_KEY = "user_id";
    final String USER_DATA_KEY = "userData";

    //Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    //Views
    private EditText emailView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;
    private TextView registerLink;
    private String userId;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //Find the views
        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.email_sign_in_button);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
        registerLink = (TextView) findViewById(R.id.link_registration);

        //Setting the editor action so you can click 'sign in' on the keyboard
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        //Setting onclick action to attempt login when the sign in button is clicked
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //Open the register activity when the register button is clicked
        registerLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
            }
        });

        //Checking the Shared Preferences to see if the user has logged in already, if they have
        //Then try the login again without them having to put in the same details
        SharedPreferences sharedPreferences =
                getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, DEFAULT);
        String password = sharedPreferences.getString(PASSWORD_KEY, DEFAULT);

        if (!email.equals(DEFAULT) && !password.equals(DEFAULT)) {
            emailView.setText(email);
            passwordView.setText(password);
            Log.d(TAG, "User already logged in: " + email + " so logging in again");
            attemptLogin();
        }

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Checks if the email is valid
     *
     * @param email The email string to check
     * @return true if valid, false if not
     */
    private boolean isEmailValid(String email) {
        //TODO: Replace this with better logic (Maybe regex?)
        return email.contains("@");
    }

    /**
     * Checks if the password is valid
     *
     * @param password The password string to check
     * @return true if valid, false if not
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with better logic (Maybe numbers and characters? regex?)
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form. compatible with older versions
     * of Android and will subsequently change animations.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Checks whether the user is registered on the server
     *
     * @param username The email of the user
     * @param password The password of the user
     * @return
     * @throws Exception If the response is unexpected
     */
    protected boolean checkLogin(String username, String password) throws Exception {
        final OkHttpClient client = new OkHttpClient();

        //Adding a post form
        RequestBody formBody = new FormBody.Builder()
                .add("EMAIL", String.valueOf(username))
                .add("PASSWORD", String.valueOf(password))
                .build();
        //Building our request
        Request request = new Request.Builder()
                .url("http://134.83.83.25:47309/LoginPost")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            //Indicates there is a problem on the server
            Log.d(TAG, "Unexpected response: " + response);
            return false;
        }

        String responseString = response.body().string();
        JSONArray jsonarray = new JSONArray(responseString);
        JSONObject firstRow = jsonarray.getJSONObject(0);
        userId = firstRow.getString("USER_ID");
        Log.d(TAG, String.valueOf(jsonarray.length()));
        Log.d(TAG, userId);

        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    /**
     * Asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;
        private final String password;

        UserLoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Checking against network here
            boolean validCredentials = false;
            try {
                validCredentials = checkLogin(email, password);
            } catch (Exception e) {
                return false;
            }

            // TODO: register the new account here.
            return validCredentials;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //TODO: successful login here (with user details storage):
                Toast.makeText(getApplicationContext(), "Successful login!", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences =
                                getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString(EMAIL_KEY, email);
                        editor.putString(PASSWORD_KEY, password);
                        editor.putString(USER_ID_KEY, userId);
                        Log.d(TAG, email + " " + password);
                        editor.commit();

                        Intent landingScreenIntent =
                                new Intent(getApplicationContext(), MainActivity.class);
                        landingScreenIntent.putExtra(EMAIL_KEY, email);
                        landingScreenIntent.putExtra(PASSWORD_KEY, password);
                        startActivity(landingScreenIntent);
                    }
                });
                finish();
            } else {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}