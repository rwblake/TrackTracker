export enum CardType {
  // The card displays how long a user has listened to Spotify for
  LISTENING_DURATION = 'LISTENING_DURATION',

  // The card displays a user's top genre
  TOP_GENRE = 'TOP_GENRE',

  // The card displays a user's top artist
  TOP_ARTIST = 'TOP_ARTIST',

  // The card displays a user's top song
  TOP_SONG = 'TOP_SONG',

  // The card displays how many friends a user has
  NO_OF_FRIENDS = 'NO_OF_FRIENDS',

  // The card displays how many different songs a user has listened to
  NO_OF_SONGS_LISTENED = 'NO_OF_SONGS_LISTENED',

  // The card displays how many different genres a user has listened to
  NO_OF_GENRES_LISTENED = 'NO_OF_GENRES_LISTENED',

  // The card displays a friend request
  FRIEND_REQUEST = 'FRIEND_REQUEST',

  // The card displays a notification of a newly-formed friendship
  NEW_FRIEND = 'NEW_FRIEND',

  // The card displays a notification of a new playlist that has been analysed
  NEW_PLAYLIST = 'NEW_PLAYLIST',

  // The card displays the happiest song of a given playlist
  PLAYLIST_HAPPIEST_SONG = 'PLAYLIST_HAPPIEST_SONG',

  // The card displays the most energetic song of a given playlist
  PLAYLIST_MOST_ENERGETIC_SONG = 'PLAYLIST_MOST_ENERGETIC_SONG',

  // The card displays the song which best sums up a given playlist
  PLAYLIST_SUMS_UP = 'PLAYLIST_SUMS_UP',

  // For pinned friends
  PINNED_FRIEND = 'PINNED_FRIEND',
}
