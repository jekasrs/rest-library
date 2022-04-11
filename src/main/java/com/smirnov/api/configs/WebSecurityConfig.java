package com.smirnov.api.configs;

import com.smirnov.api.services.ClientService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClientService clientService;

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .httpBasic()
                .and()
                .authorizeRequests()

                .antMatchers("/resources/static/**").permitAll()

                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/client/").permitAll()
                .antMatchers(HttpMethod.POST, "/api/client/").permitAll()

                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll()
                .and()
                .logout().permitAll();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(clientService).passwordEncoder(bCryptPasswordEncoder());
    }
}
