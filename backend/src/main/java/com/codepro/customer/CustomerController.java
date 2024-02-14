package com.codepro.customer;

import com.codepro.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jdk.jfr.ContentType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Get all customers")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found all customers"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request supplied",
                    content = @Content
            ),
    })
    @GetMapping
    public List<CustomerDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @Operation(summary = "Get a customer by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the customer",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Customer.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content
            )

    })
    @GetMapping("/{customerId}")
    public CustomerDTO getCustomer(@Parameter(description = "id of customer to be searched")
                                   @PathVariable("customerId") Integer id) {
        return customerService.selectCustomerById(id);
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest
                                                      request) {
        customerService.addCustomer(request);

        String token = jwtUtil.issueToken(request.email(), "ROLE_USER");

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .build();
    }

    @Operation(summary = "Delete a customer by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer found and deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content
            )

    })
    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@Parameter(description = "id of customer to be deleted")
                               @PathVariable("customerId") Integer customerId) {
        customerService.deleteCustomerById(customerId);
    }

    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer customerId,
                               @RequestBody CustomerUpdateRequest updateRequest) {

        customerService.updateCustomer(customerId, updateRequest);

    }

    @PostMapping(
            value = "{customerId}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadCustomerProfileImage(@PathVariable("customerId") Integer customerId,
                                           @RequestParam("file") MultipartFile file) {
        customerService.uploadCustomerProfileImage(customerId, file);

    }

    @GetMapping(
            value = "{customerId}/profile-image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[] getCustomerProfileImage(@PathVariable("customerId") Integer customerId) {

        return customerService.getCustomerProfileImage(customerId);

    }


}
