package com.safebox.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class LoginRequest {

    @NotBlank(message = "required email")
    private String email;

    @NotBlank(message = "required password")
    private String password;

}
