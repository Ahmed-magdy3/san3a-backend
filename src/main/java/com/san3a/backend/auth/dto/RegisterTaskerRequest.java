package com.san3a.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterTaskerRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name max length is 100")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name max length is 100")
        String lastName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9+\\-]{8,20}$", message = "Invalid phone number")
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]+$",
                message = "Password must contain uppercase, lowercase and number."
        )
                String password
) {
}
