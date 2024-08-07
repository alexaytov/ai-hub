import { ChatModelType } from './chat-model-type.mode';

export interface ChatModel {
  id?: number;
  name?: string;
  description?: string;
  type?: string;
  apiKey?: string;
}
