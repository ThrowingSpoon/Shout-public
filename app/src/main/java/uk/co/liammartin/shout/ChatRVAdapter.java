package uk.co.liammartin.shout;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatRVAdapter extends RecyclerView.Adapter<ChatRVAdapter.MessageViewHolder> {

    //List of type message to hold the data for each message
    List<message> messages;

    //User ID hardcoded at the moment
    //TODO: implement multi-user chat
    int USER_ID = 1;

    ChatRVAdapter(List<message> messages) {
        this.messages = messages;
    }

    public void updateData(ArrayList<message> data) {
        messages.clear();
        messages.addAll(data);
        notifyDataSetChanged();
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
     * Called to give the RecyclerView a ViewHolder to represent a message item
     *
     * @param viewGroup The parent ViewGroup that the message ViewHolder will added to after
     *                  it is bound to an adapter position (adapter will contain each
     *                  individual message card view, the TextViews)
     * @param i         The view type
     * @return The ViewHolder for a message card so that the RecyclerView to display messages
     */
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //This part shows us that we are inflating our msg_card xml file -------v
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.msg_card, viewGroup, false);
        Log.d("DEBUGGING", "OnCreateViewHolder Called with position " + String.valueOf(i));
        return new MessageViewHolder(v);
    }

    /**
     * Remove an item from the data List
     *
     * @param position the position from which to remove the item
     */
    public void delete(int position) {
        messages.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * This will be called by the RecyclerView to display the data, from the 'messages' object at the
     * specified position (at i) and will display the message on the left or the right side depending
     * on whether the message was sent or received.
     *
     * @param messageViewHolder The ViewHolder which should be updated to represent the contents of
     *                          the item at the given position in the data set
     * @param i                 The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(final MessageViewHolder messageViewHolder, final int i) {
        if (messages.get(messageViewHolder.getAdapterPosition()).from_id != USER_ID) {
            RecyclerView.LayoutParams card_params =
                    (RecyclerView.LayoutParams) messageViewHolder.msg_card.getLayoutParams();
            card_params.setMarginEnd(150);
            card_params.setMarginStart(0);
            messageViewHolder.msg_card.setLayoutParams(card_params);
            messageViewHolder.message.setGravity(Gravity.START);
        } else {
            RecyclerView.LayoutParams card_params =
                    (RecyclerView.LayoutParams) messageViewHolder.msg_card.getLayoutParams();
            card_params.setMarginStart(150);
            card_params.setMarginEnd(0);
            messageViewHolder.msg_card.setLayoutParams(card_params);
            messageViewHolder.message.setGravity(Gravity.END);
        }
        messageViewHolder.message.setText(messages.get(messageViewHolder.getAdapterPosition()).message);
    }

    /**
     * Overriding this method to point to which object to get the size from (in this case
     * we are using the messages object)
     *
     * @return The total number of items in the adapter
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Creating a CardView variable which will be each one of our messages
        CardView msg_card;

        //Creating variables for holding the items that will be in the CardViews
        TextView message;
        RecyclerView recyclerView;

        /**
         * Taking the View we passed it when inflating msg_card.xml and then finding
         * all of the individual views inside of it (the TextViews and ImageView at the moment)
         *
         * @param itemView the msg_card.xml inflated view (containing TextViews and ImageView)
         */
        MessageViewHolder(View itemView) {
            super(itemView);
            msg_card = (CardView) itemView.findViewById(R.id.msg_card);
            message = (TextView) itemView.findViewById(R.id.message_content);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.rv);
            msg_card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //When chat item is clicked do stuff here if you want
        }
    }
}


