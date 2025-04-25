package com.ticket.box.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ticket.box.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);

  User findByRefreshTokenAndEmail(String token, String email);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  // @Query("SELECT u FROM User u JOIN u.events e WHERE e.id = :eventId")
  // List<User> findUsersByEventId(@Param("eventId") Long eventId);

}
