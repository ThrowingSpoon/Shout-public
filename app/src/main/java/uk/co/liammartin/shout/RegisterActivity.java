package uk.co.liammartin.shout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    //Debug Tag
    private static final String TAG = "RegisterActivity.java";

    //Views
    private Spinner genderSpinner;
    private EditText passwordView;
    private EditText userNameView;
    private EditText firstNameView;
    private EditText lastNameView;
    private EditText emailView;
    private EditText confirmPasswordView;
    private TextView loginLink;
    private Button signupButton;

    //Class level variables
    private Random rand;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Finding views
        passwordView = (EditText) findViewById(R.id.input_password);
        userNameView = (EditText) findViewById(R.id.input_username);
        firstNameView = (EditText) findViewById(R.id.input_first_name);
        lastNameView = (EditText) findViewById(R.id.input_last_name);
        emailView = (EditText) findViewById(R.id.input_email);
        confirmPasswordView = (EditText) findViewById(R.id.input_confirm_password);

        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);

        //Setting an adapter on the gender spinner with 'Male' and 'Female' optons
        ArrayAdapter<CharSequence> genderSpinnerAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.gender_array,
                        android.R.layout.simple_list_item_1);
        genderSpinner.setAdapter(genderSpinnerAdapter);

        //Getting references for buttons
        signupButton = (Button) findViewById(R.id.btn_signup);
        loginLink = (TextView) findViewById(R.id.link_login);

        //Setting listeners

        //When 'sign up' button is clicked
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validate input then try signing up on the server
                signup();
            }
        });

        //When the 'Existing user? Log in' text is clicked
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    /**
     * Validate the input of the registration form and try to send valid details to the
     * server
     * <p/>
     * TODO: Get a response from the server to check if the details have actually gone into the
     * database
     */
    public void signup() {
        Log.d(TAG, "Signup called");

        //Check if input is valid, if not then run onSignupFailed() and return
        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        final String email = emailView.getText().toString();
        final String password = passwordView.getText().toString();
        final String username = userNameView.getText().toString();
        final String firstName = firstNameView.getText().toString();
        final String lastName = lastNameView.getText().toString();
        final String gender;

        if (genderSpinner.getSelectedItem().toString().matches("Male")) {
            gender = "0";
        } else {
            gender = "1";
        }

        final OkHttpClient client = new OkHttpClient();

        try {

            //Adding a post form
            RequestBody formBody = new FormBody.Builder()
                    .add("USERNAME", username)
                    .add("FIRST_NAME", firstName)
                    .add("LAST_NAME", lastName)
                    .add("GENDER", gender)
                    .add("USER_LAT", String.valueOf(RandomDouble(51.53000, 51.53999)))
                    .add("USER_LON", String.valueOf(RandomDouble(0.4700, 0.4799)))
                    .add("PASSWORD", password)
                    .add("PROFILE_IMAGE_ID", "7")
                    .add("EMAIL", email)
                    .build();
            //Building our request
            Request request = new Request.Builder()
                    .url("http://134.83.83.25:47309/RegisterPost")
                    .post(formBody)
                    .build();

            Log.d(TAG, "Request Sent: " + request);
            Log.d(TAG, "request data: " + formBody.toString());


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Unexpected response");
                            progressDialog.hide();
                            onSignupFailed();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            progressDialog.cancel();
                            progressDialog.dismiss();
                            onSignupSuccess();
                        }
                    });
                }
            });

        } catch (Exception e) {
            Log.d(TAG, e.toString());
            onSignupFailed();
        }

    }

    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "Registration complete! Please now log in.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration failed, " +
                "Please try again later.", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String username = userNameView.getText().toString();
        String firstName = firstNameView.getText().toString();
        String lastName = lastNameView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        if (!(password.equals(confirmPassword))) {
            passwordView.setError("Passwords do not match");
            confirmPasswordView.setError("Passwords do not match");
        } else {
            passwordView.setError(null);
            confirmPasswordView.setError(null);
        }

        if (firstName.isEmpty()) {
            firstNameView.setError("Please input a First Name");
            valid = false;
        } else {
            firstNameView.setError(null);
        }

        if (lastName.isEmpty()) {
            lastNameView.setError("Please input a Last Name");
        } else {
            lastNameView.setError(null);
        }

        if (username.isEmpty()) {
            userNameView.setError("Please input a Username");
        } else {
            userNameView.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailView.setError("enter a valid email address");
            valid = false;
        } else {
            emailView.setError(null);
        }

        if (password.isEmpty()) {
            passwordView.setError("Please input a password");
            valid = false;
        } else {
            passwordView.setError(null);
        }

        return valid;
    }

    /**
     * Generate a random double between a given range, inclusive
     *
     * @param a Minimum range
     * @param b Maximum range
     * @return A random double between a and b
     */
    public double RandomDouble(double a, double b) {
        if (rand == null) {
            rand = new Random();
            rand.setSeed(System.nanoTime());
        }
        return ((b - a) * rand.nextDouble() + a);
    }
}