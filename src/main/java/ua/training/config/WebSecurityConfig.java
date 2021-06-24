package ua.training.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ua.training.model.entity.enums.Role;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .mvcMatchers("/reader/**").hasRole(Role.READER.name())
                    .mvcMatchers("/librarian/**").hasRole(Role.LIBRARIAN.name())
                    .mvcMatchers("/admin/**").hasRole(Role.ADMIN.name())
                .and()
                .formLogin()
                    .permitAll()
                .and()
                .anonymous();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("reader").password("{noop}1").roles(Role.READER.name());
        auth.inMemoryAuthentication().withUser("librarian").password("{noop}1").roles(Role.LIBRARIAN.name());
        auth.inMemoryAuthentication().withUser("admin").password("{noop}1").roles(Role.ADMIN.name());
    }
}
