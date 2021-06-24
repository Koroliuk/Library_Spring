package ua.training.secutiry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import ua.training.model.entity.enums.Role;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .mvcMatchers("/admin/**").hasAuthority("ADMIN")
                    .mvcMatchers("/librarian/**").hasAuthority("LIBRARIAN")
                    .mvcMatchers("/reader/**").hasAuthority("READER")
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("login")
                    .successHandler(new SavedRequestAwareAuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                            String username = userDetails.getUsername();
                            System.out.println("The user " + username + " has logged in.");
                            System.out.println(request.getContextPath());
                            String redirectURL = request.getContextPath();
                            if (userDetails.hasRole(Role.READER)) {
                                redirectURL += "/reader/home";
                            } else if (userDetails.hasRole(Role.LIBRARIAN)) {
                                redirectURL += "/librarian/home";
                            } else if (userDetails.hasRole(Role.ADMIN)) {
                                redirectURL += "/admin/home";
                            } else {
                                response.sendError(404);
                            }
                            response.sendRedirect(redirectURL);
                        }
                    })
                    .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                    .logoutSuccessUrl("/")
                    .permitAll()
                .and()
                .anonymous();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        auth.authenticationProvider(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
