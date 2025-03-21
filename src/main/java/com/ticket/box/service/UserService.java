package com.ticket.box.service;

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

  public User handleGetUserByUsername(String username) {
    return this.userRepository.findByEmail(username);
  }
}
