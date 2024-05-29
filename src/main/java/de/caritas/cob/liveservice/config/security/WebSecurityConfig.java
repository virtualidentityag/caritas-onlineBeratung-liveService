package de.caritas.cob.liveservice.config.security;

import de.caritas.cob.liveservice.api.auth.AuthorisationService;
import de.caritas.cob.liveservice.api.auth.JwtAuthConverter;
import de.caritas.cob.liveservice.api.config.SpringFoxConfig;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

/**
 * Configuration class to provide the keycloak security configuration.
 */
@KeycloakConfiguration
public class WebSecurityConfig {

  @Autowired
  AuthorisationService authorisationService;
  @Autowired
  JwtAuthConverterProperties jwtAuthConverterProperties;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity
        .csrf().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
        .and()
        .authorizeRequests()
        .requestMatchers(SpringFoxConfig.WHITE_LIST).permitAll()
        .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/live"))).permitAll()
        .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/live/**")))
        .permitAll();

    httpSecurity.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthConverter());
    return httpSecurity.build();
  }

  @Bean
  public JwtAuthConverter jwtAuthConverter() {
    return new JwtAuthConverter(jwtAuthConverterProperties, authorisationService);
  }


  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new NullAuthenticatedSessionStrategy();
  }

  /**
   * Provides the keycloak configuration resolver bean.
   *
   * @return the configured {@link KeycloakConfigResolver}
   */
  @Bean
  public KeycloakConfigResolver keycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  @Bean
  @ConfigurationProperties(prefix = "keycloak", ignoreUnknownFields = false)
  public KeycloakSpringBootProperties keycloakSpringBootProperties() {
    return new KeycloakSpringBootProperties();
  }

}
