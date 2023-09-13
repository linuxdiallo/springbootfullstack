package com.codepro.customer;

import com.codepro.AbstractTestContainers;
import com.codepro.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
class CustomerRepositoryTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void existsCustomerByEmail() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.save(customer);

        // When
        var actual = underTest.existsCustomerByEmail(email);
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailNotPresent() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        var actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                Gender.MALE
        );
        underTest.save(customer);
        int customerId = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        var actual = underTest.existsCustomerById(customerId);
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {
        // Given
        int id = 0;

        // When
        var actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isFalse();
    }
}