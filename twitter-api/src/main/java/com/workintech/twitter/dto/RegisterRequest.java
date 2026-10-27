package com.workintech.twitter.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 30) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        @Past LocalDate birthday,
        @Size(max = 100) String location,
        @Size(max = 160) String bio) {
}
