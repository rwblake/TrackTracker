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

  graphData: GraphDataResponse;
}

export interface YearMapResponse {
  year: string;
  songCount: number;
}

export interface ArtistMapResponse {
  artistName: string;
  occurrencesInPlaylist: number;
}

export interface GenreMapResponse {
  genreName: string;
  occurrencesInPlaylist: number;
}

export interface SimpleSong {
  songSpotifyID: string;
  songTitle: string;
  songImageURL: string;
}

export interface GraphDataResponse {
  yearMaps: YearMapResponse[];
  decadeMaps: YearMapResponse[];
  artistMaps: ArtistMapResponse[];
  genreMaps: GenreMapResponse[];
}
