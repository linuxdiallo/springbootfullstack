package com.codepro.customer;

import java.util.List;

/**
 * @author aboubacar.diallo
 * @created 31/08/2023 - 16:12
 * @project springbootfullstack
 * @package com.codepro.customer
 */

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Integer age,
        List<String> roles,
        String username,
        Gender gender
) {

}
