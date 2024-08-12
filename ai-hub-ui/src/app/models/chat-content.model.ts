import { ChatMessage } from './chat-message.model';

export interface ChatContent {
  messages: ChatMessage[];
  modelId: number;
  agentId: number;
}
