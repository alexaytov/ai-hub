import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { SystemMessagesComponent } from './system-messages/system-messages.component';
import { ChatModelsComponent } from './chat-models/chat-models.component';
import { CreateChatModelComponent } from './create-chat-model/create-chat-model.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'system-messages', component: SystemMessagesComponent },
  { path: 'create-chat-model', component: CreateChatModelComponent },
  { path: 'chat-models', component: ChatModelsComponent },
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
