package com.ticket.box.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ticket.box.domain.User;
import com.ticket.box.domain.dto.ResUserDTO;
import com.ticket.box.service.UserService;
import com.ticket.box.util.annotation.ApiMessage;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
  private UserService userService;
  private PasswordEncoder passwordEncoder;

  public UserController(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/users")
  @ApiMessage("/Create new user")
  public ResponseEntity<ResUserDTO> createNewUser(@Valid @RequestBody ResUserDTO userDTO) {
    User user = this.userService.createNewUser(userDTO);
    String hashPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashPassword);
    return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertResUserDTO(user));
  }

  @GetMapping("/users")
  @ApiMessage("Get all users")
  public ResponseEntity<List<ResUserDTO>> getUsers() {
    List<User> users = this.userService.getAllUsers();
    List<ResUserDTO> resUserDTOs = users.stream().map(userService::convertResUserDTO).collect(Collectors.toList());
    return ResponseEntity.status(HttpStatus.CREATED).body(resUserDTOs);
  }

  @GetMapping("/users/{id}")
  @ApiMessage("Get user by id")
  public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") Long id) {

    Optional<User> optUser = this.userService.getUserById(id);
    if (!optUser.isPresent()) {
      return null;
    }
    User user = optUser.get();
    return ResponseEntity.ok().body(this.userService.convertResUserDTO(user));
  }

  @DeleteMapping("/users/delete/{id}")
  @ApiMessage("Delete user by id")
  public ResponseEntity<String> deleteUserById(@PathVariable("id") Long id) {
    this.userService.deleteUserById(id);
    return ResponseEntity.ok().body("User deleted");
  }

  @PutMapping("/users/update")
  @ApiMessage("Update user")
  public ResponseEntity<ResUserDTO> updateUser(@Valid @RequestBody User user) {
    User updatedUser = this.userService.updateUser(user);
    return ResponseEntity.ok().body(this.userService.convertResUserDTO(updatedUser));
  }

}
