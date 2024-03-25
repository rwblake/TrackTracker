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

    /*    selector: 'app-spotify-stats',
    templateUrl: './spotify-stats.component.html',
    styleUrls: ['./spotify-stats.component.scss']
})
export class SpotifyStatsComponent implements OnInit {
    stats: any; // Define appropriate type

    constructor(private spotifyService: SpotifyService) { }

    ngOnInit(): void {
        this.loadStats();
    }

    loadStats() {
        this.spotifyService.getUserStats().subscribe((response) => {
            this.stats = response;
        });
    }
}*/

    /*public class SpotifyStatsController {

    ("/spotify-stats/share")
    public ResponseEntity<Void> shareStats(@RequestBody String username) {
        // Implement logic to share stats with other users
        return ResponseEntity.ok().build();
    }
}*/

    /* public class CardData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    // Add more fields as needed*/
  }

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Share');
  }
}
