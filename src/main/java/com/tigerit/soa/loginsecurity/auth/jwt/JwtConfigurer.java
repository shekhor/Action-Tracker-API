//package com.tigerit.soa.loginsecurity.auth.jwt;
//
////import com.tigerit.soa.auth.filter.JwtAuthorizationFilter;
//import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.DefaultSecurityFilterChain;
////import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///**
// *
// */
//public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
//
//    private JwtHelper jwtHelper;
//
//    public JwtConfigurer(JwtHelper jwtHelper) {
//        this.jwtHelper = jwtHelper;
//    }
//
//    @Override
//    public void configure(HttpSecurity http) {
//       // JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(jwtHelper);
//        //http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
//    }
//}