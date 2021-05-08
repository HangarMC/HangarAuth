package io.papermc.hangarauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import io.papermc.hangarauth.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Disable csrf // TODO is this really right?
                .csrf().disable()

                .formLogin()
                    .loginPage("/account/login")
                    .loginProcessingUrl("/api/login/")
                    .failureUrl("/account/login?error=true")
                    .successForwardUrl("/")

                .and().logout()

                .and().authorizeRequests()
                    .antMatchers("/", "/account/settings").fullyAuthenticated()
                    .antMatchers("/account/reset", "/account/login", "/account/signup", "/account/logout/success").anonymous()
                    .antMatchers("/_nuxt/**").anonymous()
                    .anyRequest().authenticated()
                ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder();
        encoder.setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        return encoder;
    }
}
