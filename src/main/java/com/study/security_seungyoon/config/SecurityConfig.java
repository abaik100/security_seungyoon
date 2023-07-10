package com.study.security_seungyoon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CorsFilter;

import com.study.security_seungyoon.config.auth.AuthFailureHandler;
import com.study.security_seungyoon.service.auth.PrincipalOauth2UserService;

import lombok.RequiredArgsConstructor;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
   
   private final CorsFilter corsfilter;
   
   private final PrincipalOauth2UserService princialOauth2UserService;
   
   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();            //암호화를 해준다       
   }
   
   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable(); //csrf공격을 막는다
      
      http.headers()
         .frameOptions()
         .disable();
      http.addFilter(corsfilter);
      
      http.authorizeRequests()
         .antMatchers("/api/v1/grant/test/user/**")
         .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
         
         .antMatchers("/api/v1/grant/test/manager/**")
         .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
         
         .antMatchers("/api/v1/grant/test/admin/**")
         .access("hasRole('ROLE_ADMIN')")
         
         .antMatchers("/notice/addition","/notice/modification/**")   
         .hasRole("ADMIN")      
         
         .antMatchers("/", "/index","/mypage/**")   // 1) 우리가 지정한 요청
         .authenticated()                     // 2) 인증을 거쳐라
         
         .anyRequest()                        // 3) 다른 모든 요청들은
         .permitAll()                        // 4) 모두 접근 권한을 부여하겠다.
         .and()                              // 5) 로그인 방식은 form 로그인을 사용하겠다.
         .formLogin()                        // 6) 로그인 페이지는 해당 get 요청을 통해 접근하겠다.
         .loginPage("/auth/signin")               // 7) 로그인 요청(post요청)
         .loginProcessingUrl("/auth/signin")
         .failureHandler(new AuthFailureHandler())
         
         .and()
         .oauth2Login()
         .userInfoEndpoint()
         .userService(princialOauth2UserService)
         .and()
         .defaultSuccessUrl("/index");
   }
}