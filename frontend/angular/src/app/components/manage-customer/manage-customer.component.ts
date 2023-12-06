import {
  Component,
  EventEmitter,
  Input,
  Output
} from '@angular/core';
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";

@Component({
  selector: 'app-manage-customer',
  templateUrl: './manage-customer.component.html',
  styleUrls: ['./manage-customer.component.scss']
})
export class ManageCustomerComponent {
  @Input()
  customer: CustomerRegistrationRequest = {}

  @Input()
  operation:  'create' | 'update' = 'create';

  @Output()
  submitEvent: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>();

  @Output()
  cancelEvent: EventEmitter<void> = new EventEmitter<void>();
  onSubmit() {
    this.submitEvent.emit(this.customer);
  }

  get isCustomerValid(): boolean {
    return this.hasLength(this.customer.name)
      && this.hasLength(this.customer.email)
      && this.customer.age !== null && this.customer.age !== undefined &&
      (
        this.operation === 'update' ||
        this.hasLength(this.customer.password) &&
        this.hasLength(this.customer.gender)
      )
  }

  private hasLength(input: string | undefined): boolean {
    return input !== null && input !== undefined && input.length > 0;
  }

  protected readonly onsubmit = onsubmit;
  protected readonly oncancel = oncancel;

  onCancel() {
    this.cancelEvent.emit();
  }
}
