package com.codepro;

import com.codepro.customer.Customer;
import com.codepro.customer.CustomerRepository;
import com.codepro.customer.Gender;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
      SpringApplication.run(Main.class, args);

    }

   /* @Bean
    CommandLineRunner runner(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            Faker faker = new Faker();
            Random random = new Random();

            int age = random.nextInt(16, 99);

            Gender gender = (age % 2 == 0) ? Gender.MALE : Gender.FEMALE;

            Customer customer = new Customer(
                    faker.name().fullName(),
                    faker.internet().safeEmailAddress(),
                    passwordEncoder.encode(UUID.randomUUID().toString()),
                    age,
                    gender
                    );

            customerRepository.save(customer);

        };
    }

*/
}
