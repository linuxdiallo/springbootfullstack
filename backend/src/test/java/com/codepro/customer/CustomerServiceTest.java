package com.codepro.customer;

import com.codepro.exception.DuplicateResourceException;
import com.codepro.exception.RequestValidationExeception;
import com.codepro.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();
    private CustomerService underTest;
    //private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        //autoCloseable  = MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(
                customerDao,
                passwordEncoder,
                customerDTOMapper
        );
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void selectCustomerById() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex",
                "alex@gmail.com",
                "password", 34,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void willThrowWhenSelectCustomerByIdReturnEmptyOptional() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex",
                "alex@gmail.com",
                "password", 34,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.selectCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", "alex@gmail.com", "password", 21, Gender.MALE
        );

        String passwordHash = "$$$.jdkdjd%";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        // When
        underTest.addCustomer(request);

        // Then

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturCustomer = customerArgumentCaptor.getValue();

        assertThat(capturCustomer.getId()).isEqualTo(null);
        assertThat(capturCustomer.getName()).isEqualTo(request.name());
        assertThat(capturCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturCustomer.getPassword()).isEqualTo(passwordHash);

    }

    @Test
    void willThrowWhenEmailExistsWhileAddindCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", "alex@gmail.com", "password", 21, Gender.MALE
        );

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Then

        verify(customerDao, never()).insertCustomer(any());

    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 10;
        when(customerDao.existsPersonWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDao).deleteCustomerById(id);

    }

    @Test
    void willThrowDeleteCustomerByIdNotExist() {
        // Given
        int id = 10;
        when(customerDao.existsPersonWithId(id)).thenReturn(false);

        // When
        // Then
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).deleteCustomerById(id);

    }

    @Test
    void canGetCustomer() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex",
                "alex@gmail.com",
                "password", 23,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomer(10);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhencanGetCustomerReturnOptionalEmpty() {
        // Given
        int id = 10;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When

        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given
        int id = 10;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.selectCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void canUpdateAllCustomersProperties() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@gmail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", newEmail, 23
        );

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());

    }


    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com" , "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", null, null
        );

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());

    }


    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@gmail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());

    }


    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com" , "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        int newAge = 30;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, newAge
        );

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());

    }


    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com" , "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@gmail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Then

        verify(customerDao, never()).updateCustomer(any());

    }

    @Test
    void willThrowWhenNoDataChangeWhenUpdateCustomer() {
        // Given
        int id = 10;

        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", "password", 19,
                Gender.MALE);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge()
        );

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationExeception.class)
                .hasMessage("no data changes found");
        // Then

        verify(customerDao, never()).updateCustomer(any());
    }
}