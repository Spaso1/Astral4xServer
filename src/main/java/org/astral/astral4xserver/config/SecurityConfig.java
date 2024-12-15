package org.astral.astral4xserver.config;

import org.astral.astral4xserver.dao.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/users/register").permitAll()  // 放行注册页面
                .antMatchers("/api/users/auth/**").permitAll()  // 放行邮箱验证页面
                .antMatchers("/api/users/login").permitAll()           // 放行登录页面
                .antMatchers("/admin/**").hasRole("ADMIN")   // 限制 /admin/** 路径只能由 ADMIN 角色访问
                .antMatchers("/api/client/**").permitAll()//客户端交互
                .antMatchers("/**").permitAll() // 开发时用
                //.antMatchers("/swagger-ui/index.html", "/v2/api-docs", "/webjars/**", "/swagger-resources/**").hasRole("ADMIN")//正式使用
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")  // 限制 /user/** 路径只能由 USER 或 ADMIN 角色访问
                .anyRequest().authenticated()                // 其他所有请求都需要认证
                .and()
                .formLogin()
                .loginPage("/api/users/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403");;
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
