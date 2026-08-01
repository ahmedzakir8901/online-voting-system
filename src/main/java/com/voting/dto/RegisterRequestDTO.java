package com.voting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotBlank(message = "CNIC is required")
    @Pattern(
            regexp = "^\\d{5}-\\d{7}-\\d{1}$",
            message = "CNIC must be in the format 42101-1234567-1"
    )
    private String cnic;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^03\\d{9}$",
            message = "Phone number must be like 03001234567"
    )
    private String phoneNumber;
}