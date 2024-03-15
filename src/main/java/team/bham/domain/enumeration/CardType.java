package team.bham.domain.enumeration;

/**
 * Represents the different types of data that can be analysed and shared with others
 */
public enum CardType {
    /** The card displays how long a user has listened to Spotify for */
    LISTENING_DURATION,

    /** The card displays a user's top genre */
    TOP_GENRE,

    /** The card displays a user's top artist */
    TOP_ARTIST,

    /** The card displays a user's top song */
    TOP_SONG,

    /** The card displays how many friends a user has */
    NO_OF_FRIENDS,

    /** The card displays how many different songs a user has listened to */
    NO_OF_SONGS_LISTENED,

    /** The card displays a friend request */
    FRIEND_REQUEST,

    /** The card displays a notification of a newly-formed friendship */
    NEW_FRIEND,

    /** The card displays a notification of a new playlist that has been analysed */
    NEW_PLAYLIST,

    /** The card displays the happiest song of a given playlist */
    PLAYLIST_HAPPIEST_SONG,

    /** The card displays the most energetic song of a given playlist */
    PLAYLIST_MOST_ENERGETIC_SONG,

    /** The card displays the song which best sums up a given playlist */
    PLAYLIST_SUMS_UP,
}
