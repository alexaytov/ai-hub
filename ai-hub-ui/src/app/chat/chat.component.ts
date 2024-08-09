import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';
import { ChatContent } from '../models/chat-content.model';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
})
export class ChatComponent implements OnInit {
  chat: ChatContent | undefined;

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
      const id = params.get('id');

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
}
