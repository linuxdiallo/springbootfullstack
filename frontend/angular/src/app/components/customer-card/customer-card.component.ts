import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CustomerDTO} from "../../models/customer-DTO";

@Component({
  selector: 'app-customer-card',
  templateUrl: './customer-card.component.html',
  styleUrls: ['./customer-card.component.scss']
})
export class CustomerCardComponent {

   @Input()
   customer: CustomerDTO = {};

   @Input()
   customerIndex = 0;

   @Output()
   deleteEvent: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();

  @Output()
  updateEvent: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();

  get customerImage() {
     const gender = this.customer.gender === 'MALE' ? 'men':'women';
     return `https://randomuser.me/api/portraits/${gender}/${this.customerIndex}.jpg`
   }

   onDelete() {
     this.deleteEvent.emit(this.customer);
   }

  onUpdate() {
    this.updateEvent.emit(this.customer);
  }
}
