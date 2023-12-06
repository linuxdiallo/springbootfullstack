import {CustomerDTO} from "./customer-DTO";


export interface AuthenticationResponse {
  token?: string;
  customerDTO: CustomerDTO;
}
