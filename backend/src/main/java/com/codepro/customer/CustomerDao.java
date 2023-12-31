package com.codepro.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer customerId);
    void insertCustomer(Customer customer);
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Integer customerId);
    void deleteCustomerById(Integer customerId);
    void updateCustomer(Customer update);
    Optional<Customer> selectByEmail(String email);
    void updateCustomerProfileImageId(String profileImageId, Integer customerId);
}
