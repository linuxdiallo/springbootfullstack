package com.codepro.customer;

import com.codepro.exception.DuplicateResourceException;
import com.codepro.exception.RequestValidationExeception;
import com.codepro.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    List<Customer> getAllCustomers() {
          return customerDao.selectAllCustomers();
    }

    Customer selectCustomerById(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id))
                );
    }

    void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        // check if email exists
        if (customerDao.existsPersonWithEmail(email)) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }

        //add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age(),
                 customerRegistrationRequest.gender());
        customerDao.insertCustomer(customer);
    }

    void deleteCustomerById(Integer id) {

        if(!customerDao.existsPersonWithId(id)) {
            throw  new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(id)
            );
        }
        customerDao.deleteCustomerById(id);
    }

    public Customer getCustomer(Integer id) {
      return  customerDao.selectCustomerById(id)
              .orElseThrow(() -> new ResourceNotFoundException(
              "customer with id [%s] not found".formatted(id))
            );
    }

   void updateCustomer(Integer customerId, CustomerUpdateRequest updateRequest) {

      Customer customer = getCustomer(customerId);
      boolean changes = false;

      if(updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
          customer.setName(updateRequest.name());
          changes = true;
      }

       if(updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
           customer.setAge(updateRequest.age());
           changes = true;
       }

       if(updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
         if(customerDao.existsPersonWithEmail(updateRequest.email())) {
             throw new DuplicateResourceException(
                     "email already taken"
             );
         }
           customer.setEmail(updateRequest.email());
           changes = true;
       }

       if(!changes) {
           throw new RequestValidationExeception("no data changes found");
       }

       customerDao.updateCustomer(customer);

    }
}
