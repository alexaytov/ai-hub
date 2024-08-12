import { CommonModule, NgFor } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ChatModel } from '../models/chat-model.model';

import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Label.js';
import '@ui5/webcomponents/dist/Input.js';
import '@ui5/webcomponents/dist/MessageStrip.js';
import '@ui5/webcomponents/dist/Button.js';

import { Ui5InputValueAccessorDirective } from '../ui5-input-value-accessor.directive';
import { AxiosError } from 'axios';
import { AxiosService } from '../services/axios/axios.service';
import { ChatModelType } from '../models/chat-model-type.mode';
import { Router, RouterModule } from '@angular/router';
import { Error } from '../models/error.model';

@Component({
  selector: 'app-create-chat-model',
  standalone: true,
  imports: [
    CommonModule,
    NgFor,
    FormsModule,
    Ui5InputValueAccessorDirective,
    ReactiveFormsModule,
    RouterModule,
  ],
  templateUrl: './create-chat-model.component.html',
  styleUrl: './create-chat-model.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class CreateChatModelComponent implements OnInit {
  model: ChatModel = {};
  form: FormGroup;
  error: string | undefined;
  hideError = false;
  success: boolean | undefined;

  constructor(
    private fb: FormBuilder,
    private axios: AxiosService,
    private router: Router
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(50)]],
      description: ['', [Validators.required, Validators.maxLength(255)]],
      apiKey: ['', Validators.required],
      parameters: fb.array([]),
    });
  }
  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }
  }

  onAddParameter() {
    console.log(this.form);
    this.parametersFormArray.push(this.fb.group({ key: '', value: '' }));
  }

  onRemoveParameter(index: number) {
    this.parametersFormArray.removeAt(index);
  }

  get parametersFormArray(): FormArray<FormGroup> {
    return this.form.get('parameters') as FormArray;
  }

  onSubmit() {
    this.error = undefined;
    this.hideError = false;

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
