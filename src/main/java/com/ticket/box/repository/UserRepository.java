package com.ticket.box.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticket.box.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);

  User findByRefreshTokenAndEmail(String token, String email);
}
