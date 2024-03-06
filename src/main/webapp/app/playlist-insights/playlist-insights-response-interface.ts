export interface PlaylistInsightsResponse {
  playlistTitle: string;
  imageURL: string;
  happiestSong: SimpleSong;
  energeticSong: SimpleSong;
  sumsUpSong: SimpleSong;
  anomalousSong: SimpleSong;
  averageValence: number;
  averageEnergy: number;
  averageAcousticness: number;
  averageDanceability: number;
  yearsToSongs: YearsToSongResponse[];
  artistsToProportions: ArtistsToProportionResponse[];
}

export interface YearsToSongResponse {
  year: string;
  songCount: number;
}

export interface SimpleSong {
  songSpotifyID: string;
  songTitle: string;
  songImageURL: string;
}

export interface ArtistsToProportionResponse {
  artistSpotifyID: string;
  artistName: string;
  artistImageURL: string;
  occurrencesInPlaylist: number;
}
