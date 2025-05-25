package com.ticket.box.UnitTest.Repository;

import com.ticket.box.domain.User;
import com.ticket.box.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("This is test class for User's repository")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  public UserRepository userRepository;

  private User newUser ;

  @Test
  public void findAll_return_list_when_found(){
    List<User> users = this.userRepository.findAll();
    assertNotNull(users);
    List<String> names =  users.stream().map(User::getName).toList();
    System.out.println(names);
  }

  @Test
  public void find_users_not_enabled_return_when_found(){
    List<User> users = this.userRepository.findAll();
    assertNotNull(users);
    List<User> disabledUsers = users.stream().filter(u -> !u.isEnabled()).toList();
    assertNotNull(disabledUsers);
    for (User u : disabledUsers){
      System.out.print("User id:" + u.getId());
      System.out.println(", email: " + u.getEmail());
    }
  }

}
