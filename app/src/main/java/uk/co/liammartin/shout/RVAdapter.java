package uk.co.liammartin.shout;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ShoutViewHolder> {

    List<Shout> shouts;

    RVAdapter(List<Shout> shouts) {
        this.shouts = shouts;
    }

    /**
     * Called by the RecyclerView when it starts observing the adapter (we are using the
     * RVAdapter class)
     *
     * @param recyclerView The RecyclerView instance which started observing this adapter
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Called to give the RecyclerView a ViewHolder to represent a Shout item
     *
     * @param viewGroup The parent ViewGroup that the Shout ViewHolder will added to after
     *                  it is bound to an adapter position (adapter will contain each
     *                  individual shout view, the TextViews and ImageView)
     * @param i         The view type
     * @return The ViewHolder for a Shout so that the RecyclerView to display shouts
     */
    @Override
    public ShoutViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //This part shows us that we are inflating our shout_item xml file -------v
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shout_item, viewGroup, false);
        Log.d("DEBUGGING", "OnCreateViewHolder Called");
        return new ShoutViewHolder(v);
    }

    /**
     * Remove an item from the data List
     *
     * @param position the position from which to remove the item
     */
    public void delete(int position) {
        shouts.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * This will be called by the RecyclerView to display the data, from the 'shouts' object at the
     * specified position (at i)
     *
     * @param shoutViewHolder The ViewHolder which should be updated to represent the contents of
     *                        the item at the given position in the data set
     * @param i               The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(final ShoutViewHolder shoutViewHolder, final int i) {

        shoutViewHolder.username.setText(shouts.get(i).username);
        shoutViewHolder.distance.setText(shouts.get(i).distance + " km");
        shoutViewHolder.rating.setText(shouts.get(i).rating);
        shoutViewHolder.description.setText(shouts.get(i).description);

        //Async locading profile images using Glide library
        try {
            Glide.with(shoutViewHolder.profilePic.getContext())
                    .load(shouts.get(i).profilePic)
                    .placeholder(R.drawable.placeholder300x200)
                    .into(shoutViewHolder.profilePic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.d("URL Received: ", "URL Received in RV Adapter: " + shouts.get(i).profilePic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Overriding this method to point to which object to get the size from (in this case
     * we are using the shouts object)
     *
     * @return The total number of items in the adapter
     */
    @Override
    public int getItemCount() {
        return shouts.size();
    }

    class ShoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Creating a CardView variable which will be each one of our shouts
        CardView individual_shout_card_view;
        //Creating variables for holding the items that will be in the CardViews
        TextView username;
        TextView distance;
        TextView rating;
        TextView description;
        ImageView profilePic;

        /**
         * Taking the View we passed it when inflating shout_item.xml and then finding
         * all of the individual views inside of it (the TextViews and ImageView at the moment)
         *
         * @param itemView the shout_item.xml inflated view (containing TextViews and ImageView)
         */
        ShoutViewHolder(View itemView) {
            super(itemView);
            individual_shout_card_view = (CardView) itemView.findViewById(R.id.individual_shout_card_view);
            username = (TextView) itemView.findViewById(R.id.username);
            distance = (TextView) itemView.findViewById(R.id.distance);
            rating = (TextView) itemView.findViewById(R.id.rating);
            description = (TextView) itemView.findViewById(R.id.description);
            profilePic = (ImageView) itemView.findViewById(R.id.profile_picture);

            individual_shout_card_view.setOnClickListener(this);
        }

        //Implementing a click action at the ViewHolder level using getAdapterPosition(); to find
        //the position of the element we want to do something with. we have already set an
        //onclick listener on the item we want to listen for the click (the whole shout cardView)
        //and can do anything now!
        @Override
        public void onClick(View v) {
            if (getAdapterPosition() >= 0) {
                //Getting our current context
                Context current_context = v.getContext();

                //Creating the Intent to go to the Respond to shout screen
                Intent respond_to_shout = new Intent(current_context, RespondToShout.class);

                //adding data to pass to respond screen
                int position = getAdapterPosition();
                respond_to_shout.putExtra("username", shouts.get(position).username);
                respond_to_shout.putExtra("distance", shouts.get(position).distance);
                respond_to_shout.putExtra("rating", shouts.get(position).rating);
                respond_to_shout.putExtra("description", shouts.get(position).description);
                respond_to_shout.putExtra("profilepic", shouts.get(position).profilePic);
                respond_to_shout.putExtra("id", position + 2);

                //start the respond to shout activity
                current_context.startActivity(respond_to_shout);
            }
        }
    }
}


