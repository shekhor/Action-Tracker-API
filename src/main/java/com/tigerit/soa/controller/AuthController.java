package com.tigerit.soa.controller;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import com.tigerit.soa.loginsecurity.auth.exception.UserNotFoundException;
import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.loginsecurity.component.exception.ActrServiceException;
import com.tigerit.soa.loginsecurity.models.request.AuthRequest;
import com.tigerit.soa.loginsecurity.models.response.AuthResponse;
import com.tigerit.soa.loginsecurity.models.response.RestApiResponse;
import com.tigerit.soa.loginsecurity.repository.UserRepository;
import com.tigerit.soa.loginsecurity.security.jwt.JwtUtils;
import com.tigerit.soa.loginsecurity.util.ResponseMessages;
import com.tigerit.soa.loginsecurity.util.SessionKey;
import com.tigerit.soa.loginsecurity.util.Util;
import com.tigerit.soa.loginsecurity.util.core.Status;
import com.tigerit.soa.loginsecurity.util.core.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.tigerit.soa.loginsecurity.auth.SecurityConstants.*;
import static com.tigerit.soa.loginsecurity.util.ResponseMessages.INTERNAL_SERVER_ERROR;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/rest/auth")
public class AuthController {

	private Logger logger = LoggerFactory.getLogger(AuthController.class);


	//@Autowired
	//AuthenticationManager authenticationManager;

	//@Autowired
    UserRepository userRepository;


	@Autowired
	PasswordEncoder encoder;

	//@Autowired
    JwtUtils jwtUtils;

	private static final String PASSWORD="ACTR";

	private RedisTemplate<String, Object> redisTemplate;
	@Value("${security.jwt.token.access.expire}")
	private long accessTokenValidity;

	@Value("${security.jwt.token.refresh.expire}")
	private Long refreshTokenValidity;

	private final MessageSource messageSource;
	private AuthenticationManager authenticationManager;

	public AuthController(RedisTemplate<String, Object> redisTemplate,
						  MessageSource messageSource,
						  UserRepository userRepository,
						  AuthenticationManager authenticationManager,
						  JwtUtils jwtUtils){
		this.redisTemplate = redisTemplate;
		this.messageSource = messageSource;
		this.userRepository=userRepository;
		this.authenticationManager=authenticationManager;
		this.jwtUtils=jwtUtils;
	}

	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public ResponseEntity<RestApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest, Locale locale) {
		if(authRequest==null || StringUtils.isEmpty(authRequest.getUserType())){
			throw new ActrServiceException(HttpStatus.BAD_REQUEST, "auth", "auth request should not be null");
		}
		if(authRequest.getUserType().equals(UserType.SSO)){
			authRequest.setPassword(authRequest.getPassword()+PASSWORD);
		}
		logger.debug("Encoded password: "+encoder.encode(authRequest.getPassword()));
		logger.debug("Inside /api/auth/login with with username {}", authRequest.getUsername());
		RestApiResponse<AuthResponse> restApiResponse;
		authRequest.setUsername(authRequest.getUsername().trim().toLowerCase());
		try {
			String username = authRequest.getUsername();
			logger.debug("Authentication Start");
			Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));
			logger.debug("Authentication Complete");
			Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
			UserEntity user;
			List<String> methodAccessList = new ArrayList<>();
			if (optionalUser.isPresent()) {
				user = optionalUser.get();
				logger.debug("User : {}", user.toString());
				logger.debug("User Status: {}", user.getStatus().toString());
				if (user.getStatus().equals(Status.INACTIVE)) {
					logger.debug("User Status is Inactive, throwing disabled exception");
					throw new DisabledException("User is INACTIVE");
				}
				methodAccessList = userRepository.findMethodAccessListByUsername(user.getUsername());
				logger.debug("roles of username {}:"+methodAccessList.toString(),authRequest.getUsername());
			} else {
				throw new UserNotFoundException("User Not Found");
			}

			Date tokenCreateTime = new Date();

			List<String> roles = Util.methodAccessListToUniqueRoles(methodAccessList);

			String accessToken = jwtUtils.createToken(username, roles, ACCESS_TOKEN, tokenCreateTime);
			String refreshToken = jwtUtils.createToken(username, roles, REFRESH_TOKEN, tokenCreateTime);

			String wlAccessTokenKey = WHITE_LIST_ACCESS_TOKEN_PREFIX + accessToken;
			String wlRefreshTokenKey = WHITE_LIST_REFRESH_TOKEN_PREFIX + refreshToken;

			logger.debug("White list -> Access Token Key : {}", wlAccessTokenKey);
			logger.debug("White list -> Refresh Token Key : {}", wlRefreshTokenKey);

			long accTokValInMilli = TimeUnit.MINUTES.toMillis(accessTokenValidity);
			long refTokValInMilli = TimeUnit.MINUTES.toMillis(refreshTokenValidity);

			redisTemplate.opsForValue().set(wlAccessTokenKey, accessToken, accTokValInMilli, TimeUnit.MILLISECONDS);
			redisTemplate.opsForValue().set(wlRefreshTokenKey, refreshToken, refTokValInMilli, TimeUnit.MILLISECONDS);

			restApiResponse
					= Util.buildSuccessRestResponse(HttpStatus.OK, new AuthResponse(username, accessToken, refreshToken));
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		} catch (BadCredentialsException ex) {
			logger.debug("BadCredentialsException found", ex);
			restApiResponse
					= Util.buildErrorRestResponse(HttpStatus.UNAUTHORIZED,
					messageSource.getMessage("user.password", null, locale)/*"user.password"*/,
					messageSource.getMessage("error.invalid.username.password", null, locale)/*"password is invalid"*/);
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		} catch (DisabledException ex) {
			logger.debug("DisabledException found", ex);
			restApiResponse = Util.buildErrorRestResponse(HttpStatus.NOT_FOUND,
					/*messageSource.getMessage("user.username", null, locale)*/"user.username",
					/*messageSource.getMessage("error.account.disable", null, locale)*/"username invalid");
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		} catch (UserNotFoundException ex) {
			restApiResponse
					= Util.buildErrorRestResponse(HttpStatus.NOT_FOUND, "user.username",
					/*messageSource.getMessage("error.user.not.found", null, locale)*/"error.user.not.found");
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		} catch (Exception ex) {
			logger.debug("Error while login", ex);
			restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR,
					/*messageSource.getMessage("field.error", null, locale)*/"filed.error why?",
					ResponseMessages.INTERNAL_SERVER_ERROR);
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		}
	}


	//@PostMapping("/refresh")
	@RequestMapping(path = "/refresh", method = RequestMethod.POST)
	public ResponseEntity<RestApiResponse<AuthResponse>> refresh(HttpServletRequest request/*, Locale locale*/) {
		logger.debug("Inside /auth/refresh of AuthController");
		RestApiResponse<AuthResponse> restApiResponse;
		try {
			UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
			String username = userDetails.getUsername();
			Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
			UserEntity user;
			if (optionalUser.isPresent()) {
				logger.debug("User : {}", optionalUser.get().toString());
				user = optionalUser.get();
				logger.debug("User Status: {}", user.getStatus().toString());
				if (user.getStatus().equals(Status.INACTIVE)) {
					logger.debug("User Status is Inactive, throwing disabled exception");
					throw new DisabledException("User is INACTIVE");
				}
			}
			Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
			List<String> roles = new ArrayList<>(AuthorityUtils.authorityListToSet(authorities));
			Date tokenCreateTime = new Date();
			String accessToken = jwtUtils.createToken(username, roles, ACCESS_TOKEN, tokenCreateTime);
			//for redis
			String wlAccessTokenKey = WHITE_LIST_ACCESS_TOKEN_PREFIX + accessToken;
			redisTemplate.opsForValue().set(wlAccessTokenKey, accessToken, TimeUnit.MILLISECONDS.toMillis(accessTokenValidity), TimeUnit.MILLISECONDS);

			AuthResponse authResponse = new AuthResponse(username, accessToken);
			restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, authResponse);
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		} catch (DisabledException ex) {
			logger.debug("DisabledException found", ex);
			restApiResponse = Util.buildErrorRestResponse(HttpStatus.NOT_FOUND,
					/*messageSource.getMessage("user.username", null, locale)*/"user.username",
					/*messageSource.getMessage("error.account.disable", null, locale)*/"error.account.disable");
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		} catch (Exception ex) {
			logger.debug("Error while login", ex);
			restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR,
					/*messageSource.getMessage("field.error", null, locale)*/"field.error",
					INTERNAL_SERVER_ERROR);
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		}
	}
	@RequestMapping(path = "/logout", method = RequestMethod.POST)
	public ResponseEntity<RestApiResponse<String>> logout(HttpServletRequest request/*, Locale locale*/) {
		logger.debug("Inside /api/auth/logout for logout of AuthController");
		UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
		String tokenType = (String) request.getSession().getAttribute(SessionKey.TYPE_OF_TOKEN);
		logger.debug("username : {} and tokenType from session : {}", userDetails.getUsername(), tokenType);
		RestApiResponse<String> restApiResponse;
		boolean inserted = jwtUtils.insertTokenToBlackList(request, tokenType);
		if (inserted) {
			logger.debug("Logout Operation Success, Returning Response");
			restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, ResponseMessages.LOGOUT_SUCCESS);
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		} else {
			logger.debug("Logout Operation Failed, Returning Response");
			restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR,
					/*messageSource.getMessage("field.logout", null, locale)*/"field.logout", ResponseMessages.LOGOUT_FAILED);
			return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
		}
	}
}
