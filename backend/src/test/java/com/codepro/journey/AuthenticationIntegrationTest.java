package com.codepro.journey;

import com.codepro.auth.AuthenticationRequest;
import com.codepro.auth.AuthenticationResponse;
import com.codepro.customer.CustomerDTO;
import com.codepro.customer.CustomerRegistrationRequest;
import com.codepro.customer.Gender;
import com.codepro.jwt.JWTUtil;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author aboubacar.diallo
 * @created 06/09/2023 - 15:48
 * @project springbootfullstack
 * @package com.codepro.journey
 */

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthenticationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;
    private static final Random RANDOM = new Random();
    private static final String AUTHENTICATION_PATH = "/api/v1/auth";
    private static final String CUSTOMER_PATH = "/api/v1/customers";


    @Test
    void canLogin() {
        // Given
        // Create a registration request
        Faker faker = new Faker();

        Name fakeName = faker.name();

        String name = fakeName.fullName();
        String email = fakeName.lastName() + "-" + UUID.randomUUID() + "@foobarhello123.com";

        int age = RANDOM.nextInt(1, 100);

        Gender gender = (age % 2 == 0) ? Gender.MALE : Gender.FEMALE;

        String password = "password";
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, password, age, gender
        );

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                email,
                password
        );

        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(authenticationRequest),
                        AuthenticationRequest.class
                )
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // Send a post request
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(customerRegistrationRequest),
                        CustomerRegistrationRequest.class
                )
                .exchange()
                .expectStatus()
                .isOk();


        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(authenticationRequest),
                        AuthenticationRequest.class
                )
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        // Then

        String jwtToken = result.getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);
        AuthenticationResponse authenticationResponse = result.getResponseBody();

        CustomerDTO customerDTO = authenticationResponse.customerDTO();

        // When

        assertThat(jwtUtil.isTokenValid(
                jwtToken,
                customerDTO.username())).isTrue();

        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));




    }
}
