package com.example.jwtdemo.controller;

import com.example.jwtdemo.model.LoginRequest;
import com.example.jwtdemo.service.CustomUserDetailsService;
import com.example.jwtdemo.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
class PublicController {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping(value = "/healthCheck")
    public ResponseEntity<?> healthCheck() {
        log.info("Use spring security default username and password printed in the console in case you have " +
                "not configured custom UserDetailsService");
        log.info("use customeuserdetails service credentials if you have configured");
        log.info("in case , you have configured CustomSecurityConfig where you have allowed healthCheck endpoint then " +
                "you don't need to pass and credentials");
        log.info("in case we are using in memory database where we have role consiederd in the InmemoryDB , we don't need to pass role in the spring config");
        return new ResponseEntity<>("Public health check is up and running", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }
}