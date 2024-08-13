import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';

import '@ui5/webcomponents/dist/Label.js';
import '@ui5/webcomponents/dist/Input.js';
import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents/dist/MessageStrip.js';

import { Router } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Ui5InputValueAccessorDirective } from '../ui5-input-value-accessor.directive';
import { AxiosError } from 'axios';
import { Error } from '../models/error.model';

@Component({
  selector: 'app-user-settings',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    Ui5InputValueAccessorDirective,
  ],
  templateUrl: './user-settings.component.html',
  styleUrl: './user-settings.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class UserSettingsComponent implements OnInit {
  form: FormGroup;
  edit: boolean = false;
  originalUsername: string = '';

  errorMessage: string | undefined;
  success: boolean | undefined;

  constructor(private router: Router, private axios: AxiosService) {
    this.form = new FormGroup({
      username: new FormControl(''),
      password: new FormControl(''),
    });
  }

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }

    this.originalUsername = this.axios.getUsername();
    this.form.controls['username'].setValue(this.originalUsername);
  }

  onEdit() {
    this.edit = !this.edit;
  }

  onChangeUsername() {
    const changeRequest = {
      username: null,
      password: null,
    };

    const newUsername = this.form.controls['username'].value;
    if (newUsername !== this.originalUsername) {
      changeRequest.username = newUsername;
    }

    const newPassword = this.form.controls['password'].value;
    if (newPassword && newPassword != '') {
      changeRequest.password = newPassword;
    }

    this.axios.request('PUT', '/user', changeRequest).then(
      (response) => {
        this.originalUsername = response.data.username;
        this.form.controls['username'].setValue(response.data.username);
        this.edit = false;
        this.success = true;
      },
      (error: AxiosError<Error>) => {
        if (error.response) {
          this.errorMessage = error.response.data.message;
          return;
        }
        this.errorMessage = error.message;
      }
    );
  }

  onHideError() {
    this.errorMessage = undefined;
  }
  onHideSuccess() {
    this.success = undefined;
  }

  onDeleteUser() {
    this.axios.request('DELETE', '/user').then(
      () => {
        this.axios.setAuthToken(null);
        this.router.navigate(['/login']);
      },
      (error: AxiosError<Error>) => {
        if (error.response) {
          this.errorMessage = error.response.data.message;
          return;
        }
        this.errorMessage = error.message;
      }
    );
  }
}
