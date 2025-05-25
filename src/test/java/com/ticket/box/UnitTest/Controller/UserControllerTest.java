package com.ticket.box.UnitTest.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.box.controller.UserController;
import com.ticket.box.domain.User;
import com.ticket.box.domain.request.ReqUserDTO;
import com.ticket.box.domain.response.ResUserDTO;
import com.ticket.box.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ObjectMapper objectMapper;

  private ReqUserDTO reqUserDTO;
  private User currentUser;
  private ResUserDTO resUserDTO;

  @BeforeEach
  @DisplayName("...")
  public void setUp(){
    reqUserDTO = ReqUserDTO.builder()
            .name("ducsieunhan")
            .email("ducsieunhan@gmail.com")
            .password("abcabc")
            .phone("0342696978")
            .role(1L)
            .build();

    currentUser = new User();
    currentUser.setId(1L);
    currentUser.setEmail(reqUserDTO.getEmail());
    currentUser.setName(reqUserDTO.getName());
    currentUser.setPassword(reqUserDTO.getPassword());

    resUserDTO = new ResUserDTO();
    resUserDTO.setId(1L);
    resUserDTO.setEmail(currentUser.getEmail());
    resUserDTO.setName(currentUser.getName());
    resUserDTO.setPassword(currentUser.getPassword());
  }

  @Test
  @DisplayName("Test create new user")
  public void createUserApi() throws Exception {
    when(passwordEncoder.encode(any(String.class))).thenReturn("12345");
    when(this.userService.createNewUser(any(ReqUserDTO.class))).thenReturn(currentUser);
    when(this.userService.convertResUserDTO(any(User.class))).thenReturn(resUserDTO);

    mvc.perform(MockMvcRequestBuilders
            .post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reqUserDTO))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("ducsieunhan"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("ducsieunhan@gmail.com"));
    verify(passwordEncoder, times(1)).encode(any(String.class));
    verify(userService, times(1)).createNewUser(any(ReqUserDTO.class));
    verify(userService, times(1)).convertResUserDTO(any(User.class));
  }

  @Test
  @DisplayName("Test get all users")
  public void getUserListApi() throws Exception {
    when(this.userService.getAllUsers()).thenReturn(List.of(currentUser, currentUser));
    when(this.userService.convertResUserDTO(any(User.class))).thenReturn(resUserDTO);

    mvc.perform(MockMvcRequestBuilders
                    .get("/api/v1/users")
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].name").value("ducsieunhan")) ;

    verify(userService, times(1)).getAllUsers();
    verify(userService, times(2)).convertResUserDTO(any(User.class));
  }

  @Test
  @DisplayName("Test get users by id")
  public void getUserByIdApi() throws Exception {
    when(this.userService.getUserById(any(Long.class))).thenReturn(Optional.ofNullable(currentUser));
    when(this.userService.convertResUserDTO(any(User.class))).thenReturn(resUserDTO);

    mvc.perform(MockMvcRequestBuilders
                    .get("/api/v1/users/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("ducsieunhan"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L)) ;

    verify(userService, times(1)).getUserById(any(Long.class));
    verify(userService, times(1)).convertResUserDTO(any(User.class));
  }

  @Test
  @DisplayName("Test delete users by id")
  public void deleteUserByIdApi() throws Exception {
    doNothing().when(this.userService).deleteUserById(any(Long.class));

    mvc.perform(MockMvcRequestBuilders
                    .delete("/api/v1/users/delete/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

    verify(userService, times(1)).deleteUserById(any(Long.class));
  }

  @Test
  @DisplayName("Test update new user")
  public void updateUserApi() throws Exception {

    reqUserDTO.setEmail("udpate@gmail.com");
    reqUserDTO.setName("update");

    currentUser.setEmail("udpate@gmail.com");
    currentUser.setName("update");

    resUserDTO.setEmail("udpate@gmail.com");
    resUserDTO.setName("update");

    when(this.userService.updateUser(any(ReqUserDTO.class))).thenReturn(currentUser);
    when(this.userService.convertResUserDTO(any(User.class))).thenReturn(resUserDTO);


    mvc.perform(MockMvcRequestBuilders
                    .put("/api/v1/users/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reqUserDTO))
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("update"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("udpate@gmail.com"));
    verify(userService, times(1)).updateUser(any(ReqUserDTO.class));
    verify(userService, times(1)).convertResUserDTO(any(User.class));
  }

}
