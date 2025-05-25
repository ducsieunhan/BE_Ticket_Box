package com.ticket.box.UnitTest.Service;

import com.ticket.box.domain.Role;
import com.ticket.box.domain.User;
import com.ticket.box.domain.request.ReqUserDTO;
import com.ticket.box.repository.RoleRepository;
import com.ticket.box.repository.UserRepository;
import com.ticket.box.service.UserService;
import com.ticket.box.util.error.DataInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private UserService userService;

  private ReqUserDTO userDto ;
  private User user ;
  private Role adminRole ;


  @BeforeEach
  public void setUp(){
    adminRole = new Role();
    adminRole.setId(1L);
    adminRole.setName("ADMIN");
    userDto = ReqUserDTO.builder()
            .email("john.doe@example.com")
            .name("John Doe")
            .password("securePassword123")
            .phone("0987654321")
            .dob("1990-01-15")
            .province("Hanoi")
            .district("Cau Giay")
            .ward("Dich Vong Hau")
            .houseNumber("123 Main St")
            .role(1L)
            .build();

    user = new User();
    user.setId(1L);
    user.setEmail(userDto.getEmail());
    user.setName(userDto.getName());
    user.setPassword(userDto.getPassword());
    user.setRole(adminRole);
    user.setEnabled(true);



  }

  @Test
  @DisplayName("Test save user")
  public void when_save_user() throws DataInvalidException {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));
    when(userRepository.save(any(User.class))).thenReturn(user);

    User userTest = this.userService.createNewUser(userDto);
    assertNotNull(userTest);
    assertEquals(userTest.getId(), user.getId());
    assertTrue(userTest.isEnabled());
    assertSame(userTest.getRole().getName(), adminRole.getName());
  }

  @Test
  @DisplayName("Test get all users")
  public void when_get_all_users() {
    User newUser = new User();
    user.setId(2L);
    user.setEmail("newEmail");
    user.setName(userDto.getName());
    user.setPassword(userDto.getPassword());
    user.setRole(adminRole);
    user.setEnabled(true);

    when(userRepository.findAll()).thenReturn(List.of(user, newUser));
    assertNotNull(this.userService.getAllUsers());
    assertEquals(this.userService.getAllUsers().size(), 2);
  }

  @Test
  @DisplayName("Test get by id and delete by id")
  public void when_get_delete_by_id(){
    doNothing().when(userRepository).deleteById(any(Long.class));
    when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
    if(this.userService.getUserById(1L).isPresent()){
      this.userService.deleteUserById(1L);
    }
    verify(userRepository, times(1)).deleteById(1L);
  }

  @Test
  @DisplayName("Test update user")
  public void when_update_user() throws DataInvalidException {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));
    when(userRepository.save(any(User.class))).thenReturn(user);
    User userTest = this.userService.createNewUser(userDto);

    userDto.setName("Ducsieunhan");
    userDto.setEmail("ducsieunhan@gmail.com");

    when(userRepository.findByEmail(userDto.getEmail())).thenReturn(user);
    User updateUser = this.userService.updateUser(userDto);
    assertSame(updateUser.getName(), userDto.getName());
    assertSame(updateUser.getEmail(), userDto.getEmail());

  }

}
