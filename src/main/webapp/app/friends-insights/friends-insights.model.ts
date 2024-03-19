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
