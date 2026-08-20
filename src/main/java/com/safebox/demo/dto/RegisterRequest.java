package com.safebox.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "username required")
    private String username;

    @NotBlank(message = "password required")
    private String password;

    @Email
    @NotBlank(message = "email required")
    private String email;

}
