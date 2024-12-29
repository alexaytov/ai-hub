import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';

import "@ui5/webcomponents/dist/FileUploader.js";
import "@ui5/webcomponents-icons/dist/upload.js";
import { AxiosService } from '../services/axios/axios.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-data-source',
  standalone: true,
  imports: [],
  templateUrl: './create-data-source.component.html',
  styleUrl: './create-data-source.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class CreateDataSourceComponent implements OnInit {

  file: File | undefined;

  constructor(private axios: AxiosService, private router: Router) { }

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }
  }

  onFileChange($event: any) {
    this.file = ($event.target as HTMLInputElement).files?.[0];
  }

  async onUpload() {
    // read file bytes
    if (!this.file) {
      return;
    }

    const payload = {
      fileName: this.file.name,
      fileType: this.file.type,
      data: Array.from(new Uint8Array(await this.file.arrayBuffer())),
    }

    this.axios.request('POST', '/data-sources', payload).then(
      () => {
        this.router.navigate(['/data-sources']);
      },
      (error) => {
        console.error(error);
      }
    );
  }

}
