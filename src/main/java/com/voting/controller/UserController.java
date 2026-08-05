package com.voting.controller;

import com.voting.dto.RegisterRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody RegisterRequestDTO request) {

        return "Validation Successful";
    }
}