import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { Authority } from 'app/config/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'admin',
          data: {
            authorities: [Authority.ADMIN],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
        },
        {
          path: 'login',
          loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
        },
        {
          path: 'profile/edit-profile',
          loadChildren: () => import('./profile/edit-profile/edit-profile.module').then(m => m.EditProfileModule),
        },
        {
          path: 'profile/user-preferences',
          loadChildren: () => import('./profile/user-preferences/user-preferences.module').then(m => m.UserPreferencesModule),
        },
        {
          path: 'profile/:spotifyUsername',
          loadChildren: () => import('./profile/profile.module').then(m => m.ProfileModule),
        },
        {
          path: 'friends',
          loadChildren: () => import('./friends/friends.module').then(m => m.FriendsModule),
        },
        {
          path: 'insights',
          loadChildren: () => import('./insights/insights.module').then(m => m.InsightsModule),
        },
        {
          path: 'insights/playlist',
          loadChildren: () => import('./playlist-insights/playlist-insights.module').then(m => m.PlaylistInsightsModule),
        },
        {
          path: 'insights/playlist/:playlistId',
          loadChildren: () => import('./playlist-insights/playlist/playlist.module').then(m => m.PlaylistModule),
        },
        {
          path: 'insights/friends',
          loadChildren: () => import('./friends-insights/friends-insights.module').then(m => m.FriendsInsightsModule),
        },
        { path: 'share', loadChildren: () => import('./share/share.module').then(m => m.ShareModule) },
        {
          path: '',
          loadChildren: () => import(`./entities/entity-routing.module`).then(m => m.EntityRoutingModule),
        },
        {
          path: 'spotify-callback',
          loadChildren: () => import('./spotify-callback/spotify-callback.module').then(m => m.SpotifyCallbackModule),
        },
        { path: 'gdpr-policy', loadChildren: () => import('./gdpr-policy/gdpr-policy.module').then(m => m.GdprPolicyModule) },
        navbarRoute,
        ...errorRoute,
      ],
      { enableTracing: false } // Set as true to view the router info on the console
    ),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
