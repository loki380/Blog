package pl.olszewski.Blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.olszewski.Blog.service.Menagers.AuthorManager;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthorManager authorManager;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(authorManager);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()
                .authorizeRequests().antMatchers(
                "/registration**",
                "/css/**",
                "/login**",
                "/",
                "/post",
                "/post/csv",
                "/comment/**",
//                "/api/**",
                "/post/tags/*").permitAll()
                .regexMatchers("/post/(\\d+)").permitAll()
                .antMatchers("/post/new").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                .anyRequest().authenticated()
                .and()
                .headers().frameOptions().sameOrigin().httpStrictTransportSecurity().disable().and()
                .formLogin()
                .defaultSuccessUrl("/", true)
                .loginPage("/login")
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll();
    }
}
