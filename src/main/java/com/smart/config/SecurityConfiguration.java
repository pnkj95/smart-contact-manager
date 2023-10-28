//There are 4 methods used for login process i.e. loginPage(),loginProcessingUrl(),defaultSuccessUrl & failureUrl()

package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManagerBuilder auth) throws Exception {
     
		auth.authenticationProvider(authenticationProvider());
        http
        .csrf().disable()
        .authorizeHttpRequests()
    			.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
    			.requestMatchers("/user/**").hasAuthority("ROLE_USER")
    			.requestMatchers("/**").permitAll()
    			.anyRequest().authenticated()
    			.and().formLogin().loginPage("/sign-in")
    			.loginProcessingUrl("/doLogin")
    			.defaultSuccessUrl("/user/index")
    			//.failureUrl("/login-fail")
    			;
    			
    		
        return http.build();
    }
}
