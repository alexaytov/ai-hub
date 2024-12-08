export interface ChatModel {
  id: number;
  name: string;
  description?: string;
  type?: string;
  apiKey?: string;
  parameters?: { [key: string]: string };
}
