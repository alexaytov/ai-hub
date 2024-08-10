import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';

import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Label.js';

import '@ui5/webcomponents-fiori/dist/Timeline.js';
import '@ui5/webcomponents-fiori/dist/TimelineItem.js';

import '@ui5/webcomponents-icons/dist/tri-state.js';
import { Router } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';
import { AuditLog } from '../models/audit-log.model';
import { CommonModule } from '@angular/common';
import { AxiosError } from 'axios';
import { Error } from '../models/error.model';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit-logs.component.html',
  styleUrl: './audit-logs.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AuditLogsComponent implements OnInit {
  logs: AuditLog[] | undefined;
  error: string | undefined;
  hideError = false;

  constructor(private router: Router, private axios: AxiosService) {}

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }

    this.axios
      .request('GET', '/audit-logs')
      .then((response) => {
        this.logs = response.data;
        console.log(this.logs);
      })
      .catch((error: AxiosError<Error>) => {
        if (error.response) {
          this.error = error.response.data.message;
          return;
        }
        this.error = error.message;
      });
  }

  onHideError() {
    this.hideError = true;
  }
}
