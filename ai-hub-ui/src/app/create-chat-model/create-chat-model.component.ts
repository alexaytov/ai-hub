import { CommonModule } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ChatModel } from '../models/chat-model.model';

import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Label';
import '@ui5/webcomponents/dist/Input.js';
import '@ui5/webcomponents/dist/MessageStrip.js';
import { Ui5InputValueAccessorDirective } from '../ui5-input-value-accessor.directive';
import { Axios, AxiosError } from 'axios';
import { AxiosService } from '../services/axios/axios.service';
import { ChatModelType } from '../models/chat-model-type.mode';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-chat-model',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Ui5InputValueAccessorDirective,
    ReactiveFormsModule,
  ],
  templateUrl: './create-chat-model.component.html',
  styleUrl: './create-chat-model.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class CreateChatModelComponent implements OnInit {
  model: ChatModel = {};
  form: FormGroup;
  error: string | undefined;
  success: boolean | undefined;

  constructor(
    private fb: FormBuilder,
    private axios: AxiosService,
    private router: Router
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      apiKey: ['', Validators.required],
    });
  }
  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }
  }

  onSubmit() {
    if (!this.form.valid) {
      return;
    }

    const model: ChatModel = {
      name: this.form.value.name,
      description: this.form.value.description,
      type: ChatModelType.OPEN_AI,
      apiKey: this.form.value.apiKey,
    };

    this.axios
      .request('POST', '/chat-models', model)
      .then((response) => {
        this.success = true;
      })
      .catch((error: AxiosError) => {
        this.error = error.message;
      });
  }
}
