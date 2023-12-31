package com.codepro;

import com.codepro.customer.Customer;
import com.codepro.customer.CustomerRepository;
import com.codepro.customer.Gender;
import com.codepro.s3.S3Buckets;
import com.codepro.s3.S3Service;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.InputStream;
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
            PasswordEncoder passwordEncoder,
            S3Service s3Service,
            S3Buckets s3Buckets
    ) {
        return args -> {
            String PATH = System.getProperty("user.home") + "/s3";
            System.out.printf(PATH);
            //createRandomCustomer(customerRepository, passwordEncoder);
            //testBucketUploadAndDownload(s3Service, s3Buckets);
        };
    }*/

    private  void testBucketUploadAndDownload(S3Service s3Service, S3Buckets s3Buckets) throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/image.jpeg");

        s3Service.putObject(
                s3Buckets.getCustomer(),
                "jamila",
                inputStream.readAllBytes()
        );

        byte[] object = s3Service.getObject(
                s3Buckets.getCustomer(),
                "jamila"
        );
        System.out.println("Hooray: " + new String(object));
    }

    private static void createRandomCustomer(CustomerRepository customerRepository,
                                             PasswordEncoder passwordEncoder) {
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
    }
}
