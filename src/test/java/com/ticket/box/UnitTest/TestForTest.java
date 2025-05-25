package com.ticket.box.UnitTest;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;


@DisplayName("This is just test for test")
public class TestForTest {

  @BeforeAll
  static void setUp(){
    System.out.println("This is before test");
  }

  @AfterAll
  static void endUp(){
    System.out.println("this is after test");
  }

  @Test
  public void testForTest(){
    int sum = 1 ;
    sum ++ ;

    assertEquals(2, sum);
  }

  @Test
  @Disabled("This is not implemented yet")
  @DisplayName("Disabled test")
  public void testForDisable(){
    assertEquals(2,2);
  }

  @Test
  @DisplayName("This is test for lambda")
  public void testForLambda(){
    List<Integer> arr = Arrays.asList(1,2,3);

    List<Integer> output  = arr.stream().filter(n -> n == 1).toList();

    assertTrue(output.size() == 1);

  }

  @Test
  @DisplayName("This is test for assumption")
  public void testFoAsump(){
    Random rd = new Random();

    for(int i = 0 ; i < 5; i++){
      int num =  rd.nextInt(3);
//      assumingThat(num == 2, () -> assertEquals(2,num));
      assumingThat(num != 2, () -> assertEquals(2, num, "Wrong"));
    }
  }

}
