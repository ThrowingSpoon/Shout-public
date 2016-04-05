package uk.co.liammartin.shout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //Update times
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    //Shared Preferences keys
    final String DEFAULT = "null";
    final String EMAIL_KEY = "email";
    final String PASSWORD_KEY = "password";
    final String USER_ID_KEY = "user_id";
    final String USER_DATA_KEY = "userData";

    //Debug tag
    final String TAG = "MainActivity.java";

    //OkHttpClient to make network requests
    private final OkHttpClient client = new OkHttpClient();

    //List to hold all of the shout data objects
    public List<Shout> shouts;

    //Google Location API variables
    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    protected String mLastUpdateTime;
    protected LocationRequest nowLocationRequest;
    double user_lat;
    double user_lon;
    double radius = 1000000;

    //Views
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: get filter preferences
        //TODO: apply filter preferences (maybe onresume or something?)
        //TODO: Handle what to do with logged in user data (not show their own shouts etc)
        //TODO: Implement refresh function

        SharedPreferences sharedPreferences =
                getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, DEFAULT);
        String password = sharedPreferences.getString(PASSWORD_KEY, DEFAULT);
        String userId = sharedPreferences.getString(USER_ID_KEY, DEFAULT);

        Log.d(TAG, "MainActivity onCreate: " + email + " " + password + " " + userId);

        //Creates the window where we will place the UI
        setContentView(R.layout.activity_main);

        //Get the RecyclerView
        rv = (RecyclerView) findViewById(R.id.rv);

        //Create a LinearLayoutManager and set it to the RecyclerView
        //This will mean the RecyclerView will add items below eachother
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        buildGoogleApiClient();

    }

    public void getShouts() throws Exception {

        //Adding a post form
        RequestBody formBody = new FormBody.Builder()
                .add("latitude", String.valueOf(user_lat))
                .add("longitude", String.valueOf(user_lon))
                .add("distance", String.valueOf(radius))
                .build();
        //Building our request
        Request request = new Request.Builder()
                .url("http://134.83.83.25:47309/GetShoutsPost")
                .post(formBody)
                .build();

        //Queue up the client call to server
        client.newCall(request).enqueue(new Callback() {

            String responseString;

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.toString());
            }

            //Getting our response
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                //Displaying the data in logcat
                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                responseString = response.body().string();

                //DISPLAYING SHOUTS
                try {
                    //Clearing previous data
                    shouts.clear();

                    JSONArray array = new JSONArray(responseString);

                    //repopulating data
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        Log.d(TAG, row.toString());
                        Log.d(TAG, row.getString("USERNAME"));
                        int imageID = Integer.parseInt(row.getString("PROFILE_IMAGE_ID"));

                        //Building the URI for the profile image
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http")
                                .authority("134.83.83.25:47309")
                                .appendPath("LiamImageDB")
                                .appendQueryParameter("imageid", String.valueOf(imageID));
                        Uri image_uri = builder.build();
                        String image_url = URLDecoder.decode(image_uri.toString(), "UTF-8");
                        Log.d("URI URL", "URL created: " + image_url);

                        shouts.add(
                                new Shout(
                                        (row.getString("USERNAME")),
                                        (row.getString("distance_in_km")),
                                        "★★★★★",
                                        (row.getString("DESCRIPTION")),
                                        (image_url)
                                )
                        );
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }

                //Update the shouts on the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rv.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        });
    }

    /**
     * Create Shout data objects using data from the server
     */
    private void initializeData() {
        try {
            shouts = new ArrayList<>();
            getShouts();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Create an instance of RVAdapter (Recycler View Adapter) using an ArrayList of data
     * objects, in this case we are using Shout objects (from Shout.java)
     */
    private void initializeAdapter() {
        RVAdapter adapter = new RVAdapter(shouts);
        rv.setAdapter(adapter);
    }

    public void openFilterMenu(View view) {
        //Creating the Intent to go to the ShoutFilter screen
        Intent getNameScreenIntent = new Intent(this, ShoutFilter.class);

        //Adding the 'No History' flag so the user cannot use the back button to go back
        getNameScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        //Start the shout filter intent!
        startActivity(getNameScreenIntent);

        //Animating the activity in and out nicely
        overridePendingTransition(R.anim.slide_in_bottom_to_top, R.anim.slide_out_top_to_bottom);
    }

    public void openNewShoutActivity(View view) {
        //Creating the Intent to go to the ShoutFilter screen
        Intent getNameScreenIntent = new Intent(this, NewShout.class);

        //Start the shout filter intent!
        startActivity(getNameScreenIntent);
    }

    public void openMyProfileActivity(View view) {
        Intent myProfileIntent = new Intent(this, myProfileActivity.class);
        startActivity(myProfileIntent);
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
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        createNowLocationRequest();
        startNowLocationUpdate();

    }

    @Override
    public void onConnectionSuspended(int i) {
        //If the Google API Client connection was interrupted we will try to reconnect.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.d(TAG, "onLocationChanged: "
                + mCurrentLocation.getLatitude() + " "
                + mCurrentLocation.getLongitude());
        try {
            user_lat = mCurrentLocation.getLatitude();
            user_lon = mCurrentLocation.getLongitude();

            /* AFTER WE HAVE THE LOCATION NOW SET UP THE SHOUT LIST */

            //Create instances for data
            initializeData();

            //Create an instance of RVAdapter so we can set up the RecyclerView using
            //an adapter
            initializeAdapter();

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