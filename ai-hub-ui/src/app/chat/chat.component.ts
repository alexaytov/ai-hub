import {
  AfterViewChecked,
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  ElementRef,
  OnInit,
  SecurityContext,
  ViewChild,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';
import { ChatContent } from '../models/chat-content.model';
import { CommonModule } from '@angular/common';

import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents-compat/dist/Table.js';
import '@ui5/webcomponents-compat/dist/TableColumn.js';
import '@ui5/webcomponents-compat/dist/TableRow.js';
import '@ui5/webcomponents-compat/dist/TableGroupRow.js';
import '@ui5/webcomponents-compat/dist/TableCell.js';
import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Tag.js';
import {
  MarkdownModule,
  MarkdownService,
  SECURITY_CONTEXT,
} from 'ngx-markdown';
import { AxiosResponse } from 'axios';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, MarkdownModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [
    MarkdownService,
    { provide: SECURITY_CONTEXT, useValue: SecurityContext.HTML },
  ],
})
export class ChatComponent implements OnInit, AfterViewChecked {
  @ViewChild('input') input: ElementRef | undefined;
  @ViewChild('chatWindow', { static: true }) chatWindow: ElementRef | undefined;

  chat: ChatContent | undefined;
  modelName: string | undefined;
  agentName: string | undefined;
  chatId: number | null = null;
  waitingResponse = false;

  constructor(
    private router: Router,
    private axios: AxiosService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      this.router.navigate(['/login']);
    }

    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      this.chatId = id;

      if (id) {
        this.axios.request('GET', `/chats/${id}`).then(
          (response: AxiosResponse<ChatContent>) => {
            this.chat = response.data;

            if (this.chat?.modelId) {
              this.axios
                .request('GET', `/chat-models/${this.chat.modelId}`)
                .then(
                  (response) => {
                    this.modelName = response.data.name;
                  },
                  (error) => {
                    console.error(error);
                  }
                );
            }
            if (this.chat?.agentId) {
              this.axios.request('GET', `/agents/${this.chat.agentId}`).then(
                (response) => {
                  response;
                  this.agentName = response.data.name;
                },
                (error) => {
                  console.error(error);
                }
              );
            }
          },
          (error) => {
            console.error(error);
          }
        );
      }
    });
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    try {
      if (this.chatWindow) {
        this.chatWindow.nativeElement.scrollTop =
          this.chatWindow.nativeElement.scrollHeight;
      }
    } catch (err) {
      console.error('Could not scroll to bottom:', err);
    }
  }

  sendMessage() {
    this.waitingResponse = true;

    const message = this.input?.nativeElement.value;
    this.chat?.messages.push({
      content: message,
      type: 'user',
    });
    if (this.input) {
      this.input.nativeElement.value = '';
    }

    this.axios
      .request('PUT', `/chats/${this.chatId}`, {
        content: message,
      })
      .then(
        (response) => {
          this.chat?.messages.push({
            content: response.data.content,
            type: 'assistant',
          });
          this.waitingResponse = false;
        },
        (error) => {
          this.chat?.messages.push({
            content: error.message,
            type: 'assistant',
          });
          this.waitingResponse = false;
        }
      );
  }

  onKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !this.waitingResponse) {
      this.sendMessage();
    }
  }
}
