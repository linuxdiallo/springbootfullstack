package com.codepro.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age,
        Gender gender
) {
}
