import { TimePeriod } from '../time-period-picker/time-period-picker.component';

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

export interface AlbumMapResponse {
  albumName: string;
  occurrencesInPlaylist: number;
}

export interface TimeListenedMapResponse {
  hours: TimePeriod;
  when: TimePeriod;
}

export interface SongMapResponse {
  songName: string;
  occurrencesInPlaylist: number;
}

export interface GraphDataResponse {
  decadeMapsWeek: YearMapResponse[];
  decadeMapsMonth: YearMapResponse[];
  decadeMapsYear: YearMapResponse[];
  decadeMapsAllTime: YearMapResponse[];

  songMapsWeek: SongMapResponse[];
  songMapsMonth: SongMapResponse[];
  songMapsYear: SongMapResponse[];
  songMapsAllTime: SongMapResponse[];

  artistMapsWeek: ArtistMapResponse[];
  artistMapsMonth: ArtistMapResponse[];
  artistMapsYear: ArtistMapResponse[];
  artistMapsAllTime: ArtistMapResponse[];

  genreMapsWeek: GenreMapResponse[];
  genreMapsMonth: GenreMapResponse[];
  genreMapsYear: GenreMapResponse[];
  genreMapsAllTime: GenreMapResponse[];

  albumMapsWeek: AlbumMapResponse[];
  albumMapsMonth: AlbumMapResponse[];
  albumMapsYear: AlbumMapResponse[];
  albumMapsAllTime: AlbumMapResponse[];

  timeMapsWeek: TimeListenedMapResponse[];
  timeMapsMonth: TimeListenedMapResponse[];
  timeMapsYear: TimeListenedMapResponse[];
  timeMapsAllTime: TimeListenedMapResponse[];
}
