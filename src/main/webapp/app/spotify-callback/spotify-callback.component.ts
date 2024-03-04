import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-spotify-callback',
  templateUrl: './spotify-callback.component.html',
  styleUrls: ['./spotify-callback.component.scss'],
})
export class SpotifyCallbackComponent implements OnInit {
  error = false;

  ngOnInit(): void {
    const url = new URL(window.location.href);
    const params = url.searchParams;
    if (params.has('state') && (params.has('code') || params.has('error'))) {
      // Check whether there's an opener window to direct the code back to
      if (window.opener != null) {
        window.opener.postMessage(params.toString());
        window.close();
        return;
      }
    }
    this.error = true;
  }
}
