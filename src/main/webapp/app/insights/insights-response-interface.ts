export interface InsightsResponse {
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
}

export interface GraphDataResponse {
  yearMaps: YearMapResponse[];
  decadeMaps: YearMapResponse[];
  artistMaps: ArtistMapResponse[];
  genreMaps: GenreMapResponse[];
}
