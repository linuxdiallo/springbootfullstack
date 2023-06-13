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

    record PingPong(String result){};

    @GetMapping("/pingpong")
    public PingPong getPingPong() {
        return new PingPong("hola pingpong to you!");
    }
}