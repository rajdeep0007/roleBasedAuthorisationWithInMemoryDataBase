package com.example.jwtdemo.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {

    //    The idea behind this implementation is to ensure that the user who only has role
//    as "USER" can access this health check, endpoint and we will try to implement the same using JWT
    @GetMapping("/healthCheck")
    public ResponseEntity<?> getUserHealthCheck() {
        return new ResponseEntity<>("User health check is up and running", HttpStatus.OK);
    }

}
