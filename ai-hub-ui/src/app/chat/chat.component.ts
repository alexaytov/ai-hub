import {
  AfterViewChecked,
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  ElementRef,
  OnInit,
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

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ChatComponent implements OnInit, AfterViewChecked {
  @ViewChild('input') input: ElementRef | undefined;
  @ViewChild('chatWindow', { static: true }) chatWindow: ElementRef | undefined;

  chat: ChatContent | undefined;
  chatId: number | null = null;

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
          (response) => {
            this.chat = response.data;
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
        console.log('here');
      }
    } catch (err) {
      console.error('Could not scroll to bottom:', err);
    }
  }

  sendMessage() {
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
        },
        (error) => {
          this.chat?.messages.push({
            content: error.message,
            type: 'assistant',
          });
        }
      );
  }

  onKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }
}
