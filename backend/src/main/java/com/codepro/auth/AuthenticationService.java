package com.codepro.auth;

import com.codepro.customer.Customer;
import com.codepro.customer.CustomerDTO;
import com.codepro.customer.CustomerDTOMapper;
import com.codepro.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author aboubacar.diallo
 * @created 06/09/2023 - 14:43
 * @project springbootfullstack
 * @package com.codepro.auth
 */

@Service
public class AuthenticationService {
  private final AuthenticationManager authenticationManager;
  private final CustomerDTOMapper customerDTOMapper;
  private final JWTUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 CustomerDTOMapper customerDTOMapper, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login (AuthenticationRequest request) {

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        Customer principal = (Customer) authenticate.getPrincipal();
        CustomerDTO customerDTO = customerDTOMapper.apply(principal);
        String token = jwtUtil.issueToken(customerDTO.username(), customerDTO.roles());

        return new AuthenticationResponse(token, customerDTO);
    }
}

