import { Component, CUSTOM_ELEMENTS_SCHEMA, ViewChild } from '@angular/core';
import '@ui5/webcomponents-fiori/dist/ShellBar.js';
import '@ui5/webcomponents-fiori/dist/ShellBarItem.js';
import '@ui5/webcomponents/dist/List.js';
import '@ui5/webcomponents/dist/ListItemStandard.js';

import '@ui5/webcomponents-icons/dist/person-placeholder.js';
import '@ui5/webcomponents/dist/Popover.js';

import '@ui5/webcomponents-icons/dist/log.js';
import '@ui5/webcomponents-icons/dist/settings.js';
import '@ui5/webcomponents/dist/Avatar.js';
import { Router, RouterModule } from '@angular/router';
import { AxiosService } from '../services/axios/axios.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class HeaderComponent {
  @ViewChild('profilePopover') profilePopover: any;

  constructor(private router: Router, private axios: AxiosService) {}

  onOpenProfilePopover() {
    this.profilePopover.nativeElement.open = true;
  }

  logout() {
    this.axios.setAuthToken(null);
    this.router.navigate(['/login']);
    this.profilePopover.nativeElement.open = false;
  }

  openSettings() {
    this.router.navigate(['/settings']);
  }

  onNavigateToHome() {
    this.router.navigate(['/home']);
  }

  onNavigate($event: any) {
    const path = $event.detail.item.innerText.toLowerCase();
    if (path === 'system messages') {
      this.router.navigate(['/system-messages']);
      return;
    }

    if (path === 'chat models') {
      this.router.navigate(['/chat-models']);
      return;
    }

    if (path === 'home') {
      this.router.navigate(['/home']);
      return;
    }

    if (path === 'agents') {
      this.router.navigate(['/agents']);
      return;
    }
  }
}
