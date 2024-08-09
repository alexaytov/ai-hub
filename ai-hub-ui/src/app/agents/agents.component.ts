import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';

import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents-compat/dist/Table.js';
import '@ui5/webcomponents-compat/dist/TableColumn.js';
import '@ui5/webcomponents-compat/dist/TableRow.js';
import '@ui5/webcomponents-compat/dist/TableGroupRow.js';
import '@ui5/webcomponents-compat/dist/TableCell.js';
import '@ui5/webcomponents/dist/Card.js';
import '@ui5/webcomponents/dist/CardHeader.js';
import '@ui5/webcomponents/dist/Tag.js';
import '@ui5/webcomponents-icons/dist/personnel-view.js';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-agents',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './agents.component.html',
  styleUrl: './agents.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AgentsComponent implements OnInit {
  constructor(private router: Router, private axios: AxiosService) {}

  agents: any[] | undefined;
  error: string | undefined;

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }

    this.axios
      .request('GET', '/agents')
      .then((response) => {
        this.agents = response.data;
      })
      .catch((error) => {
        this.error = error.message;
      });
  }

  deleteAgent(id: number) {
    this.axios.request('DELETE', `/agents/${id}`).then(
      () => {
        this.agents = this.agents?.filter((agent) => agent.id !== id);
      },
      (error) => {
        this.error = error.message;
      }
    );
  }

  onNavigateToCreateAgent() {
    this.router.navigate(['/create-agent']);
  }
}
