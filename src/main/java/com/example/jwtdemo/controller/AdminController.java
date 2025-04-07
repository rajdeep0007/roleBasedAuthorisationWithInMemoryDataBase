package com.example.jwtdemo.controller;

import com.example.jwtdemo.model.RequestUserPayload;
import com.example.jwtdemo.model.User;
import com.example.jwtdemo.repository.UserRepository;
import com.example.jwtdemo.service.AdminService;
import com.example.jwtdemo.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/healthCheck")
    public ResponseEntity<?> getAdminHealthCheck(){
        return new ResponseEntity<>("Admin health check is up and running", HttpStatus.OK);
    }


    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody User request) throws BadRequestException {
       User response =  adminService.addUser(request);
       return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
}
