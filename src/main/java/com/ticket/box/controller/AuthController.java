package com.ticket.box.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import com.ticket.box.domain.User;
import com.ticket.box.domain.request.ReqUserDTO;
import com.ticket.box.domain.require.ReqLoginDto;
import com.ticket.box.domain.response.ResLoginDTO;
import com.ticket.box.service.UserService;
import com.ticket.box.util.SecurityUtil;
import com.ticket.box.util.annotation.ApiMessage;
import com.ticket.box.util.error.DataInvalidException;
import com.ticket.box.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final SecurityUtil securityUtil;
  private final UserService userService;
  private PasswordEncoder passwordEncoder;

  @Value("${ticket.jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;

  public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
      UserService userService, PasswordEncoder passwordEncoder) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.securityUtil = securityUtil;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  @ApiMessage("Login Account")
  public ResponseEntity<ResLoginDTO> login(@RequestBody ReqLoginDto loginDto) throws IdInvalidException {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDto.getUsername(), loginDto.getPassword());
    if (this.userService.handleGetUserByUsername(loginDto.getUsername()) == null) {
      throw new IdInvalidException("Account not exists!");
    }

    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    User currentUser = this.userService.handleGetUserByUsername(loginDto.getUsername());

    ResLoginDTO res = new ResLoginDTO();

    if (currentUser != null) {
      res.setUser(new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(), currentUser.getName()));
    }

    String access_token = this.securityUtil.createAccessToken(loginDto.getUsername(), res.getUser());
    res.setAccessToken(access_token);

    String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);

    this.userService.updateUserToken(refresh_token, loginDto.getUsername());

    // set using cookie
    ResponseCookie resCookie = ResponseCookie.from("refresh_token", refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookie.toString())
        .body(res);
  }

  @GetMapping("/account")
  @ApiMessage("Fetch account")
  public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
    String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

    User currentUser = this.userService.handleGetUserByUsername(email);
    ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
    if (currentUser != null) {
      userLogin.setEmail(email);
      userLogin.setName(currentUser.getName());
      userLogin.setId(currentUser.getId());
    }

    return ResponseEntity.ok().body(userLogin);
  }

  @PostMapping("/refresh")
  @ApiMessage("Get user by refresh token ")
  public ResponseEntity<ResLoginDTO> getRefreshToken(
      @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) throws IdInvalidException {
    if (refresh_token.equals("abc")) {
      throw new IdInvalidException("You don't have access token ");
    }

    Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);

    String email = decodedToken.getSubject();

    User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);

    if (currentUser == null) {
      throw new IdInvalidException("Refresh Token invalid");
    }

    ResLoginDTO res = new ResLoginDTO();

    User currentUserDB = this.userService.handleGetUserByUsername(email);
    if (currentUserDB != null) {
      ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(),
          currentUserDB.getName());
      res.setUser(userLogin);
    }

    // Get again access_token

    String access_token = this.securityUtil.createAccessToken(email, res.getUser());

    res.setAccessToken(access_token);

    String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

    // update for user
    this.userService.updateUserToken(refresh_token, email);

    // set cookies
    ResponseCookie resCookie = ResponseCookie.from("refresh_token", new_refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookie.toString())
        .body(res);
  }

  @PostMapping("/logout")
  @ApiMessage("Logout account")
  public ResponseEntity<Void> logout()
      throws IdInvalidException {

    String email = this.securityUtil.getCurrentUserLogin().isPresent() ? this.securityUtil.getCurrentUserLogin().get()
        : "";

    if (email.equals("")) {
      throw new IdInvalidException("Access token invalid");
    }

    this.userService.updateUserToken(null, email);

    ResponseCookie resCookie = ResponseCookie.from("refresh_token", null)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookie.toString())
        .body(null);
  }

  @PostMapping("/register")
  @ApiMessage("Logout account")
  public ResponseEntity<String> getRegister(@RequestBody ReqLoginDto loginDto)
      throws IdInvalidException, DataInvalidException {
    if (this.userService.handleGetUserByUsername(loginDto.getUsername()) != null) {
      throw new IdInvalidException("This email already exist!!");
    }
    User newUser = new User();
    newUser.setEmail(loginDto.getUsername());
    String hashPassword = passwordEncoder.encode(loginDto.getPassword());
    newUser.setPassword(hashPassword);
    ReqUserDTO reqUser = new ReqUserDTO();
    reqUser.fromEntity(newUser);
    User createUser = this.userService.createNewUser(reqUser);

    if (createUser == null) {
      throw new IdInvalidException("Error !!");
    }

    return ResponseEntity.status(HttpStatus.CREATED).body("Successfully created new user");
  }
}
