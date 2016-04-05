package uk.co.liammartin.shout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewShout extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //TODO: Implement a Shout maker async task with loading spinner like login screen

    //Update times and constants
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final int MAX_SHOUT_LENGTH = 70;
    private final String DEFAULT = "null";

    //Debug tag
    final String TAG = "NewShout.java";

    //Views
    public EditText newShoutMainText;
    private TextView charCount;

    //Google Location API variables
    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    protected String mLastUpdateTime;
    protected LocationRequest nowLocationRequest;

    //class level variables
    private String userId;
    private String shoutBodyText;

    //OkHttpClient to make network requests
    OkHttpClient client = new OkHttpClient();

    //Using a TextWatcher to update the character counter as the user types
    private final TextWatcher charTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        //Using this method to change the value of the character counter whenever the user changes
        //the shout main EditText
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int shoutLength = Integer.parseInt(String.valueOf(newShoutMainText.length()));
            String charsRemaining = String.valueOf(MAX_SHOUT_LENGTH - shoutLength);
            if (MAX_SHOUT_LENGTH - shoutLength < 0) {
                charCount.setTextColor(ContextCompat.getColorStateList
                        (getApplicationContext(), R.color.warning));
            } else {
                charCount.setTextColor(ContextCompat.getColorStateList
                        (getApplicationContext(), R.color.secondary_text));
            }

            charCount.setText(charsRemaining);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shout);
        charCount = (TextView) this.findViewById(R.id.char_count);
        newShoutMainText = (EditText) this.findViewById(R.id.new_shout_main_text);
        newShoutMainText.addTextChangedListener(charTextWatcher);

        SharedPreferences sharedPreferences =
                getSharedPreferences("userData", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", DEFAULT);

        if (userId.equals(DEFAULT)) {
            Toast.makeText(getApplicationContext(), "There was a problem, please login again",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        buildGoogleApiClient();
    }

    public void tryToMakeShout(View view) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    startNowLocationUpdate();
                    shoutBodyText = newShoutMainText.getText().toString();
                } catch (final Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "There was a problem making your shout, please try again" +
                                    "later",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    /**
     * The method that sends the shout to the server
     *
     * @param message The message you want to send to the server
     * @throws Exception if there is an unexpected response from the server
     */
    public void makeShout(String message) throws Exception {

        //Adding a post form
        RequestBody formBody = new FormBody.Builder()
                .add("SHOUTER_ID", userId)
                .add("USER_LAT", String.valueOf(mCurrentLocation.getLatitude()))
                .add("USER_LON", String.valueOf(mCurrentLocation.getLongitude()))
                .add("DESCRIPTION", message)
                .build();
        //Building our request
        Request request = new Request.Builder()
                .url("http://134.83.83.25:47309/MakeShout")
                .post(formBody)
                .build();

        //Queue up the client call to server
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.toString());
            }

            //Getting our response (no need to worry because its a post)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Shout made!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "There was a problem making your shout, please try again" +
                                            "later",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    Log.d(TAG, e.toString());
                }
            }
        });

    }

    protected void startNowLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, nowLocationRequest, this);
    }

    protected void createNowLocationRequest() {
        nowLocationRequest = new LocationRequest();
        nowLocationRequest.setNumUpdates(1);
        nowLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        nowLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        nowLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Builds a GoogleApiClient with the relevant APIs
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createNowLocationRequest();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location.getLongitude() + " " + location.getLatitude());
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.d(TAG, "onLocationChanged: "
                + mCurrentLocation.getLatitude()
                + mCurrentLocation.getLongitude());
        try {
            makeShout(shoutBodyText);
        } catch (Exception e) {
            Log.d(TAG, "onLocationChanged: " + e.toString());
            Toast.makeText(getApplicationContext(),
                    "There was a problem when the location was changed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " +
                connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /*
    Thank you to Google for this set of tutorials which allowed me to make my app
    location aware using the latest APIs: http://developer.android.com/training/location/index.html
    */
}