package com.ticket.box.controller;

import com.ticket.box.domain.require.VerifyUserDto;
import com.ticket.box.service.AuthService;
import com.ticket.box.util.error.VerificationException;
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
import org.springframework.web.bind.annotation.*;

import com.ticket.box.domain.User;
import com.ticket.box.domain.request.ReqUserDTO;
import com.ticket.box.domain.require.ReqLoginDto;
import com.ticket.box.domain.response.ResLoginDTO;
import com.ticket.box.service.UserService;
import com.ticket.box.util.SecurityUtil;
import com.ticket.box.util.annotation.ApiMessage;
import com.ticket.box.util.error.DataInvalidException;
import com.ticket.box.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final SecurityUtil securityUtil;
  private final UserService userService;
  private PasswordEncoder passwordEncoder;

  private AuthService authService;

  @Value("${ticket.jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;

  public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
      UserService userService, PasswordEncoder passwordEncoder, AuthService authService) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.securityUtil = securityUtil;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.authService = authService;
  }

  @PostMapping("/login")
  @ApiMessage("Login Account")
  public ResponseEntity<ResLoginDTO> login(@RequestBody ReqLoginDto loginDto) throws IdInvalidException, VerificationException {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDto.getUsername(), loginDto.getPassword());
    if (this.userService.handleGetUserByUsername(loginDto.getUsername()) == null) {
      throw new IdInvalidException("Account not exists!");
    }

    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    User currentUser = this.userService.handleGetUserByUsername(loginDto.getUsername());

    if(!currentUser.isEnabled()){
      throw new VerificationException("Account not verified");
    }

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

  @GetMapping("/login")
  public void oauth2Login() {
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
    User newUser = this.authService.signUp(loginDto);

    if (newUser == null) {
      throw new IdInvalidException("Error !!");
    }
    return ResponseEntity.status(HttpStatus.CREATED).body("Successfully created new user");
  }

  @PostMapping("/verify")
  public ResponseEntity<String> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
    try {
      authService.verifyUser(verifyUserDto);
      return ResponseEntity.ok("Account verified successfully");
    } catch (IdInvalidException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/resend")
  public ResponseEntity<String> resendVerificationCode(@RequestParam String email) {
    try {
      authService.resendVerificationCode(email);
      return ResponseEntity.ok("Verification code sent");
    } catch (IdInvalidException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/reset_code")
  public ResponseEntity<String> resetCodePassword(@RequestParam String email) {
    try {
      authService.sendCodeResetPassword(email);
      return ResponseEntity.ok("Verification code sent");
    } catch (IdInvalidException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/reset")
  @ApiMessage("Reset password")
  public ResponseEntity<String> resetPassword(@RequestBody ReqLoginDto loginDto)
          throws IdInvalidException, DataInvalidException {
    if (this.userService.handleGetUserByUsername(loginDto.getUsername()) == null) {
      throw new IdInvalidException("Account not exist!!");
    }
    User newUser = this.authService.resetPassword(loginDto);

    if (newUser == null) {
      throw new IdInvalidException("Error !!");
    }
    return ResponseEntity.status(HttpStatus.CREATED).body("Successfully update new password");
  }


}
