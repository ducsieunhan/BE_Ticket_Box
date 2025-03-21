package com.ticket.box.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import com.ticket.box.domain.dto.LoginDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginDto> login(@RequestBody LoginDto loginDto) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDto.getUsername(), loginDto.getPassword());
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    return ResponseEntity.ok().body(loginDto);
  }

}
