import {
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  OnInit,
  ViewChild,
} from '@angular/core';

import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Ui5InputValueAccessorDirective } from '../ui5-input-value-accessor.directive';

import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Label.js';
import '@ui5/webcomponents/dist/Input.js';
import '@ui5/webcomponents/dist/TextArea.js';
import '@ui5/webcomponents/dist/MessageStrip.js';
import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents/dist/Option.js';
import '@ui5/webcomponents/dist/Select.js';
import "@ui5/webcomponents/dist/Switch";

import { Router, RouterModule } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';
import { SystemMessage } from '../models/system-message.model';
import { ChatModel } from '../models/chat-model.model';
import { AxiosError } from 'axios';
import { Error } from '../models/error.model';
import { ChatMessage } from '../models/chat-message.model';
import { QueryRequest } from '../models/query-request.model';

@Component({
  selector: 'app-create-agent',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    Ui5InputValueAccessorDirective,
    RouterModule,
  ],
  templateUrl: './create-agent.component.html',
  styleUrl: './create-agent.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class CreateAgentComponent implements OnInit {
  @ViewChild('model') model: any;
  @ViewChild('input') input: any;
  @ViewChild('message') message: any;
  @ViewChild('customInstructionalMessage') customInstructionalMessage: any;

  form: FormGroup;
  error: string | undefined;
  hideError = false;
  success: boolean | undefined;

  systemMessage: SystemMessage[] = [];
  models: ChatModel[] = [];

  usingCustomMessage = false;
  presencePenalty: number | undefined;
  frequencyPenalty: number | undefined;
  temperature: number | undefined;
  maxTokens: number | undefined;

  // Test Configuration
  messages: ChatMessage[] | undefined;
  waitingResponse: any;
  currentMessage: string | undefined;

  constructor(
    private router: Router,
    private axios: AxiosService,
    private fb: FormBuilder
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(50)]],
      description: ['', [Validators.required, Validators.maxLength(255)]],
      systemMessage: [''],
      model: [''],
    });
  }

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }

    this.axios
      .request('GET', '/system-messages')
      .then((response) => {
        this.systemMessage = response.data;
      })
      .catch((error) => {
        this.error = error.message;
      });
    this.axios
      .request('GET', '/chat-models')
      .then((response) => {
        this.models = response.data;
        this.onUpdateParameters();
      })
      .catch((error) => {
        this.error = error.message;
      });
  }

  onUpdateParameters() {
    let parameters: { [key: string]: string } | undefined;
    if (this.model.nativeElement.value) {
      parameters = this.models.find(m => m.id == this.model.nativeElement.value)?.parameters;
    } else {
      parameters = this.models[0].parameters;
    }

    if (!parameters) {
      this.presencePenalty = undefined;
      this.frequencyPenalty = undefined;
      this.maxTokens = undefined;
      return;
    }

    if (parameters['presencePenalty'] != null) {
      this.presencePenalty = parseFloat(parameters['presencePenalty']);
    } else {
      this.presencePenalty = undefined;
    }

    if (parameters['frequencyPenalty'] != null) {
      this.frequencyPenalty = parseFloat(parameters['frequencyPenalty']);
    } else {
      this.frequencyPenalty = undefined
    }

    if (parameters['temperature'] != null) {
      this.temperature = parseFloat(parameters['temperature']);
    } else {
      this.temperature = undefined;
    }

    if (parameters['maxTokens'] != null) {
      this.maxTokens = parseFloat(parameters['maxTokens']);
    } else {
      this.maxTokens = undefined;
    }
  }

  onCustomMessage() {
    const value = this.customInstructionalMessage.nativeElement.value;
    if (value === null || value === '') {
      this.usingCustomMessage = false;
      return;
    }

    this.usingCustomMessage = true;
  }

  onSubmit() {
    this.error = undefined;
    this.hideError = false;

    if (!this.form.valid) {
      return;
    }

    const agent = {
      name: this.form.value.name,
      description: this.form.value.description,
      systemMessageId: this.message.nativeElement.value,
      modelId: this.model.nativeElement.value,
    };

    this.axios
      .request('POST', '/agents', agent)
      .then((response) => {
        this.success = true;
      })
      .catch((error: AxiosError<Error>) => {
        if (error.response) {
          this.error = error.response.data.message;
          return;
        }

        this.error = error.message;
      });
  }

  onTestConfiguration() {
    if (this.messages === undefined) {
      this.messages = [];
    }
  }

  onKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter' && event.metaKey && !this.waitingResponse) {
      const message = this.input.nativeElement.value;
      this.input.nativeElement.value = '';
      this.sendMessage(message);
    }
  }

  private getSystemMessage(): string {
    if (this.customInstructionalMessage.nativeElement.value) {
      return this.customInstructionalMessage.nativeElement.value;
    }

    if (this.message.nativeElement.value === -1) {
      return '';
    }

    const message = this.systemMessage.find((m) => m.id == this.message.nativeElement.value);
    return message?.message || '';
  }

  onSendMessageClick() {
    this.sendMessage(this.input.nativeElement.value);
    this.input.nativeElement.value = '';
  }

  sendMessage(message: string) {
    this.waitingResponse = true;

    this.messages?.push({
      content: message|| '',
      type: 'USER',
    });

    const lastMessages = this.messages?.slice(-4);

    const parameters: { [key: string]: string } = {};
    if (this.presencePenalty) {
      parameters['presencePenalty'] = this.presencePenalty.toString();
    }
    if (this.frequencyPenalty) {
      parameters['frequencyPenalty'] = this.frequencyPenalty.toString();
    }
    if (this.temperature) {
      parameters['temperature'] = this.temperature.toString();
    }
    if (this.maxTokens) {
      parameters['maxTokens'] = this.maxTokens.toString();
    }

    const queryRequest: QueryRequest = {
      modelId: this.model.nativeElement.value,
      systemMessage: this.getSystemMessage(),
      messages: lastMessages || [],
      customParameters: parameters,
    }

    this.axios
      .request('POST', `/query`, queryRequest)
      .then(
        (response) => {
          this.messages?.push({
            content: response.data.content,
            type: 'ASSISTANT',
          });
          this.waitingResponse = false;
        },
        (error) => {
          this.messages?.push({
            content: error.message,
            type: 'ASSISTANT',
          });
          this.waitingResponse = false;
        }
      );
  }

  onHideError() {
    this.hideError = true;
  }
}
