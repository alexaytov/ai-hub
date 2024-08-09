import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { SystemMessagesComponent } from './system-messages/system-messages.component';
import { ChatModelsComponent } from './chat-models/chat-models.component';
import { CreateChatModelComponent } from './create-chat-model/create-chat-model.component';
import { UserSettingsComponent } from './user-settings/user-settings.component';
import { AgentsComponent } from './agents/agents.component';
import { CreateAgentComponent } from './create-agent/create-agent.component';
import { CreateMessageComponent } from './create-message/create-message.component';
import { ChatsComponent } from './chats/chats.component';
import { ChatComponent } from './chat/chat.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'system-messages', component: SystemMessagesComponent },
  { path: 'create-message', component: CreateMessageComponent },
  { path: 'create-chat-model', component: CreateChatModelComponent },
  { path: 'chat-models', component: ChatModelsComponent },
  { path: 'user-settings', component: UserSettingsComponent },
  { path: 'chats', component: ChatsComponent },
  { path: 'chat/:id', component: ChatComponent },
  { path: 'home', component: HomeComponent },
  { path: 'agents', component: AgentsComponent },
  { path: 'create-agent', component: CreateAgentComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
