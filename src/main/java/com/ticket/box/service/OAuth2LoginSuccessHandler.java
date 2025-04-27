package com.ticket.box.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.box.domain.Role;
import com.ticket.box.domain.User;
import com.ticket.box.domain.response.ResLoginDTO;
import com.ticket.box.repository.RoleRepository;
import com.ticket.box.repository.UserRepository;
import com.ticket.box.util.SecurityUtil;
import com.ticket.box.util.constant.AuthenticationProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private UserService userService;
  private RoleRepository roleRepository;
  private UserRepository userRepository;
  private SecurityUtil securityUtil;
  private ObjectMapper objectMapper ;
  @Value("${ticket.jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;

  public OAuth2LoginSuccessHandler(UserService userService, RoleRepository roleRepository, UserRepository userRepository, SecurityUtil securityUtil, ObjectMapper objectMapper){
    this.userService = userService;
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.securityUtil = securityUtil;
    this.objectMapper = objectMapper;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getEmail();
//    System.out.println("Email of the user: " + email);

    User user = this.userService.handleGetUserByUsername(email);
    if(user == null){
      User newUser = new User();
      newUser.setEmail(email);
      newUser.setName(oAuth2User.getName());
      newUser.setEnabled(true);
      newUser.setAuthProvider(AuthenticationProvider.GOOGLE);
      Role signUpRole = this.roleRepository.findById(1L)
              .orElseThrow(() -> new RuntimeException("Default role not found"));
      newUser.setRole(signUpRole);
      user = newUser;
      this.userRepository.save(user);
    }

    ResLoginDTO res = new ResLoginDTO();

    if (user != null) {
      res.setUser(new ResLoginDTO.UserLogin(user.getId(), user.getEmail(), user.getName()));
    }

    String access_token = this.securityUtil.createAccessToken(user.getEmail(), res.getUser());
    res.setAccessToken(access_token);

    String refresh_token = this.securityUtil.createRefreshToken(user.getEmail(), res);

    this.userService.updateUserToken(refresh_token, user.getEmail());

    // set using cookie
    ResponseCookie resCookie = ResponseCookie.from("refresh_token", refresh_token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(refreshTokenExpiration)
            .build();

    response.setHeader(HttpHeaders.SET_COOKIE, resCookie.toString());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    String jsonResponse = objectMapper.writeValueAsString(res);
//    response.getWriter().write(jsonResponse);
    String encodedData = URLEncoder.encode(jsonResponse, StandardCharsets.UTF_8.toString());

    String redirectUrl = "http://localhost:5173/oauth2/callback?" + encodedData;
    response.sendRedirect(redirectUrl);

  }

}
