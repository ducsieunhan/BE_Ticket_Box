package com.ticket.box.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ticket.box.domain.Role;
import com.ticket.box.domain.User;
import com.ticket.box.domain.dto.ResUserDTO;
import com.ticket.box.repository.RoleRepository;
import com.ticket.box.repository.UserRepository;

@Service
public class UserService {
  private UserRepository userRepository;
  private RoleRepository roleRepository;

  public UserService(UserRepository userRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  public User createNewUser(ResUserDTO resUser) {
    User user = loadUserFromResUserDTO(resUser);
    return this.userRepository.save(user);
  }

  // For login authenticate
  public User handleGetUserByUsername(String username) {
    return this.userRepository.findByEmail(username);
  }

  public List<User> getAllUsers() {
    return this.userRepository.findAll();
  }

  public Optional<User> getUserById(Long id) {
    return this.userRepository.findById(id);
  }

  public void deleteUserById(long id) {
    this.userRepository.deleteById(id);
  }

  public User updateUser(User reqUser) {
    User currentUser = getUserById(reqUser.getId()).get();
    if (currentUser != null) {

      currentUser.setDob(reqUser.getDob());
      currentUser.setEmail(reqUser.getEmail());
      currentUser.setName(reqUser.getName());
      currentUser.setPassword(reqUser.getPassword());
      currentUser.setPhone(reqUser.getPhone());
      currentUser.setDistrict(reqUser.getDistrict());
      currentUser.setHouseNumber(reqUser.getHouseNumber());
      currentUser.setProvince(reqUser.getProvince());
      currentUser.setWard(reqUser.getWard());
      currentUser.setRefreshToken(reqUser.getRefreshToken());
    }
    return this.userRepository.save(currentUser);
  }

  public ResUserDTO convertResUserDTO(User user) {
    ResUserDTO res = new ResUserDTO();
    ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
    if (user.getRole() != null) {
      roleUser.setId(user.getRole().getId());
      roleUser.setName(user.getRole().getName());
      res.setRole(roleUser.getName());
    }
    res.setId(user.getId());
    res.setDob(user.getDob());
    res.setEmail(user.getEmail());
    res.setName(user.getName());
    res.setPassword(user.getPassword());
    res.setPhone(user.getPhone());
    res.setDistrict(user.getDistrict());
    res.setHouseNumber(user.getHouseNumber());
    res.setProvince(user.getProvince());
    res.setWard(user.getWard());

    return res;
  }

  public User loadUserFromResUserDTO(ResUserDTO resUserDTO) {
    User user = new User();
    Role role = this.roleRepository.findByName(resUserDTO.getRole());
    user.setDistrict(resUserDTO.getDistrict());
    user.setDob(resUserDTO.getDob());
    user.setEmail(resUserDTO.getEmail());
    user.setHouseNumber(resUserDTO.getHouseNumber());
    user.setName(resUserDTO.getName());
    user.setPassword(resUserDTO.getPassword());
    user.setPhone(resUserDTO.getPhone());
    user.setProvince(resUserDTO.getProvince());
    user.setRole(role);
    user.setWard(resUserDTO.getWard());
    return user;
  }

}
