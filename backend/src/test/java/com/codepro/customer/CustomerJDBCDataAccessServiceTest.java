package com.codepro.customer;

import com.codepro.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                "password", 21,
                Gender.MALE
        );

        underTest.insertCustomer(customer);

        // When
        List<Customer> actual = underTest.selectAllCustomers();

        // Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int customerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        Optional<Customer> actual = underTest.selectCustomerById(customerId);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        int id = 0;

        // When
        var actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int customerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        var actual = underTest.selectCustomerById(customerId);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());

        });
    }

    @Test
    void existsPersonWithEmail() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);

        // When
        boolean actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsCustomerByEmail(email);

        // Then

        assertThat(actual).isFalse();
    }

    @Test
    void existsPersonWithId() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int customerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        boolean actual = underTest.existsCustomerById(customerId);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        int id = -1;

        // When
        boolean actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.deleteCustomerById(id);

        // Then
        var actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();

    }

    @Test
    void updateCustomerName() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newName = "foo";
        // When update new name
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        var actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName); // Change
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());

        });
    }

     // i commented this method because sonar was failing when try to execute this test
    @Test
    void updateCustomerEmail() {
        // Given
        String email = faker.internet().safeEmailAddress()+ "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newEmail = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When update new email
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        var actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getEmail()).isEqualTo(newEmail); // Change
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(customer.getAge());

        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        int newAge = 100;

        // When update new age
        Customer update = new Customer();
        update.setId(id);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        var actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getAge()).isEqualTo(newAge); // Change
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getName()).isEqualTo(customer.getName());

        });
    }

    @Test
    void willUpdateAllPropertiesCustomer() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When

        Customer update = new Customer();
        update.setId(id);
        update.setName("foo");
        String newEmail = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        update.setEmail(newEmail);
        update.setAge(50);

        underTest.updateCustomer(update);

        // Then

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(updated -> {
            assertThat(updated.getId()).isEqualTo(id);
            assertThat(updated.getGender()).isEqualTo(Gender.MALE);
            assertThat(updated.getName()).isEqualTo("foo");
            assertThat(updated.getEmail()).isEqualTo(newEmail);
            assertThat(updated.getAge()).isEqualTo(50);
        });
    }

    @Test
    void willNotUpdateWhenNothingChanges() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When update without no changes
        Customer update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void canUpdateProfileImageId() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int customerId = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        String profileImageId = "22222";
        underTest.updateCustomerProfileImageId(profileImageId, customerId);

        //Then
        Optional<Customer> customerOptional = underTest.selectCustomerById(customerId);
        assertThat(customerOptional)
                .isPresent()
                .hasValueSatisfying(
                        c -> assertThat(c.getProfileImageId()).isEqualTo(profileImageId)
                );
    }
}