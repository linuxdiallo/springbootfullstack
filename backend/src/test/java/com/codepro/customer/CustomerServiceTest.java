package com.codepro.customer;

import com.codepro.exception.DuplicateResourceException;
import com.codepro.exception.RequestValidationExeception;
import com.codepro.exception.ResourceNotFoundException;
import com.codepro.s3.S3Buckets;
import com.codepro.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();
    private CustomerService underTest;
    //private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        //autoCloseable  = MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(
                customerDao,
                passwordEncoder,
                customerDTOMapper,
                s3Service,
                s3Buckets);
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

        when(customerDao.existsCustomerByEmail(email)).thenReturn(false);

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

        when(customerDao.existsCustomerByEmail(email)).thenReturn(true);

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
        when(customerDao.existsCustomerById(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDao).deleteCustomerById(id);

    }

    @Test
    void willThrowDeleteCustomerByIdNotExist() {
        // Given
        int id = 10;
        when(customerDao.existsCustomerById(id)).thenReturn(false);

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

        when(customerDao.existsCustomerByEmail(newEmail)).thenReturn(false);

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

        when(customerDao.existsCustomerByEmail(newEmail)).thenReturn(false);

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

        when(customerDao.existsCustomerByEmail(newEmail)).thenReturn(true);

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

    @Test
    void updateCustomerWillThrowWhenCustomerDoesNotExist() {
        // Given
        int customerId = 10;
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(()-> underTest.updateCustomer(customerId, any()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(customerId));

        //Then
        verifyNoMoreInteractions(customerDao);
    }

    @Test
    void canUploadProfileImage() {
        // Given
        int customerId = 10;

        when(customerDao.existsCustomerById(customerId)).thenReturn(true);

        byte[] bytes = "Hello World".getBytes();

        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                bytes
        );
        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);
        // When
         underTest.uploadCustomerProfileImage(customerId, multipartFile);

        // Then

        ArgumentCaptor<String> profileImageArgumentCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(customerDao).updateCustomerProfileImageId(
                profileImageArgumentCaptor.capture(),
                eq(customerId)
        ); // we use eq expression because we are getting the first parameter value from an ArgumentCaptor

        verify(s3Service).putObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageArgumentCaptor.getValue()),
                bytes
        );
    }

    @Test
    void cannotUploadProfileImageWhenCustomerDoesNotExists() {
        // Given

        int customerId = 10;
        when(customerDao.existsCustomerById(customerId)).thenReturn(false);

        // When

          assertThatThrownBy(
                  () -> underTest.uploadCustomerProfileImage(customerId, mock(MultipartFile.class))
                 )
                  .isInstanceOf(ResourceNotFoundException.class)
                  .hasMessage("customer with id [%s] not found".formatted(customerId));

        // Then

        verify(customerDao).existsCustomerById(customerId);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }

    @Test
    void cannotUploadProfileImageWhenExceptionIsThrown() throws IOException {
        // Given
        int customerId = 10;

        when(customerDao.existsCustomerById(customerId)).thenReturn(true);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
         assertThatThrownBy(() -> {
             underTest.uploadCustomerProfileImage(customerId, multipartFile);
         })
                 .isInstanceOf(RuntimeException.class)
                 .hasMessage("failed to upload profile image")
                 .hasRootCauseInstanceOf(IOException.class);

        // Then
        verify(customerDao, never()).updateCustomerProfileImageId(any(), any());

    }

    @Test
    void canDownloadProfileImage() {
        // Given
        int customerId = 10;
        String profileImageId = "22222";
        Customer customer = new Customer(
                customerId, "Alex",
                "alex@gmail.com",
                "password", 34,
                Gender.MALE,
                profileImageId
        );
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();

        when(s3Service.getObject(bucket, "profile-images/%s/%s".formatted(customerId, profileImageId))
        ).thenReturn(expectedImage);

        // When
        byte[] actualImage = underTest.getCustomerProfileImage(customerId);

        //Then
        assertThat(actualImage).isEqualTo(expectedImage);
    }


    @Test
    void cannotDownloadProfileImageWhenCustomerDoesNotHaveProfileImageId() {
        // Given
        int customerId = 10;
        Customer customer = new Customer(
                customerId, "Alex",
                "alex@gmail.com",
                "password",
                34,
                Gender.MALE
        );
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // When
        //Then
        assertThatThrownBy(
                () -> underTest.getCustomerProfileImage(customerId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] profile image not found".formatted(customerId));

        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);
    }

    @Test
    void cannotDownloadProfileImageWhenCustomerDoesNotExist() {
        // Given
        int customerId = 10;

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        // When
        //Then
        assertThatThrownBy(
                () -> underTest.getCustomerProfileImage(customerId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(customerId));

        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);
    }
}