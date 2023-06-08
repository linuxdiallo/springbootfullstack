package com.codepro;

import org.springframework.web.bind.annotation.RestController;

/**
 * @author aboubacar.diallo
 * @created 08/06/2023 - 14:52
 * @project springbootfullstack
 * @package com.codepro
 */

@RestController
public class PinpongController {

    record PinPong(String result){}
}
