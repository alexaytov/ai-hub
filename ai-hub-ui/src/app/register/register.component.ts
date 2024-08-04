import { CommonModule } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Ui5InputValueAccessorDirective } from '../ui5-input-value-accessor.directive';
import { AxiosService } from '../services/axios/axios.service';
import { Router } from '@angular/router';
import { AxiosError } from 'axios';

import '@ui5/webcomponents/dist/Label';
import '@ui5/webcomponents/dist/Input.js';
import '@ui5/webcomponents/dist/MessageStrip.js';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, Ui5InputValueAccessorDirective],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class RegisterComponent {
  registerForm: FormGroup;
  error: string | undefined;

  constructor(
    private fb: FormBuilder,
    private axios: AxiosService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.registerForm?.valid) {
      const { username, password } = this.registerForm.value;
      this.axios
        .request('post', '/register', { username, password })
        .then((response) => {
          this.axios.setAuthToken(response.data.token);
          this.router.navigate(['/login']);
        })
        .catch((error: AxiosError) => {
          this.error = error.message;
        });
    }
  }
}
