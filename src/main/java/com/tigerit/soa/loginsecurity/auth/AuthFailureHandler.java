//package com.tigerit.soa.loginsecurity.auth;
//
//import com.tigerit.soa.model.response.RestApiResponse;
//import com.tigerit.soa.model.response.ajax.ErrorDetails;
//import com.tigerit.soa.util.HttpUtils;
//import com.tigerit.soa.util.Util;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.web.access.AccessDeniedHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// *
// */
//public class AuthFailureHandler implements AccessDeniedHandler {
//
//    private Logger logger = LoggerFactory.getLogger(AuthFailureHandler.class);
//
//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response,
//                       AccessDeniedException ex) throws IOException, ServletException {
//        logger.debug("authentication failed for target URL: {}", HttpUtils.determineTargetUrl(request));
//        logger.debug("Inside AuthFailureHandler handle, creating custom error response");
//        ErrorDetails errorDetails = new ErrorDetails(SecurityConstants.TOKEN_HEADER, ex.getMessage());
//        RestApiResponse error = new RestApiResponse(HttpStatus.UNAUTHORIZED, errorDetails);
//        Util.createCustomResponse(response, error);
//    }
//}
