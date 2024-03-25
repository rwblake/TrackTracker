import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';

@Component({
  selector: 'jhi-share',
  templateUrl: './share.component.html',
  styleUrls: ['./share.component.scss'],
})
export class ShareComponent implements OnInit {
  constructor(private titleService: Title) {
    true;
    /*
    public class SpotifyService {

      public SpotifyStats getUserStats(String username) {
      // Use Spotify API to fetch user stats
      // Implementation depends on the Spotify API you're using
      // Example: spotifyApi.getUserStats(username);
      return new SpotifyStats(username,  stats  );
    }
*/
  }

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Share');
  }
}
