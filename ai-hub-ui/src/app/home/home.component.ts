import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import { AxiosService } from '../services/axios/axios.service';
import { Router } from '@angular/router';

import '@ui5/webcomponents/dist/Button.js';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class HomeComponent implements OnInit {
  constructor(private axios: AxiosService, private router: Router) {}

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }
  }

  click() {
    this.axios.request('GET', '/system-messages').then((response) => {
      console.log(response);
    });
    console.log(this.axios.getAuthToken());
  }
}
