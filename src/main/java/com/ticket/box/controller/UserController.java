package com.ticket.box.controller;

import org.springframework.web.bind.annotation.RestController;
import com.ticket.box.domain.User;
import com.ticket.box.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {
  private UserService userService;
  private PasswordEncoder passwordEncoder;

  public UserController(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/users")
  public ResponseEntity<User> createNewUser(@Valid @RequestBody User user) {
    String hashPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashPassword);
    return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createNewUser(user));
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.getAllUsers());
  }

}
