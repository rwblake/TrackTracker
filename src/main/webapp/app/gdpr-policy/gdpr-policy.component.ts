import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'jhi-gdpr-policy',
  templateUrl: './gdpr-policy.component.html',
  styleUrls: ['./gdpr-policy.component.scss'],
})
export class GdprPolicyComponent implements OnInit {
  constructor(private router: Router) {}

  ngOnInit(): void {}
  backToProfile() {
    this.router.navigate(['/profile/:spotifyUsername']);
  }
  notAccept() {
    let text = document.getElementById('no') as HTMLElement | null;
    if (text) {
      text.style.display = 'block';
    } else {
      console.error('Text element not found');
    }
  }
}
