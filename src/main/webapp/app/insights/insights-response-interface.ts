import { TimePeriod } from '../time-period-picker/time-period-picker.component';
import { ISong } from '../entities/song/song.model';
import { IGenre } from '../entities/genre/genre.model';
import { IAlbum } from '../entities/album/album.model';
import { IArtist } from '../entities/artist/artist.model';

export interface StreamInsightsResponse {
  songCounter: {
    byWeek: Entry[];
    byMonth: Entry[];
    byYear: Entry[];
    byAllTime: Entry[];
  };
  artistCounter: {
    byWeek: Entry[];
    byMonth: Entry[];
    byYear: Entry[];
    byAllTime: Entry[];
  };
  decadeCounter: {
    byWeek: Entry[];
    byMonth: Entry[];
    byYear: Entry[];
    byAllTime: Entry[];
  };
  genreCounter: {
    byWeek: Entry[];
    byMonth: Entry[];
    byYear: Entry[];
    byAllTime: Entry[];
  };
  albumCounter: {
    byWeek: Entry[];
    byMonth: Entry[];
    byYear: Entry[];
    byAllTime: Entry[];
  };
}

export interface Entry {
  metric: ISong | IGenre | IAlbum | IArtist | string;
  value: number;
}

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
