package com.codepro.customer;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * @author aboubacar.diallo
 * @created 31/08/2023 - 16:28
 * @project springbootfullstack
 * @package com.codepro.customer
 */

@Component
public class CustomerDTOMapper implements Function<Customer, CustomerDTO> {

    @Override
    public CustomerDTO apply(Customer customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList(),
                customer.getUsername(),
                customer.getGender(),
                customer.getProfileImageId());
    }
}
