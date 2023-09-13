package com.codepro.auth;

/**
 * @author aboubacar.diallo
 * @created 06/09/2023 - 14:36
 * @project springbootfullstack
 * @package com.codepro.auth
 */

public record AuthenticationRequest(
        String username,
        String password
) {

}
