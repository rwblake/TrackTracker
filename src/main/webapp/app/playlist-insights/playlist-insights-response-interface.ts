export interface PlaylistInsightsResponse {
  playlistTitle: string;
  imageURL: string;
  happiestSongID: SimpleSong;
  energeticSongID: SimpleSong;
  sumsUpSongID: SimpleSong;
  anomalousSongID: SimpleSong;
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
  spotifyID: string;
  title: string;
  imageURL: string;
}

export interface ArtistsToProportionResponse {
  artistSpotifyID: string;
  artistName: string;
  artistImageURL: string;
  proportionOfPlaylist: number;
}
