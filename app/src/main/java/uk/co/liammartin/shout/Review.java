package uk.co.liammartin.shout;

/**
 * Each review will have its own instance of this class
 */

class Review {
    String username;
    String rating;
    String body;
    int profilePic;

    /**
     * Constructor for making a new review instance
     *
     * @param username    The username of the reviewer
     * @param rating      The rating that the review has given
     * @param body        The body of text in the review
     * @param profilePic  The profile picture of the review maker
     */
    Review(String username, String rating, String body, int profilePic) {
        this.username = username;
        this.rating = rating;
        this.body = body;
        this.profilePic = profilePic;
    }
}
