package com.ticket.box.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping("/")
  public String getHelloWorld(){
    return "xin chao xin chao xin chao";
  }
}
