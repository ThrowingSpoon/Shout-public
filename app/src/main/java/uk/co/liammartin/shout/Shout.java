package uk.co.liammartin.shout;

/**
 * Each shout will have its own instance of this class
 */

class Shout {
    String username;
    String distance;
    String rating;
    String description;
    String profilePic;

    /**
     * Constructor for making a new Shout instance
     *
     * @param username    the username of the shout maker
     * @param distance    The distance the maker of the shout is to the current user
     * @param rating      The rating that the maker of the shout has
     * @param description The description that the maker of the shout wants to go to
     * @param profilePic  URL String of the image on the server
     */
    Shout(String username, String distance, String rating, String description, String profilePic) {
        this.username = username;
        this.distance = distance;
        this.rating = rating;
        this.description = description;
        this.profilePic = profilePic;
    }
}