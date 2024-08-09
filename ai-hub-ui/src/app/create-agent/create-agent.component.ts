import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';

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
import '@ui5/webcomponents/dist/Label';
import '@ui5/webcomponents/dist/Input.js';
import '@ui5/webcomponents/dist/MessageStrip.js';
import '@ui5/webcomponents/dist/Button.js';
import { Router } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';
import { SystemMessage } from '../models/system-message.model';

@Component({
  selector: 'app-create-agent',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    Ui5InputValueAccessorDirective,
  ],
  templateUrl: './create-agent.component.html',
  styleUrl: './create-agent.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class CreateAgentComponent implements OnInit {
  form: FormGroup;
  error: string | undefined;
  success: boolean | undefined;

  systemMessage: SystemMessage[] = [];

  constructor(
    private router: Router,
    private axios: AxiosService,
    private fb: FormBuilder
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      systemMessage: ['', Validators.required],
      model: ['', Validators.required],
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
  }

  onSubmit() {
    throw new Error('Method not implemented.');
  }
}
