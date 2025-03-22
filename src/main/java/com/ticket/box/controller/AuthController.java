package com.ticket.box.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.ticket.box.domain.dto.LoginDto;
import com.ticket.box.domain.dto.ResLoginDTO;
import com.ticket.box.util.SecurityUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final SecurityUtil securityUtil;

  public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.securityUtil = securityUtil;
  }

  @PostMapping("/login")
  public ResponseEntity<ResLoginDTO> login(@RequestBody LoginDto loginDto) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDto.getUsername(), loginDto.getPassword());
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    String access_token = this.securityUtil.createToken(authentication);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    ResLoginDTO res = new ResLoginDTO();
    res.setAccessToken(access_token);

    return ResponseEntity.ok().body(res);
  }

}
