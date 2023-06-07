//package com.tigerit.soa.security;
//
//import com.tigerit.soa.service.UsersService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurity extends WebSecurityConfigurerAdapter {
//    BCryptPasswordEncoder bCryptPasswordEncoder;
//    UsersService usersService;
//    Environment environment;
//
//    @Autowired
//    public WebSecurity(BCryptPasswordEncoder bCryptPasswordEncoder,UsersService usersService,Environment environment){
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//        this.usersService = usersService;
//        this.environment = environment;
//    }
//
//
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception{
//        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/api/users/**").permitAll()
////        http.authorizeRequests()
////                .antMatchers(HttpMethod.POST, environment.getProperty("api.registration.url.path")).permitAll()
////                .antMatchers(HttpMethod.POST, environment.getProperty("api.login.url.path")).permitAll()
////                .anyRequest().authenticated()
//         .and()
//         .addFilter(getAuthenticationFilter());
//         http.headers().frameOptions().disable();
//    }
//
//
////    @Override
////    protected void configure(HttpSecurity http) throws Exception {
////        http.csrf().disable();
////        http.authorizeRequests().antMatchers("/**").hasIpAddress(environment.getProperty("gateway.ip"))
////                .and().addFilter(getAuthenticationFilter());
////        http.headers().frameOptions().disable();
////    }
//
//
//
//    private AuthenticationFilter getAuthenticationFilter() throws Exception {
//        AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService,environment,authenticationManager());
//        authenticationFilter.setFilterProcessesUrl(environment.getProperty("api.login.url.path"));
//        return authenticationFilter;
//    }
//
//
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
//    }
//
//}
