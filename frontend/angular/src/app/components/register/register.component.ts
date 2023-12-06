import {Component} from '@angular/core';
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {CustomerService} from "../../services/customer/customer.service";
import {MessageService} from "primeng/api";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/auth/authentication.service";
import {AuthenticationRequest} from "../../models/authentication-request";
import {HttpStatusCode} from "@angular/common/http";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  errorMsg = '';
  customer: CustomerRegistrationRequest = {};
  authenticationRequest: AuthenticationRequest = {};

  constructor(private customerService: CustomerService,
              private messageService: MessageService,
              private authenticationService: AuthenticationService,
              private router: Router) {
  }


  createAccount() {
    if (this.customer) {
      this.customerService.registerCustomer(this.customer).subscribe({
        next: () => {
          this.authenticationRequest.username = this.customer.email;
          this.authenticationRequest.password = this.customer.password;
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
            })
        }
      });
    }
  }
}
