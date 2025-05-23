package com.ticket.box.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
  private String accessToken;
  private UserLogin user;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserLogin {
    private long id;
    private String email;
    private String name;
    private String role ;
  }

}
