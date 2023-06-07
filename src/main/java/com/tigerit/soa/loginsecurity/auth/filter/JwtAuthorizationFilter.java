//package com.tigerit.soa.loginsecurity.auth.filter;
//
//import com.tigerit.soa.auth.exception.InvalidJwtAuthenticationException;
//import com.tigerit.soa.auth.jwt.JwtHelper;
//import com.tigerit.soa.util.Util;
//import io.jsonwebtoken.Claims;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.GenericFilterBean;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.UUID;
//
//import static com.tigerit.soa.auth.SecurityConstants.ACCESS_TOKEN;
//import static com.tigerit.soa.auth.SecurityConstants.TOKEN_HEADER;
//import static com.tigerit.soa.util.ResponseMessages.INVALID_TOKEN;
//
///**
// *
// */
//public class JwtAuthorizationFilter extends GenericFilterBean {
//
//    private static Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
//
//    private JwtHelper jwtHelper;
//
//    public JwtAuthorizationFilter(JwtHelper jwtHelper) {
//        this.jwtHelper = jwtHelper;
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException, InvalidJwtAuthenticationException {
//        String randomCode = UUID.randomUUID().toString().substring(0, 6);
//        MDC.put("random_code", randomCode);
//        try {
//            logger.debug("Calling jwtHelper to resolve access token from request");
//            String accessToken = jwtHelper.resolveToken((HttpServletRequest) request);
//            String tokenType = jwtHelper.getTokenType(accessToken);
//            if (accessToken != null && !tokenType.equalsIgnoreCase(ACCESS_TOKEN)) {
//                throw new AuthenticationServiceException(INVALID_TOKEN);
//            }
//            logger.debug("accessToken: {}, tokenType: {}", accessToken, tokenType);
//            logger.debug("Calling jwtHelper to resolve claims from request");
//            Claims claims = jwtHelper.resolveClaims((HttpServletRequest) request);
//            if (accessToken != null && claims != null
//                    && jwtHelper.validateClaims(claims) && jwtHelper.tokenNotBlackListed(accessToken)) {
//                MDC.put("logged_user", jwtHelper.getUsername(claims));
//                logger.debug("Calling getAuthentication with claims, httpRequest and token");
//                Authentication authentication = jwtHelper.getAuthentication(claims, (HttpServletRequest) request, accessToken);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        } catch (InvalidJwtAuthenticationException | AuthenticationServiceException ex) {
//            Util.createCustomResponse((HttpServletResponse) response,
//                    Util.buildErrorRestResponse(HttpStatus.UNAUTHORIZED, TOKEN_HEADER, ex.getMessage()));
//        }
//        try {
//            chain.doFilter(request, response);
//        }
//        catch (Exception e) {
//            logger.warn("Global Exception caught", e);
//            throw e;
//        } finally {
//            MDC.remove("logged_user");
//            MDC.remove("random_code");
//        }
//    }
//}
