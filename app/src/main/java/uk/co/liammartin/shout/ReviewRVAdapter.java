package uk.co.liammartin.shout;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class ReviewRVAdapter extends RecyclerView.Adapter<ReviewRVAdapter.ReviewViewHolder> {

    List<Shout> shouts; //TODO: change to reviews

    ReviewRVAdapter(List<Shout> shouts) {
        this.shouts = shouts;
    } //TODO: change to reviews

    /**
     * Called by the RecyclerView when it starts observing the adapter (we are using the
     * ReviewRVAdapter class)
     *
     * @param recyclerView The RecyclerView instance which started observing this adapter
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Called to give the RecyclerView a ViewHolder to represent a review item
     *
     * @param viewGroup The parent ViewGroup that the review ViewHolder will added to after
     *                  it is bound to an adapter position (adapter will contain each
     *                  individual review view, the TextViews and ImageView)
     * @param i         The view type
     * @return The ViewHolder for a review so that the RecyclerView to display reviews
     */
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //This part shows us that we are inflating our review_item xml file -------v
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_item, viewGroup, false);
        Log.d("DEBUGGING", "OnCreateViewHolder Called with position " + i);
        return new ReviewViewHolder(v);
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
     * This will be called by the RecyclerView to display the data, from the 'reviews' object at the
     * specified position (at i)
     *
     * @param ReviewViewHolder The ViewHolder which should be updated to represent the contents of
     *                         the item at the given position in the data set
     * @param i                The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(final ReviewViewHolder ReviewViewHolder, final int i) {
        ReviewViewHolder.username.setText(shouts.get(i).username);
//        ReviewViewHolder.distance.setText(shouts.get(i).distance);
        ReviewViewHolder.rating.setText(shouts.get(i).rating); //TODO: change to review
//        ReviewViewHolder.description.setText(shouts.get(i).description);
        /*Glide.with(ReviewViewHolder.profilePic.getContext())
                .load(shouts.get(i).profilePic)
                .placeholder(R.drawable.default_profile_pic)
                .into(ReviewViewHolder.profilePic);
*/ //TODO: IMPLEMENT REVIEW PICS DOWNLOADING FROM SERVER, remember they are circles

    }

    /**
     * Overriding this method to point to which object to get the size from (in this case
     * we are using the reviews object)
     *
     * @return The total number of items in the adapter
     */
    @Override
    public int getItemCount() {
        return shouts.size();
    } //TODO: make review data object etc

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Creating a CardView variable which will be each one of our shouts
        CardView individual_review_card_view;
        //Creating variables for holding the items that will be in the CardViews
        TextView username;
        TextView rating;
        TextView body;
        ImageButton profilePic;


        /**
         * Taking the View we passed it when inflating review_item.xml and then finding
         * all of the individual views inside of it (the TextViews and ImageView at the moment)
         *
         * @param itemView the review_item.xml inflated view (containing TextViews and ImageView)
         */
        ReviewViewHolder(View itemView) {
            super(itemView);
            individual_review_card_view = (CardView) itemView.findViewById(R.id.individual_review_card_view);
            username = (TextView) itemView.findViewById(R.id.username);
            rating = (TextView) itemView.findViewById(R.id.rating);
            body = (TextView) itemView.findViewById(R.id.description);
            profilePic = (ImageButton) itemView.findViewById(R.id.review_profile_pic);

            individual_review_card_view.setOnClickListener(this);
        }

        //Implementing a click action at the ViewHolder level using getAdapterPosition(); to find
        //the position of the element we want to do something with. we have already set an
        //onclick listener on the item we want to listen for the click (the whole review cardView)
        //and can do anything now!
        @Override
        public void onClick(View v) {
            if (getAdapterPosition() >= 0) {

                //Getting our current context
                Context current_context = v.getContext();

                //Creating the Intent to go to the Respond to review screen TODO: change to reviews
                Intent respond_to_shout = new Intent(current_context, RespondToShout.class);

                //adding data to pass to respond screen TODO: update data to reviews
                int position = getAdapterPosition();
                respond_to_shout.putExtra("username", shouts.get(position).username);

                //start the respond to review activity TODO: change to reviews
                current_context.startActivity(respond_to_shout);
            }
        }
    }
}