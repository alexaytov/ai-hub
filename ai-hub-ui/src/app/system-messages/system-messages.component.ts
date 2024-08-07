import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import { AxiosService } from '../services/axios/axios.service';
import { Router } from '@angular/router';
import { SystemMessage } from '../models/system-message.model';

import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents-compat/dist/Table.js';
import '@ui5/webcomponents-compat/dist/TableColumn.js';
import '@ui5/webcomponents-compat/dist/TableRow.js';
import '@ui5/webcomponents-compat/dist/TableGroupRow.js';
import '@ui5/webcomponents-compat/dist/TableCell.js';
import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Tag.js';
import '@ui5/webcomponents-icons/dist/letter.js';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-system-messages',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './system-messages.component.html',
  styleUrl: './system-messages.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SystemMessagesComponent implements OnInit {
  deleteMessage(arg0: any) {
    throw new Error('Method not implemented.');
  }
  constructor(private axios: AxiosService, private router: Router) {}

  messages: SystemMessage[] | undefined;
  error: string | undefined;

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }

    this.axios
      .request('GET', '/system-messages')
      .then((response) => {
        this.messages = response.data;
      })
      .catch((error) => {
        console.error(error);
      });
  }
}
