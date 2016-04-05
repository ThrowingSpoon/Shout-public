package uk.co.liammartin.shout;

/**
 * Each message will have its own instance of this class
 */

class message {
    int to_id;
    int from_id;
    String message;
    /**
     * Constructor for making a new user instance
     *
     * @param to_id    the ID of the to_id of the message
     * @param from_id  the ID of the from_id of the message
     * @param message   the message sent
     */
    message(int to_id,int from_id,String message) {
        this.to_id = to_id;
        this.from_id = from_id;
        this.message = message;
    }
}