import { Component, OnInit } from '@angular/core';
import { NavigationEnd } from '@angular/router';

@Component({
  selector: 'jhi-spotify-callback',
  templateUrl: './spotify-callback.component.html',
  styleUrls: ['./spotify-callback.component.scss'],
})
export class SpotifyCallbackComponent implements OnInit {
  constructor() {}

  ngOnInit(): void {
    // try to log in automatically
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateTitle();

        const url = new URL(window.location.href);
        const params = url.searchParams;
        if (params.has('state') && (params.has('code') || params.has('error'))) {
          // Check whether there's an opener to send the code back to
          if (window.opener != null) {
            console.log(`sending params '${params.toString()}' to window.opener`, window.opener);
            window.opener.postMessage(params.toString());
            // window.close();
          }
        }
      }
    });
  }
}
