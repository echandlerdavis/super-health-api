package io.catalyte.training.superhealth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * Disables the login page for Spring boot and cross-referencing
   */
  @Override
  protected void configure(HttpSecurity security) throws Exception {
    security.httpBasic().disable();
    security.csrf().disable();
  }

}
