package com.codepro;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author aboubacar.diallo
 * @created 31/08/2023 - 12:21
 * @project springbootfullstack
 * @package com.codepro
 */

@TestConfiguration
public class TestConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
