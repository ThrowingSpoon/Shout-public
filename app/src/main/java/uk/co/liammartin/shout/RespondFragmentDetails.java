package uk.co.liammartin.shout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class RespondFragmentDetails extends Fragment {
    //Views
    TextView username;
    TextView description;
    TextView distance;
    TextView rating;
    TextView review_username;
    TextView review_rating;
    TextView review_body;
    ImageView profile_pic;
    Button accept_button;
    Button decline_button;

    //Class scope variables
    int id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Create our view object by inflating it from our XML file
        return inflater.inflate(R.layout.respond_to_shout_details_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //Initialise all variables except for our views
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initialise our views
        //Finding the views
        username = (TextView) view.findViewById(R.id.username);
        description = (TextView) view.findViewById(R.id.description);
        rating = (TextView) view.findViewById(R.id.rating);
        distance = (TextView) view.findViewById(R.id.distance);
        review_username = (TextView) view.findViewById(R.id.review_username);
        review_rating = (TextView) view.findViewById(R.id.review_rating);
        review_body = (TextView) view.findViewById(R.id.review_body);
        profile_pic = (ImageView) view.findViewById(R.id.profile_picture);
        accept_button = (Button) view.findViewById(R.id.accept_button);
        decline_button = (Button) view.findViewById(R.id.decline_button);

        //Getting our data that the intent passed to us and putting it in the views
        Bundle shout_data = getActivity().getIntent().getExtras();
        final String USER = shout_data.getString("username");
        review_body.setText(getString(R.string.user_review_body_text, USER));
        username.setText(USER);
        distance.setText(shout_data.getString("distance") + "km");
        rating.setText(shout_data.getString("rating") + "(6)");
        description.setText(shout_data.getString("description"));
        String pic_url_string = shout_data.getString("profilepic");

        //Getting user image with Glide
        try {
            Glide.with(getContext())
                    .load(pic_url_string)
                    .placeholder(R.drawable.placeholder300x200)
                    .into(profile_pic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.d("URL Received: ", "URL Received in RespondFragment: " + pic_url_string);
        } catch (Exception e) {
            e.printStackTrace();
        }

        id = shout_data.getInt("id");

        //Accept Shout button
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundling the username and opening the chat screen using an intent
                Context current_context = v.getContext();
                Intent open_chat = new Intent(current_context, ChatScreen.class);
                open_chat.putExtra("username", USER);
                open_chat.putExtra("id", String.valueOf(id));
                current_context.startActivity(open_chat);
            }
        });

        //Decline Shout button
        decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                /*Intent open_main = new Intent(v.getContext(), MainActivity.class);
                v.getContext().startActivity(open_main);*/
            }
        });
    }
}
