import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import '@ui5/webcomponents-fiori/dist/ShellBar.js';

import '@ui5/webcomponents-fiori/dist/ShellBarItem.js';

import '@ui5/webcomponents-icons/dist/home.js';
import '@ui5/webcomponents-icons/dist/settings.js';
import '@ui5/webcomponents-icons/dist/person-placeholder.js';
import '@ui5/webcomponents-icons/dist/employee.js';
import '@ui5/webcomponents/dist/Avatar.js';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class HeaderComponent {}
