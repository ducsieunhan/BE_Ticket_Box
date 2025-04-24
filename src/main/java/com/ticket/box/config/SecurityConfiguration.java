package com.ticket.box.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.ticket.box.service.CustomOAuth2UserService;
import com.ticket.box.service.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import com.ticket.box.util.SecurityUtil;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private CustomOAuth2UserService oAuth2UserService;

  private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

  // signature
  @Value("${ticket.jwt.base64-secret}")
  private String jwtKey;

  public SecurityConfiguration(CustomAuthenticationEntryPoint customAuthenticationEntryPoint, CustomOAuth2UserService customOAuth2UserService, @Lazy OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.oAuth2UserService = customOAuth2UserService;
    this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/", "/api/v1/auth/login", "/api/v1/auth/refresh",
                "/api/v1/auth/register", "/api/v1/auth/**", "/storage/**", "/oauth/**" , "/login/oauth2/**", "/oauth2/**")
            .permitAll()
            .anyRequest().authenticated()
        )
            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/api/v1/auth/login")

                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(oAuth2UserService)
                    )
                    .successHandler(oAuth2LoginSuccessHandler)
//                    .defaultSuccessUrl("http://localhost:5173", true)

            )
        // .anyRequest().permitAll())
        .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()) // programmed to using jwt
            .authenticationEntryPoint(customAuthenticationEntryPoint)) // token for authentication for all
        .formLogin(f -> f.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthorityPrefix("");
    grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  // Decode JWT and verify user when user have request
  @Bean
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
        getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
    return token -> {
      try {
        return jwtDecoder.decode(token);
      } catch (Exception e) {
        System.out.println(">>> JWT error: " + e.getMessage());
        throw e;
      }
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
  }

  private SecretKey getSecretKey() {
    byte[] keyBytes = Base64.from(jwtKey).decode();
    return new SecretKeySpec(keyBytes, 0, keyBytes.length,
        SecurityUtil.JWT_ALGORITHM.getName());
  }

}