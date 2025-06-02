package com.ticket.box.UnitTest.Service;

import com.ticket.box.domain.Role;
import com.ticket.box.domain.User;
import com.ticket.box.domain.require.ReqLoginDto;
import com.ticket.box.domain.require.VerifyUserDto;
import com.ticket.box.repository.RoleRepository;
import com.ticket.box.repository.UserRepository;
import com.ticket.box.service.AuthService;
import com.ticket.box.service.EmailService;
import com.ticket.box.service.UserService;
import com.ticket.box.util.error.IdInvalidException;
import com.ticket.box.util.error.VerificationException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private UserRepository userRepository;
  @Mock
  private EmailService emailService;
  @Mock
  private UserService userService;
  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private AuthService authService;

  private ReqLoginDto loginDto;
  private Role defaultRole;
  private User user;

  @BeforeEach
  void setUp() {
    loginDto = new ReqLoginDto();
    loginDto.setUsername("test@example.com");
    loginDto.setPassword("password123");

    defaultRole = new Role();
    defaultRole.setId(1L);
    defaultRole.setName("USER");

    user = new User();
    user.setEmail(loginDto.getUsername());
    user.setPassword("abcabc");
    user.setRole(defaultRole);
    user.setVerificationCode("code");
    user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
    user.setEnabled(false);
  }

  @Test
  void shouldSignUpSuccess() throws MessagingException {
    when(passwordEncoder.encode(anyString())).thenReturn("abcabc");
    when(roleRepository.findById(1L)).thenReturn(Optional.of(defaultRole));
    when(userRepository.save(any(User.class))).thenReturn(user);

    User result = authService.signUp(loginDto);

    assertNotNull(result);
    assertEquals(loginDto.getUsername(), result.getEmail());
    assertEquals("abcabc", result.getPassword());
    assertFalse(result.isEnabled());
    assertNotNull(result.getVerificationCode());
    assertNotNull(result.getVerificationCodeExpiresAt());

    verify(passwordEncoder).encode(loginDto.getPassword());
    verify(roleRepository).findById(1L);
    verify(userRepository).save(any(User.class));
    verify(emailService).sendVerificationEmail(any(String.class), any(String.class), any(String.class));
  }
  
  @Test 
  void shouldVerifyUserAfterSignUpSuccess() throws IdInvalidException {
    VerifyUserDto verify = new VerifyUserDto(); 
    verify.setEmail(user.getEmail());
    verify.setVerificationCode("code");

    when(userService.handleGetUserByUsername(anyString())).thenReturn(user);
    when(userRepository.findByEmail(anyString())).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(mock(User.class));

    this.authService.verifyUser(verify);

    assertTrue(user.isEnabled());
    assertNull(user.getVerificationCodeExpiresAt());
    assertNull(user.getVerificationCode());

    verify(userService).handleGetUserByUsername(anyString());
    verify(userRepository).findByEmail(anyString());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void shouldThrowErrorWhenVerifyCodeExpire(){
    VerifyUserDto verify = new VerifyUserDto();
    verify.setEmail(user.getEmail());
    verify.setVerificationCode("code");

    when(userService.handleGetUserByUsername(anyString())).thenReturn(user);
    when(userRepository.findByEmail(anyString())).thenReturn(user);

    user.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(15));

    assertThrows(VerificationException.class,
            () -> this.authService.verifyUser(verify),
            "Verification code has expired");

    verify(userService).handleGetUserByUsername(anyString());
    verify(userRepository).findByEmail(anyString());
  }

  @Test
  void shouldThrowErrorWhenVerifyCodeWrong(){
    VerifyUserDto verify = new VerifyUserDto();
    verify.setEmail(user.getEmail());
    verify.setVerificationCode("code");

    when(userService.handleGetUserByUsername(anyString())).thenReturn(user);
    when(userRepository.findByEmail(anyString())).thenReturn(user);

    user.setVerificationCode("xyz");

    assertThrows(VerificationException.class,
            () -> this.authService.verifyUser(verify),
            "Invalid verification code");

    verify(userService).handleGetUserByUsername(anyString());
    verify(userRepository).findByEmail(anyString());
  }
}
