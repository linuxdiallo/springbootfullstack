package com.codepro;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aboubacar.diallo
 * @created 07/06/2023 - 12:19
 * @project fullstack-professional
 * @package com.codepro
 */

@RestController
public class PingPongController {
    record PingPong(String result){};

    @GetMapping("/ping")
    public PingPong getPingPong() {
        return new PingPong("PingPong");
    }

}
