package com.ticket.box.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.ticket.box.domain.User;
import com.ticket.box.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {
  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/users")
  public User createNewUser(@RequestBody User user) {
    return this.userService.createNewUser(user);
  }

}
