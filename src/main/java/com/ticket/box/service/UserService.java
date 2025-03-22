package com.ticket.box.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticket.box.domain.User;
import com.ticket.box.repository.UserRepository;

@Service
public class UserService {
  private UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createNewUser(User user) {
    return this.userRepository.save(user);
  }

  // For login authenticate
  public User handleGetUserByUsername(String username) {
    return this.userRepository.findByEmail(username);
  }

  public List<User> getAllUsers() {
    return this.userRepository.findAll();
  }
}
