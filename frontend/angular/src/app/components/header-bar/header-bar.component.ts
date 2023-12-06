import { Component } from '@angular/core';
import {MenuItem} from "primeng/api";
import {AuthenticationResponse} from "../../models/authentication-response";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header-bar',
  templateUrl: './header-bar.component.html',
  styleUrls: ['./header-bar.component.scss']
})
export class HeaderBarComponent {
  items: Array<MenuItem> = [
    {
      label: 'Profile',
      icon: 'pi pi-fw pi-user'
    },
    {
      label: 'Settings',
      icon: 'pi pi-fw pi-cog'
    },
    {
      separator: true
    },
    {
      label: 'Sign out',
      icon: 'pi pi-fw pi-sign-out',
      command: () => {
        localStorage.removeItem('user');
        this.router.navigate(['login']).finally();
      }
    }
    ];

  constructor(private router: Router) {
  }

  getUsername(): string {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      const authResponse: AuthenticationResponse = JSON.parse(storedUser);
      if (authResponse && authResponse.customerDTO && authResponse.customerDTO.username) {
        return authResponse.customerDTO.username;
      }
    }
    return '---';

  }

  getUserRole(): string {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      const authResponse: AuthenticationResponse = JSON.parse(storedUser);
      if (authResponse && authResponse.customerDTO && authResponse.customerDTO.roles) {
        return authResponse.customerDTO.roles[0];
      }
    }
    return '---';

  }
}
