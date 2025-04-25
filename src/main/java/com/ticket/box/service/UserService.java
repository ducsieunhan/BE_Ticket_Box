package com.ticket.box.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ticket.box.domain.Role;
import com.ticket.box.domain.User;
import com.ticket.box.domain.request.ReqUserDTO;
import com.ticket.box.domain.response.ResUserDTO;
import com.ticket.box.repository.RoleRepository;
import com.ticket.box.repository.UserRepository;
import com.ticket.box.util.error.DataInvalidException;

@Service
public class UserService {
  private UserRepository userRepository;
  private RoleRepository roleRepository;
  private ModelMapper modelMapper;

  public UserService(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.modelMapper = modelMapper;
  }

  public User createNewUser(ReqUserDTO reqUser) throws DataInvalidException {
    // if (reqUser.getRoleId() != 1 || reqUser.getRoleId() != 2) {
    // throw new IdInvalidException("Role is not exist");
    // }
    if (reqUser.getPhone() != null) {
      if (this.userRepository.existsByPhone(reqUser.getPhone())) {
        throw new DataInvalidException("Phone is already exists");
      }
    }
    if (this.userRepository.existsByEmail(reqUser.getEmail())) {
      throw new DataInvalidException("Email is already exists");
    }

    User user = loadUserFromReqUserDTO(reqUser);
    // User user = modelMapper.map(reqUser, User.class);
    Role role = this.roleRepository.findById(reqUser.getRole()).get();
    user.setRole(role);
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

  public User updateUser(ReqUserDTO reqUser) {
    User currentUser = this.userRepository.findByEmail(reqUser.getEmail());
    if (currentUser != null) {

      currentUser.setDob(reqUser.getDob());
      currentUser.setEmail(reqUser.getEmail());
      currentUser.setName(reqUser.getName());
      if (reqUser.getPassword() != null) {
        currentUser.setPassword(reqUser.getPassword());
      }
      currentUser.setPhone(reqUser.getPhone());
      currentUser.setDistrict(reqUser.getDistrict());
      currentUser.setHouseNumber(reqUser.getHouseNumber());
      currentUser.setProvince(reqUser.getProvince());
      currentUser.setWard(reqUser.getWard());
      currentUser.setAvatar(reqUser.getAvatar());
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
    res.setPassword(null);
    res.setPhone(user.getPhone());
    res.setDistrict(user.getDistrict());
    res.setHouseNumber(user.getHouseNumber());
    res.setProvince(user.getProvince());
    res.setWard(user.getWard());
    res.setAvatar(user.getAvatar());
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
    user.setPhone(resUserDTO.getPhone());
    user.setProvince(resUserDTO.getProvince());
    user.setRole(role);
    user.setWard(resUserDTO.getWard());
    user.setAvatar(resUserDTO.getAvatar());
    return user;
  }

  public User loadUserFromReqUserDTO(ReqUserDTO reqUserDTO) throws DataInvalidException {
    User user = new User();
    Optional<Role> role = this.roleRepository.findById(reqUserDTO.getRole());
    if (!role.isPresent()) {
      throw new DataInvalidException("Role is not exist");
    }
    user.setDistrict(reqUserDTO.getDistrict());
    user.setDob(reqUserDTO.getDob());
    user.setEmail(reqUserDTO.getEmail());
    user.setHouseNumber(reqUserDTO.getHouseNumber());
    user.setName(reqUserDTO.getName());
    user.setPassword(reqUserDTO.getPassword());
    user.setPhone(reqUserDTO.getPhone());
    user.setProvince(reqUserDTO.getProvince());
    user.setRole(role.get());
    user.setWard(reqUserDTO.getWard());
    user.setAvatar(reqUserDTO.getAvatar());
    return user;
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
