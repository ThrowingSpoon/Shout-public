package uk.co.liammartin.shout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class myProfileActivity extends Activity {
    //Tag for debugging
    final String TAG = "myProfileActivity.java";

    //Shared Preferences keys
    final String DEFAULT = "null";
    final String EMAIL_KEY = "email";
    final String PASSWORD_KEY = "password";
    final String USER_ID_KEY = "user_id";
    final String USER_DATA_KEY = "userData";

    //Views
    ImageView profilePic;
    TextView name;
    TextView username;
    Button logoutButton;

    //Class level variables
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inflating our view from the XML layout file
        setContentView(R.layout.my_profile_screen);

        //Getting SharedPreferences values
        SharedPreferences sharedPreferences =
                getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);
        userId = sharedPreferences.getString(USER_ID_KEY, DEFAULT);

        //Finding our views
        profilePic = (ImageView) findViewById(R.id.profile_picture);
        name = (TextView) findViewById(R.id.full_name);
        username = (TextView) findViewById(R.id.username);
        logoutButton = (Button) findViewById(R.id.log_out_button);

        //When the logout button is clicked
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences =
                        getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putString(EMAIL_KEY, DEFAULT);
                editor.putString(PASSWORD_KEY, DEFAULT);
                editor.putString(USER_ID_KEY, DEFAULT);
                editor.apply();

                Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                //Set flags to remove other logged in activities from the back stack
                //So the user cannot use the back button to get back to the logged in state
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                Toast.makeText(getApplicationContext(), "Logged out!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
