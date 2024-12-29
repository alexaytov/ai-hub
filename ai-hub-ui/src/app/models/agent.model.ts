export interface Agent {
  id: number | undefined;
  name: string;
  description: string;
  modelId: number;
  systemMessageId: number | undefined;
}
