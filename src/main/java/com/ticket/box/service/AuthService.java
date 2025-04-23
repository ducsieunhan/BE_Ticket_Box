package com.ticket.box.service;

import com.ticket.box.domain.Role;
import com.ticket.box.domain.User;
import com.ticket.box.domain.require.ReqLoginDto;
import com.ticket.box.domain.require.VerifyUserDto;
import com.ticket.box.repository.RoleRepository;
import com.ticket.box.repository.UserRepository;
import com.ticket.box.util.error.IdInvalidException;
import com.ticket.box.util.error.VerificationException;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final EmailService emailService;
  private PasswordEncoder passwordEncoder;
  private UserService userService;

  private RoleRepository roleRepository;
  public AuthService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder,
                     UserService userService, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
    this.roleRepository = roleRepository;
  }

  public User signUp(ReqLoginDto loginDto){
    User newUser = new User();
    newUser.setEmail(loginDto.getUsername());
    String hashPassword = passwordEncoder.encode(loginDto.getPassword());
    newUser.setPassword(hashPassword);

    Role signUpRole = this.roleRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
    newUser.setRole(signUpRole);

    newUser.setVerificationCode(generateVerificationCode());
    newUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
    newUser.setEnabled(false);

    sendVerificationEmail(newUser);
    return this.userRepository.save(newUser);
  }

  public void verifyUser(VerifyUserDto input) throws IdInvalidException{
    if (this.userService.handleGetUserByUsername(input.getEmail()) == null) {
      throw new IdInvalidException("This email not exist!!");
    }

    User user = userRepository.findByEmail(input.getEmail());

      if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
        throw new VerificationException("Verification code has expired");
      }
      if (user.getVerificationCode().equals(input.getVerificationCode())) {
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
      } else {
        throw new VerificationException("Invalid verification code");
      }
  }

  public void resendVerificationCode(String email) throws IdInvalidException {

    if (this.userService.handleGetUserByUsername(email) == null) {
      throw new IdInvalidException("This email not exist!!");
    }

    User user = userRepository.findByEmail(email);
      if (user.isEnabled()) {
        throw new VerificationException("Account is already verified");
      }
      user.setVerificationCode(generateVerificationCode());
      user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
      sendVerificationEmail(user);
      userRepository.save(user);

  }

  private void sendVerificationEmail(User user) { //TODO: Update with company logo
    String subject = "Account Verification";
    String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
    String htmlMessage = "<html>"
            + "<body style=\"font-family: Arial, sans-serif;\">"
            + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
            + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
            + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
            + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
            + "<h3 style=\"color: #333;\">Verification Code:</h3>"
            + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
            + "</div>"
            + "</div>"
            + "</body>"
            + "</html>";

    try {
      emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
    } catch (MessagingException e) {
      // Handle email sending exception
      e.printStackTrace();
    }
  }


  private String generateVerificationCode() {
    Random random = new Random();
    int code = random.nextInt(900000) + 100000;
    return String.valueOf(code);
  }
}
