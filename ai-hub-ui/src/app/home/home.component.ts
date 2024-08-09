import {
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  OnInit,
  ViewChild,
} from '@angular/core';
import { AxiosService } from '../services/axios/axios.service';
import { Router, RouterModule } from '@angular/router';

import '@ui5/webcomponents/dist/Button.js';
import '@ui5/webcomponents/dist/Popover.js';
import '@ui5/webcomponents-fiori/dist/ProductSwitch.js';
import '@ui5/webcomponents-fiori/dist/ProductSwitchItem.js';
import '@ui5/webcomponents-icons/dist/home.js';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class HomeComponent implements OnInit {
  constructor(private axios: AxiosService, private router: Router) {}

  username: string | undefined;

  @ViewChild('profilePopover') profilePopover: any;

  ngOnInit(): void {
    if (!this.axios.getAuthToken()) {
      // Redirect to the login page if the user is not logged in
      this.router.navigate(['/login']);
    }

    this.username = this.axios.getUsername();
  }

  click() {
    this.profilePopover.nativeElement.open = true;
  }
}
