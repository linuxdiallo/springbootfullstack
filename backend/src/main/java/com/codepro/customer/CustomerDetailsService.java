package com.codepro.customer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author aboubacar.diallo
 * @created 29/08/2023 - 11:02
 * @project springbootfullstack
 * @package com.codepro.customer
 */

@Service
public class CustomerDetailsService implements UserDetailsService {
    private final CustomerDao customerDao;

    public CustomerDetailsService(@Qualifier("list") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerDao.selectByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Username " + username + " not found")
                );
    }
}
