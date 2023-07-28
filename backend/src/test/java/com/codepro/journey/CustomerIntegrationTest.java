package com.codepro.journey;


import com.codepro.customer.Customer;
import com.codepro.customer.CustomerRegistrationRequest;
import com.codepro.customer.CustomerUpdateRequest;
import com.codepro.customer.Gender;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "/api/v1/customers";

    @Test
    void canRegisterACustomer() {

        // Create a registration request
        Faker faker = new Faker();

        Name fakeName = faker.name();

        String name = fakeName.fullName();
        String email = fakeName.lastName() + "-" + UUID.randomUUID() + "@foobarhello123.com";
        int age = RANDOM.nextInt(1, 100);

        Gender gender = (age % 2 == 0) ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );

        // Send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // Get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // Make sure that customer is present

        Customer expectedCustomer = new Customer(
                name, email, age,
                gender);

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);
        // Get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {

        // Create a registration request
        Faker faker = new Faker();

        Name fakeName = faker.name();
        String name = fakeName.fullName();
        String email = fakeName.lastName() + "-" + UUID.randomUUID() + "@foobarhello123.com";

        int age = RANDOM.nextInt(1, 100);
        Gender gender = (age % 2 == 0) ? Gender.MALE : Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );

        // Send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // Get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // Make sure that customer is present

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

       // delete customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // Get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // Create a registration request
        Faker faker = new Faker();

        Name fakeName = faker.name();
        String name = fakeName.fullName();
        String email = fakeName.lastName() + "-" + UUID.randomUUID() + "@foobarhello123.com";

        int age = RANDOM.nextInt(1, 100);

        Gender gender = (age % 2 == 0) ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );

        // Send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // Get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // Make sure that customer is present

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // update customer

        String newName = "Ali";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Get customer by id

        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id, newName, email, age,
                gender);

        assertThat(updatedCustomer).isEqualTo(expected);

    }
}
