package com.codepro.auth;

import com.codepro.customer.CustomerDTO;

/**
 * @author aboubacar.diallo
 * @created 06/09/2023 - 14:38
 * @project springbootfullstack
 * @package com.codepro.auth
 */

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO) {
}
