package com.example.jwtdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/healthCheck")
    public ResponseEntity<?> getAdminHealthCheck(){
        return new ResponseEntity<>("Admin health check is up and running", HttpStatus.OK);
    }
}
