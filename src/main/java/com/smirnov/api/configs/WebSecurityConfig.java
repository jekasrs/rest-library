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
                .csrf()
                    .disable()
                .httpBasic()
                .and()
                .authorizeRequests()

                .antMatchers(HttpMethod.POST,"/clients/").permitAll()
                .antMatchers(HttpMethod.POST, "/clients/reg_admin").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/clients/**").hasAnyRole("CLIENT", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/clients/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/clients/full_info/**").hasAnyRole("CLIENT", "ADMIN")
                .antMatchers(HttpMethod.GET, "/clients/**").permitAll()

                .antMatchers(HttpMethod.GET, "/type-books/**").permitAll()
                .antMatchers(HttpMethod.POST, "/type-books/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/type-books/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/type-books/**").hasRole("ADMIN")

                .antMatchers(HttpMethod.GET, "/books/**").permitAll()
                .antMatchers(HttpMethod.POST, "/books/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

                .antMatchers(HttpMethod.GET, "/journal/**").hasAnyRole("ADMIN", "CLIENT")
                .antMatchers(HttpMethod.POST, "/journal/**").hasAnyRole("ADMIN", "CLIENT")
                .antMatchers(HttpMethod.PUT, "/books/**").hasAnyRole("ADMIN", "CLIENT")
                .antMatchers(HttpMethod.DELETE, "/books/**").hasAnyRole("ADMIN", "CLIENT")

                .anyRequest()
                    .authenticated()
                .and()
                .formLogin()
                    .disable()
                .logout()
                    .permitAll();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(clientService).passwordEncoder(bCryptPasswordEncoder());
    }
}
