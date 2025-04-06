package com.example.jwtdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@Slf4j
class PublicController {

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
}