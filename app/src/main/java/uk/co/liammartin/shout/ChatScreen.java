package uk.co.liammartin.shout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatScreen extends Activity {
    final String TAG = "ChatScreen.java";

    //OkHttpClient
    private final OkHttpClient client = new OkHttpClient();

    //Views
    TextView chatHeader;
    RecyclerView rv;
    EditText message_input_box;

    //Data
    List<message> messages = new ArrayList<>();
    List<String> message_bodies = new ArrayList<>();
    List<Integer> from_ids = new ArrayList<>();
    List<Integer> to_ids = new ArrayList<>();
    String chattingToUsername;
    String chattingToId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting the activity up
        setContentView(R.layout.chat_screen);
        chatHeader = (TextView) findViewById(R.id.chat_header);
        message_input_box = (EditText) findViewById(R.id.message_input);

        //Getting data from the Intent
        Bundle chat_data = getIntent().getExtras();
        chattingToUsername = chat_data.getString("username");
        chattingToId = chat_data.getString("id");
        chatHeader.setText(getString(R.string.chat_header, chattingToUsername));
        //Get the RecyclerView
        rv = (RecyclerView) findViewById(R.id.chat_rv);

        //Create a LinearLayoutManager and set it to the RecyclerView
        //This will mean the RecyclerView will add items below each other
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        //Create an instance of RVAdapter so we can set up the RecyclerView using an adapter
        initializeAdapter();

        //Pull the data down from the server and create data objects from them
        initializeData();
    }

    /**
     * Initialize the data
     */
    public void initializeData() {
        try {
            getMessages();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Create an instance of RVAdapter (Recycler View Adapter) using an ArrayList of data
     * objects, in this case we are using user objects (from user.java)
     */
    private void initializeAdapter() {
        ChatRVAdapter adapter = new ChatRVAdapter(messages);
        rv.setAdapter(adapter);
    }

    /**
     * Gets the messages in the chat from the server.
     *
     * @throws Exception if response is not received
     */
    public void getMessages() throws Exception {

        //Adding a post form
        RequestBody formBody = new FormBody.Builder()
                .add("USER_NAME", chattingToUsername)
                .add("CHATTING_TO", chattingToId)
                .build();
        //Building our request
        Request request = new Request.Builder()
                .url("http://134.83.83.25:47309/Chat")
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

                //Taking the response and updating the messages on the *MAIN THREAD*
                responseString = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayMessages(responseString);
                        rv.getAdapter().notifyDataSetChanged();
                    }
                });
                try {
                    Thread.sleep(2000, 100);
                    getMessages();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });

    }

    /**
     * Gets the message contents and tries to send the message to the server
     */
    public void sendChat(View view) {
        String message = message_input_box.getText().toString().trim();
        try {
            makeChat(message);
            message_input_box.setText("");
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    /**
     * The method that sends the chat message to the server with the receivers ID
     *
     * @param message The message you want to send to the server
     * @throws Exception if there is an unexpected response from the server
     */
    public void makeChat(String message) throws Exception {

        //Adding a post form
        RequestBody formBody = new FormBody.Builder()
                .add("MESSAGE", message)
                .add("CHATTING_TO", chattingToId)
                .build();
        //Building our request
        Request request = new Request.Builder()
                .url("http://134.83.83.25:47309/MakeChat")
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
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });

    }

    /**
     * Display all of the entries from the JSONArray string in the RecyclerView
     *
     * @param response The JSONArray received from the server
     */
    private void displayMessages(String response) {
        try {
            message_bodies.clear();
            messages.clear();
            to_ids.clear();
            from_ids.clear();
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject row = array.getJSONObject(i);
                Log.d(TAG, row.toString());
                Log.d(TAG, row.getString("MESSAGE"));
                message_bodies.add(row.getString("MESSAGE"));
                to_ids.add(row.getInt("TO_ID"));
                from_ids.add(row.getInt("FROM_ID"));
            }
            for (int i = 0; i < message_bodies.size(); i++) {
                messages.add(new message(to_ids.get(i), from_ids.get(i), message_bodies.get(i)));
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

}
