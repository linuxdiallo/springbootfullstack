import {Component, OnInit} from '@angular/core';
import {AuthenticationRequest} from "../../models/authentication-request";
import {AuthenticationService} from "../../services/auth/authentication.service";
import {HttpStatusCode} from "@angular/common/http";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  authenticationRequest: AuthenticationRequest = {};
  errorMsg: string = '';

  constructor(private authenticationService: AuthenticationService,
              private router: Router) {
  }

  ngOnInit(): void {
  }

  login() {
    this.errorMsg = '';
    this.authenticationService.login(this.authenticationRequest)
      .subscribe({
        next: (authenticationResponse) => {
          localStorage.setItem("user", JSON.stringify(authenticationResponse));
          this.router.navigate(["customers"]).finally();
        },
        error: (err) => {
          if (err.error.statusCode == HttpStatusCode.Unauthorized) {
            this.errorMsg = "Login and / or password is incorrect";
          }
        }
      });
  }

  protected readonly matchMedia = matchMedia;
}
