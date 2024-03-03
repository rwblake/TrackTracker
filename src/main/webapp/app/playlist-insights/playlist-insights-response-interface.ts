export interface PlaylistInsightsResponse {
  happiestSongID: string;
  energeticSongID: string;
  sumsUpSongID: string;
  anomalousSongID: string;
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

export interface ArtistsToProportionResponse {
  artistID: string;
  proportionOfPlaylist: number;
}
