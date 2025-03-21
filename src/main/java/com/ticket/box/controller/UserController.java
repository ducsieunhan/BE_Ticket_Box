package com.ticket.box.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ticket.box.domain.RestResponse;
import com.ticket.box.domain.User;
import com.ticket.box.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {
  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/users")
  public ResponseEntity<User> createNewUser(@Valid @RequestBody User user) {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createNewUser(user));
  }

}
