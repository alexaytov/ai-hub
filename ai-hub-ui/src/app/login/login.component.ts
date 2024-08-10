import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { Ui5InputValueAccessorDirective } from '../ui5-input-value-accessor.directive';
import { CommonModule } from '@angular/common';
import { AxiosService } from '../services/axios/axios.service';
import { Router } from '@angular/router';

import '@ui5/webcomponents/dist/Label';
import '@ui5/webcomponents/dist/Input.js';
import '@ui5/webcomponents/dist/MessageStrip.js';
import { AxiosError } from 'axios';
import { Error } from '../models/error.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, Ui5InputValueAccessorDirective, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  error: string | undefined;
  hideError = false;

  constructor(
    private fb: FormBuilder,
    private axios: AxiosService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    if (this.axios.getAuthToken()) {
      // Redirect to the home page if the user is already logged in
      this.router.navigate(['/home']);
    }
  }

  onSubmit(): void {
    this.error = undefined;
    this.hideError = false;

    if (this.loginForm?.valid) {
      const { username, password } = this.loginForm.value;
      this.axios.request('POST', '/login', { username, password }).then(
        (response) => {
          this.axios.setAuthToken(response.data.token);
          this.router.navigate(['/home']);
        },
        (error: AxiosError<Error>) => {
          if (error.response) {
            console.log(error.response.data);
            this.error = error.response.data.message;
            return;
          }
          this.error = error.message;
        }
      );
    }
  }

  onCloseError() {
    this.hideError = true;
  }
}
