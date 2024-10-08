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
import '@ui5/webcomponents/dist/MessageStrip.js';
import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents/dist/Option.js';
import '@ui5/webcomponents/dist/Select.js';

import { Router, RouterModule } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';
import { SystemMessage } from '../models/system-message.model';
import { ChatModel } from '../models/chat-model.model';
import { AxiosError } from 'axios';
import { Error } from '../models/error.model';

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
  @ViewChild('message') message: any;

  form: FormGroup;
  error: string | undefined;
  hideError = false;
  success: boolean | undefined;

  systemMessage: SystemMessage[] = [];
  models: ChatModel[] = [];

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
      })
      .catch((error) => {
        this.error = error.message;
      });
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

  onHideError() {
    this.hideError = true;
  }
}
