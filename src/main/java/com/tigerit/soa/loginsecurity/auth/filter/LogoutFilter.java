//package com.tigerit.soa.loginsecurity.auth.filter;
//
//
//import com.tigerit.soa.auth.NoOpAuthManager;
//import com.tigerit.soa.auth.jwt.JwtHelper;
//import com.tigerit.soa.model.response.RestApiResponse;
//import com.tigerit.soa.model.response.ajax.SuccessDetails;
//import com.tigerit.soa.util.HttpUtils;
//import com.tigerit.soa.util.ResponseMessages;
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
//import static com.tigerit.soa.util.HttpUtils.determineTargetUrl;
//import static com.tigerit.soa.util.UrlHelper.AUTH_LOGOUT;
//
///**
// *
// */
//@Component
//public class LogoutFilter extends AbstractAuthenticationProcessingFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(LogoutFilter.class);
//
//    private final JwtHelper jwtHelper;
//
//    public LogoutFilter(JwtHelper jwtHelper) {
//        super(AUTH_LOGOUT);
//        setAuthenticationManager(new NoOpAuthManager());
//        this.jwtHelper = jwtHelper;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request,
//                                                HttpServletResponse response)
//            throws AuthenticationException, IOException {
//        logger.debug("Inside logout attempt authentication");
//        logger.debug("Calling jwtHelper to resolve refresh token from request");
//        String token = jwtHelper.resolveToken(request);
//        logger.debug("token : {}", token);
//        logger.debug("Calling jwtHelper to resolve claims from request");
//        Claims claims = jwtHelper.resolveClaims(request);
//        if (token != null && jwtHelper.validateClaims(claims) && jwtHelper.tokenNotBlackListed(token)) {
//            Authentication authentication = jwtHelper.getAuthentication(claims, request, token);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            return authentication;
//        }
//        throw new AuthenticationServiceException("INVALID_TOKEN");
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        String url = HttpUtils.determineTargetUrl(request);
//        logger.debug("permission ok; path: {}", url);
//        request.getRequestDispatcher(url).forward(request, response);
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                              AuthenticationException failed) throws IOException, ServletException {
//        logger.debug("authentication failed for target URL: {}", determineTargetUrl(request));
//        logger.debug("failed msg : {}", failed.getMessage());
//        RestApiResponse success;
//        if (failed.getMessage().equals(ResponseMessages.EXPIRED_TOKEN)) {
//            SuccessDetails successDetails = new SuccessDetails(ResponseMessages.EXPIRED_TOKEN);
//            success = new RestApiResponse(HttpStatus.OK, successDetails);
//        } else {
//            SuccessDetails successDetails = new SuccessDetails(ResponseMessages.INVALID_TOKEN);
//            success = new RestApiResponse(HttpStatus.OK, successDetails);
//        }
//        Util.createCustomResponse(response, success);
//    }
//}
