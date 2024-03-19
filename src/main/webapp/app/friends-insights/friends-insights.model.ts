export interface CategoryInformation {
  value: {
    imageURL: string;
    name: string;
  };
  frequency: number;
}

export interface IPopularCategories {
  tracks: CategoryInformation[];
  artists: CategoryInformation[];
  albums: CategoryInformation[];
}

export interface ILeaderboardUser {
  username: string;
  avatarUrl: string;
}

export interface LeaderboardEntry {
  value: ILeaderboardUser;
  frequency: number;
}

export interface ILeaderboard {
  streamedSecondsLeaderboard: LeaderboardEntry[];
  streamedArtistsLeaderboard: LeaderboardEntry[];
}
