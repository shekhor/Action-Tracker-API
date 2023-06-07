//package com.tigerit.soa.loginsecurity.auth.filter;
//
//import com.tigerit.soa.auth.NoOpAuthManager;
//import com.tigerit.soa.auth.jwt.JwtHelper;
//import com.tigerit.soa.util.Util;
//import io.jsonwebtoken.Claims;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//import static com.tigerit.soa.auth.SecurityConstants.*;
//import static com.tigerit.soa.auth.SecurityConstants.REFRESH_TOKEN;
//import static com.tigerit.soa.auth.SecurityConstants.TOKEN_HEADER;
//import static com.tigerit.soa.util.HttpUtils.determineTargetUrl;
//import static com.tigerit.soa.util.ResponseMessages.*;
//import static com.tigerit.soa.util.ResponseMessages.INVALID_TOKEN;
//import static com.tigerit.soa.util.UrlHelper.*;
//import static com.tigerit.soa.util.UrlHelper.AUTH_REFRESH;
//
//
///**
// *
// */
//@Component
//public class JwtRefreshFilter extends AbstractAuthenticationProcessingFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtRefreshFilter.class);
//
//    private final JwtHelper jwtHelper;
//
//    public JwtRefreshFilter(JwtHelper jwtHelper) {
//        super(AUTH_REFRESH);
//        setAuthenticationManager(new NoOpAuthManager());
//        this.jwtHelper = jwtHelper;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
//        logger.debug("Calling jwtHelper to resolve token from request");
//        String refreshToken = jwtHelper.resolveToken(request);
//        String tokenType = jwtHelper.getTokenType(refreshToken);
//        if (refreshToken != null && !tokenType.equalsIgnoreCase(REFRESH_TOKEN)) {
//            throw new AuthenticationServiceException(INVALID_TOKEN);
//        }
//        logger.debug("refreshToken: {}, tokenType: {}", refreshToken, tokenType);
//        logger.debug("Calling jwtHelper to resolve claims from request");
//        Claims claims = jwtHelper.resolveClaims(request);
//        if (refreshToken != null && jwtHelper.validateClaims(claims)  && jwtHelper.tokenNotBlackListed(refreshToken)) {
//            logger.debug("Calling getAuthentication with claims, httpRequest and token");
//            Authentication authentication = jwtHelper.getAuthentication(claims, request, refreshToken);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            return authentication;
//        }
//        throw new AuthenticationServiceException(INVALID_TOKEN);
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        String url = determineTargetUrl(request);
//        logger.debug("permission ok; path: {}", url);
//        request.getRequestDispatcher(url).forward(request, response);
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                              AuthenticationException ex) throws IOException, ServletException {
//        logger.debug("authentication failed for target URL: {}", determineTargetUrl(request));
//        Util.createCustomResponse(response,
//                Util.buildErrorRestResponse(HttpStatus.UNAUTHORIZED, TOKEN_HEADER, ex.getMessage()));
//    }
//}
