package com.codepro.customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aboubacar.diallo
 * @created 08/06/2023 - 17:01
 * @project springbootfullstack
 * @package com.codepro.customer
 */


@RestController
public class TestController {
    private static int COUNT = 0;
    record PingPong(String result){};

    @GetMapping("/ping")
    public PingPong getPingPong() {
        return new PingPong("Pong: %s".formatted(++COUNT));

    }
}