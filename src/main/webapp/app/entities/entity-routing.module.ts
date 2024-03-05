import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'friendship',
        data: { pageTitle: 'Friendships' },
        loadChildren: () => import('./friendship/friendship.module').then(m => m.FriendshipModule),
      },
      {
        path: 'friend-request',
        data: { pageTitle: 'FriendRequests' },
        loadChildren: () => import('./friend-request/friend-request.module').then(m => m.FriendRequestModule),
      },
      {
        path: 'friend-recommendation',
        data: { pageTitle: 'FriendRecommendations' },
        loadChildren: () => import('./friend-recommendation/friend-recommendation.module').then(m => m.FriendRecommendationModule),
      },
      {
        path: 'stream',
        data: { pageTitle: 'Streams' },
        loadChildren: () => import('./stream/stream.module').then(m => m.StreamModule),
      },
      {
        path: 'song',
        data: { pageTitle: 'Songs' },
        loadChildren: () => import('./song/song.module').then(m => m.SongModule),
      },
      {
        path: 'artist',
        data: { pageTitle: 'Artists' },
        loadChildren: () => import('./artist/artist.module').then(m => m.ArtistModule),
      },
      {
        path: 'album',
        data: { pageTitle: 'Albums' },
        loadChildren: () => import('./album/album.module').then(m => m.AlbumModule),
      },
      {
        path: 'genre',
        data: { pageTitle: 'Genres' },
        loadChildren: () => import('./genre/genre.module').then(m => m.GenreModule),
      },
      {
        path: 'playlist',
        data: { pageTitle: 'Playlists' },
        loadChildren: () => import('./playlist/playlist.module').then(m => m.PlaylistModule),
      },
      {
        path: 'playlist-stats',
        data: { pageTitle: 'PlaylistStats' },
        loadChildren: () => import('./playlist-stats/playlist-stats.module').then(m => m.PlaylistStatsModule),
      },
      {
        path: 'app-user',
        data: { pageTitle: 'AppUsers' },
        loadChildren: () => import('./app-user/app-user.module').then(m => m.AppUserModule),
      },
      {
        path: 'user-preferences',
        data: { pageTitle: 'UserPreferences' },
        loadChildren: () => import('./user-preferences/user-preferences.module').then(m => m.UserPreferencesModule),
      },
      {
        path: 'card',
        data: { pageTitle: 'Cards' },
        loadChildren: () => import('./card/card.module').then(m => m.CardModule),
      },
      {
        path: 'sharing-preference',
        data: { pageTitle: 'SharingPreferences' },
        loadChildren: () => import('./sharing-preference/sharing-preference.module').then(m => m.SharingPreferenceModule),
      },
      {
        path: 'feed',
        data: { pageTitle: 'Feeds' },
        loadChildren: () => import('./feed/feed.module').then(m => m.FeedModule),
      },
      {
        path: 'card-template',
        data: { pageTitle: 'CardTemplates' },
        loadChildren: () => import('./card-template/card-template.module').then(m => m.CardTemplateModule),
      },
      {
        path: 'card-metric',
        data: { pageTitle: 'CardMetrics' },
        loadChildren: () => import('./card-metric/card-metric.module').then(m => m.CardMetricModule),
      },
      {
        path: 'feed-card',
        data: { pageTitle: 'FeedCards' },
        loadChildren: () => import('./feed-card/feed-card.module').then(m => m.FeedCardModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
