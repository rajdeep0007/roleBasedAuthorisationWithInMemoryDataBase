package com.example.jwtdemo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@Builder
public class LoginRequest {

    private String username;
    private String password;
}
