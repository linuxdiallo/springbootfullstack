package com.codepro.customer;

import com.codepro.exception.DuplicateResourceException;
import com.codepro.exception.RequestValidationExeception;
import com.codepro.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }

    List<CustomerDTO> getAllCustomers() {
        return customerDao.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    CustomerDTO selectCustomerById(Integer id) {

        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
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
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender()
        );

        customerDao.insertCustomer(customer);
    }

    void deleteCustomerById(Integer id) {

        if (!customerDao.existsPersonWithId(id)) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(id)
            );
        }
        customerDao.deleteCustomerById(id);
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id))
                );
    }

    void updateCustomer(Integer customerId,
                        CustomerUpdateRequest updateRequest) {

        Customer customer = customerDao.selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId))
                );

        boolean changes = false;

        if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
            changes = true;
        }

        if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsPersonWithEmail(updateRequest.email())) {
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationExeception("no data changes found");
        }

        customerDao.updateCustomer(customer);

    }
}
