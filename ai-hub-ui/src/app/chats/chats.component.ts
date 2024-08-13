import { CommonModule } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';
import { Chat } from '../models/chat.model';
import { ChatModel } from '../models/chat-model.model';
import { AxiosResponse } from 'axios';
import { Agent } from '../models/agent.model';

@Component({
  selector: 'app-chats',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './chats.component.html',
  styleUrl: './chats.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ChatsComponent implements OnInit {
  chats: Chat[] = [];
  models: Map<number | undefined, ChatModel> = new Map();
  agents: Map<number | undefined, Agent> = new Map();

  constructor(private router: Router, private axios: AxiosService) {}

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      this.router.navigate(['/login']);
    }

    this.axios.request('GET', '/chats').then(
      (response) => {
        this.chats = response.data;
      },
      (error) => {
        console.error(error);
      }
    );

    this.axios.request('GET', '/chat-models').then(
      (response: AxiosResponse<ChatModel[]>) => {
        response.data.forEach((model) => {
          this.models.set(model.id, model);
        });
      },
      (error) => {
        console.error(error);
      }
    );

    this.axios.request('GET', '/agents').then(
      (response: AxiosResponse<Agent[]>) => {
        response.data.forEach((agent) => {
          this.agents.set(agent.id, agent);
        });
      },
      (error) => {
        console.error(error);
      }
    );
  }

  deleteChat(id: number) {
    this.axios.request('DELETE', `/chats/${id}`).then(
      () => {
        this.chats = this.chats.filter((chat) => chat.id !== id);
      },
      (error) => {
        console.error(error);
      }
    );
  }

  openChat(id: number) {
    this.router.navigate([`/chat/${id}`]);
  }
}
