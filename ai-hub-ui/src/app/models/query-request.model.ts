import { ChatMessage } from "./chat-message.model";

export interface QueryRequest {
    modelId: number;
    systemMessage: string;
    messages: ChatMessage[];
    customParameters: { [key: string]: string };
    dataSources: number[];
}