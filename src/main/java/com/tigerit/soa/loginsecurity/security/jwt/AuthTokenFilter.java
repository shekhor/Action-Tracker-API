package com.tigerit.soa.loginsecurity.security.jwt;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tigerit.soa.loginsecurity.auth.exception.InvalidJwtAuthenticationException;
//import com.tigerit.soa.loginsecurity.security.services.UserDetailsServiceImpl;
import com.tigerit.soa.loginsecurity.util.Util;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.tigerit.soa.loginsecurity.auth.SecurityConstants.ACCESS_TOKEN;
import static com.tigerit.soa.loginsecurity.auth.SecurityConstants.TOKEN_HEADER;
import static com.tigerit.soa.loginsecurity.util.ResponseMessages.INVALID_TOKEN;


public class AuthTokenFilter extends GenericFilterBean /*OncePerRequestFilter*/ {
	@Autowired
	private JwtUtils jwtUtils;

	//@Autowired
	//private UserDetailsServiceImpl userDetailsService;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException, InvalidJwtAuthenticationException {
		String randomCode = UUID.randomUUID().toString().substring(0, 6);
		MDC.put("random_code", randomCode);
		try {
			logger.debug("Calling jwtHelper to resolve access token from request");
			String accessToken = jwtUtils.resolveToken((HttpServletRequest) request);
			String tokenType = jwtUtils.getTokenType(accessToken);
			if (accessToken != null && !tokenType.equalsIgnoreCase(ACCESS_TOKEN)) {
				throw new AuthenticationServiceException(INVALID_TOKEN);
			}
			logger.debug("accessToken: {}, tokenType: {}", accessToken, tokenType);
			logger.debug("Calling jwtHelper to resolve claims from request");
			Claims claims = jwtUtils.resolveClaims((HttpServletRequest) request);
			if (accessToken != null && claims != null
					&& jwtUtils.validateClaims(claims) && jwtUtils.tokenNotBlackListed(accessToken)) {
				MDC.put("logged_user", jwtUtils.getUsername(claims));
				logger.debug("Calling getAuthentication with claims, httpRequest and token");
				Authentication authentication = jwtUtils.getAuthentication(claims, (HttpServletRequest) request, accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (InvalidJwtAuthenticationException | AuthenticationServiceException ex) {
			Util.createCustomResponse((HttpServletResponse) response,
					Util.buildErrorRestResponse(HttpStatus.UNAUTHORIZED, TOKEN_HEADER, ex.getMessage()));
		}
		try {
			chain.doFilter(request, response);
		}
		catch (Exception e) {
			logger.warn("Global Exception caught", e);
			throw e;
		} finally {
			MDC.remove("logged_user");
			MDC.remove("random_code");
		}
	}

	/*@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}*/
}
