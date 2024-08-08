import { CommonModule } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';
import { ChatModel } from '../models/chat-model.model';

import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents-compat/dist/Table.js';
import '@ui5/webcomponents-compat/dist/TableColumn.js';
import '@ui5/webcomponents-compat/dist/TableRow.js';
import '@ui5/webcomponents-compat/dist/TableGroupRow.js';
import '@ui5/webcomponents-compat/dist/TableCell.js';
import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Tag.js';
import '@ui5/webcomponents-icons/dist/ai.js';

@Component({
  selector: 'app-chat-models',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chat-models.component.html',
  styleUrl: './chat-models.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ChatModelsComponent implements OnInit {
  onNavigateToCreateModel() {
    this.router.navigate(['/create-chat-model']);
  }

  deleteModel(id?: number) {
    this.axios.request('DELETE', `/chat-models/${id}`).then(
      () => {
        this.models = this.models?.filter((model) => model.id !== id);
      },
      (error) => {
        this.error = error.message;
      }
    );
  }
  constructor(private axios: AxiosService, private router: Router) {}

  models: ChatModel[] | undefined;
  error: string | undefined;

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }

    this.axios
      .request('GET', '/chat-models')
      .then((response) => {
        this.models = response.data;
      })
      .catch((error) => {
        console.error(error);
      });
  }
}
