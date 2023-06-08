package com.codepro.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    Customer getCustomer(@PathVariable("customerId") Integer id) {
        return customerService.selectCustomerById(id);
    }

    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest
                                             customerRegistrationRequest) {
        customerService.addCustomer(customerRegistrationRequest);
    }

    @DeleteMapping("/{customerId}")
    void deleteCustomer(@PathVariable("customerId") Integer customerId) {
         customerService.deleteCustomerById(customerId);
    }

    @PutMapping("{customerId}")
    void updateCustomer(@PathVariable("customerId") Integer customerId,
                        @RequestBody CustomerUpdateRequest updateRequest) {

        customerService.updateCustomer(customerId, updateRequest);

    }


}
