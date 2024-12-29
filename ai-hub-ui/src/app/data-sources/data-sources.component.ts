import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ListDataSource } from '../models/ListDataSource.model';
import { AxiosService } from '../services/axios/axios.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-data-sources',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './data-sources.component.html',
  styleUrl: './data-sources.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class DataSourcesComponent {

  dataSources: ListDataSource[] | undefined;

  constructor(private axios: AxiosService, private router: Router) { }

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }

    this.axios
      .request('GET', '/data-sources')
      .then((response) => {
        this.dataSources = response.data;
      })
      .catch((error) => {
        console.error(error);
      });
  }

  onNavigateToCreate() {
    this.router.navigate(['/create-data-source']);
  }

  deleteDataSource(id: number) {
    this.axios.request('DELETE', `/data-sources/${id}`).then(
      () => {
        this.dataSources = this.dataSources?.filter((dataSource) => dataSource.id !== id);
      },
      (error) => {
        console.error(error);
      }
    );
  }
}
