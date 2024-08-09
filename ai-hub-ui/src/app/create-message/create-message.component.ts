import { CommonModule } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';

import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Label';
import '@ui5/webcomponents/dist/Input.js';
import '@ui5/webcomponents/dist/MessageStrip.js';
import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents-icons/dist/letter.js';
import { Ui5InputValueAccessorDirective } from '../ui5-input-value-accessor.directive';

@Component({
  selector: 'app-create-message',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    Ui5InputValueAccessorDirective,
    RouterModule,
  ],
  templateUrl: './create-message.component.html',
  styleUrl: './create-message.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class CreateMessageComponent implements OnInit {
  success: boolean | undefined;
  error: string | undefined;
  form: FormGroup;

  constructor(
    private router: Router,
    private axios: AxiosService,
    private fb: FormBuilder
  ) {
    this.form = this.fb.group({
      message: [''],
    });
  }

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }
  }
  onSubmit() {
    if (this.form?.valid) {
      const { message } = this.form.value;
      this.axios.request('POST', '/system-messages', { message }).then(
        (response) => {
          this.success = true;
        },
        (error) => {
          this.error = error.message;
        }
      );
    }
  }
}
