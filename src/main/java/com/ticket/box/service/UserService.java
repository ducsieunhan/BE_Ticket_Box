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

  public void updateUserToken(String token, String email) {
    User currentUser = this.handleGetUserByUsername(email);
    if (currentUser != null) {
      currentUser.setRefreshToken(token);
      this.userRepository.save(currentUser);
    }
  }

  public User getUserByRefreshTokenAndEmail(String token, String email) {
    return this.userRepository.findByRefreshTokenAndEmail(token, email);
  }

}
