import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';

import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents-compat/dist/Table.js';
import '@ui5/webcomponents-compat/dist/TableColumn.js';
import '@ui5/webcomponents-compat/dist/TableRow.js';
import '@ui5/webcomponents-compat/dist/TableGroupRow.js';
import '@ui5/webcomponents-compat/dist/TableCell.js';
import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Tag.js';
import '@ui5/webcomponents-icons/dist/personnel-view.js';
import { CommonModule } from '@angular/common';
import { SystemMessage } from '../models/system-message.model';
import { ChatModel } from '../models/chat-model.model';
import { AxiosResponse } from 'axios';

@Component({
  selector: 'app-agents',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './agents.component.html',
  styleUrl: './agents.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AgentsComponent implements OnInit {
  constructor(private router: Router, private axios: AxiosService) {}

  agents: any[] | undefined;
  error: string | undefined;

  systemMessages: Map<number, string> = new Map();
  models: Map<number, string> = new Map();

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }

    this.axios
      .request('GET', '/agents')
      .then((response) => {
        this.agents = response.data;
      })
      .catch((error) => {
        this.error = error.message;
      });

    this.axios.request('GET', '/chat-models').then(
      (response: AxiosResponse<ChatModel[]>) => {
        response.data.forEach((model) => {
          this.models.set(model.id, model.name);
        });
      },
      (error) => {
        console.error(error);
      }
    );

    this.axios
      .request('GET', '/system-messages')
      .then((response: AxiosResponse<SystemMessage[]>) => {
        response.data.forEach((message) => {
          this.systemMessages.set(message.id, message.message);
        });
      })
      .catch((error) => {
        console.error(error);
      });
  }

  deleteAgent(id: number) {
    this.axios.request('DELETE', `/agents/${id}`).then(
      () => {
        this.agents = this.agents?.filter((agent) => agent.id !== id);
      },
      (error) => {
        this.error = error.message;
      }
    );
  }

  onChat(agentId: number | undefined) {
    this.axios
      .request('POST', '/chats', {
        agentId,
      })
      .then(
        (response) => {
          this.router.navigate([`/chat/${response.data.id}`]);
        },
        (error) => {
          this.error = error.message;
        }
      );
  }

  onNavigateToCreateAgent() {
    this.router.navigate(['/create-agent']);
  }
}
