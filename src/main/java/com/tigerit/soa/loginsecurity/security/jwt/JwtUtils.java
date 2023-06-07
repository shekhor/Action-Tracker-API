package com.tigerit.soa.loginsecurity.security.jwt;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.tigerit.soa.loginsecurity.auth.SecurityConstants;
import com.tigerit.soa.loginsecurity.auth.exception.InvalidJwtAuthenticationException;
import com.tigerit.soa.loginsecurity.models.ActrUserDetails;
//import com.tigerit.soa.loginsecurity.security.services.UserDetailsImpl;
//import com.tigerit.soa.loginsecurity.util.Util;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import static com.tigerit.soa.loginsecurity.auth.SecurityConstants.*;
import static com.tigerit.soa.loginsecurity.util.ResponseMessages.*;
import static com.tigerit.soa.loginsecurity.util.SessionKey.TYPE_OF_TOKEN;
import static com.tigerit.soa.loginsecurity.util.SessionKey.USER_DETAILS;


@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${bezkoder.app.jwtSecret}")
	private String jwtSecret;

	@Value("${bezkoder.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Value("${security.jwt.token.access.expire}")
	private long accessTokenValidity;

	@Value("${security.jwt.token.refresh.expire}")
	private Long refreshTokenValidity;

	private JwtParser jwtParser;
	private Key key;

	@Value("${security.jwt.token.secret-key}")
	private String signingKey;

	//private final UserRepository userRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	public JwtUtils(/*UserRepository userRepository,*/ RedisTemplate redisTemplate) {
		//this.userRepository = userRepository;
		this.redisTemplate = redisTemplate;
	}

	@PostConstruct
	protected void init() {
		this.key = Keys.hmacShaKeyFor(signingKey.getBytes());
		this.jwtParser = Jwts.parser().setSigningKey(key);
	}

	/*public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}*/

	public String createToken(String username, List<String> roles, String tokenType, Date tokenCreateTime) {
		logger.debug("Token create request for username : {}, tokenType : {}, tokenCreateTime : {}",
				username, tokenType, tokenCreateTime);
		Claims claims = Jwts.claims().setSubject(username);
		Date tokenValidity;

		if (tokenType.equals(SecurityConstants.ACCESS_TOKEN)) {
			claims.put("roles", roles);
			logger.debug("roles : {}", roles);
			tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
		} else {
			logger.debug("roles : {}", roles);
			tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(refreshTokenValidity));
		}

		return Jwts.builder()
				.setClaims(claims)
				.setHeaderParam(TOKEN_TYPE, tokenType)
				.setIssuedAt(tokenCreateTime)
				.setExpiration(tokenValidity)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	public String resolveToken(HttpServletRequest request) {
		logger.debug("Resolving jwt token from request");
		String bearerToken = request.getHeader(TOKEN_HEADER);
		if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
			logger.debug("Bearer token found in header, returning token");
			return bearerToken.substring(TOKEN_PREFIX.length());
		}
		logger.debug("Bearer token NOT found in header, returning token : null");
		return null;
	}

	public String getTokenType(String token) {
		logger.debug("Parsing token type from token : {}", token);
		try {
			if (token != null) {
				return (String) jwtParser.parse(token).getHeader().get(TOKEN_TYPE);
			}
			logger.debug("Token is Null, returning TYPE : null");
			return null;
		} catch (ExpiredJwtException ex) {
			logger.debug("Could not parse jwt claims, Token Expired ", ex);
			throw new InvalidJwtAuthenticationException(EXPIRED_TOKEN, ex);
		} catch (Exception ex) {
			logger.debug("Could not parse jwt claims, Token Invalid ", ex);
			throw new InvalidJwtAuthenticationException(INVALID_TOKEN, ex);
		}
	}
	public Claims resolveClaims(HttpServletRequest req) {
		try {
			logger.debug("Trying to resolve claims token ");
			String bearerToken = req.getHeader(TOKEN_HEADER);
			if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
				return parseJwtClaims(bearerToken.substring(TOKEN_PREFIX.length()));
			}
			return null;
		} catch (ExpiredJwtException ex) {
			logger.debug("Could not parse jwt claims, Token Expired ", ex);
			req.setAttribute(EXPIRED, ex.getMessage());
			throw new InvalidJwtAuthenticationException(EXPIRED_TOKEN, ex);
		} catch (Exception ex) {
			logger.debug("Could not parse jwt claims, Token Invalid ", ex);
			req.setAttribute(INVALID, ex.getMessage());
			throw new InvalidJwtAuthenticationException(INVALID_TOKEN, ex);
		}
	}
	private Claims parseJwtClaims(String token) {
		return jwtParser.parseClaimsJws(token).getBody();
	}
	public boolean validateClaims(Claims claims) throws InvalidJwtAuthenticationException {
		try {
			logger.debug("Validating jwt token Claims");
			return claims.getExpiration().after(new Date());
		} catch (Exception e) {
			logger.debug("Exception while parsing Claims");
			throw new InvalidJwtAuthenticationException("Expired or invalid JWT token", e);
		}
	}
	public String getUsername(Claims claims) {
		return claims.getSubject();
	}
	public Authentication getAuthentication(Claims claims, HttpServletRequest request, String token) {
		logger.debug("Authentication request of token received");
		String username = getUsername(claims);
		String tokenType = getTokenType(token);
		List<String> roles;
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		if (tokenType.equals(ACCESS_TOKEN)) {
			roles = getRoles(claims);
			logger.debug("tokenType : access_token, username : {}, -> roles : {}", username, roles);
			roles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
		} else {
			//roles = Util.methodAccessListToUniqueRoles(userRepository.findMethodAccessListByUsername(username));
			roles=getRoles(claims);
			logger.debug("tokenType : refresh_token, Username : {}, -> roles : {}", username, roles);
			roles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
		}
		UserDetails userDetails = new ActrUserDetails(username, null, roles);
		request.getSession().setAttribute(USER_DETAILS, userDetails);
		request.getSession().setAttribute(TYPE_OF_TOKEN, tokenType);
		return new UsernamePasswordAuthenticationToken(userDetails, "", grantedAuthorities);
	}
	private List<String> getRoles(Claims claims) {
		return (List<String>) claims.get("roles");
	}

	public boolean insertTokenToBlackList(HttpServletRequest request, String tokenType) {
		try {
			if (tokenType.equals(ACCESS_TOKEN)) {
				String accessToken = resolveToken(request);
				String blAccessTokenKey = SecurityConstants.BLACK_LIST_ACCESS_TOKEN_PREFIX + accessToken;
				//this is for temporary
				//Util.redisTemplate.add(accessToken);
				//Util.printBlackList();

				redisTemplate.opsForValue().set(blAccessTokenKey, accessToken,
						TimeUnit.MINUTES.toMillis(accessTokenValidity), TimeUnit.MILLISECONDS);
				return true;
			} else {
				String refreshToken = resolveToken(request);
				String blRefreshTokenKey = SecurityConstants.BLACK_LIST_REFRESH_TOKEN_PREFIX + refreshToken;
				//this is for temporary
				//Util.redisTemplate.add(refreshToken);
				//Util.printBlackList();

				redisTemplate.opsForValue().set(blRefreshTokenKey, refreshToken,
						TimeUnit.MINUTES.toMillis(refreshTokenValidity), TimeUnit.MILLISECONDS);
				return true;
			}
		} catch (Exception ex) {
			logger.debug("Error While Insert JWT Toke To BlackList", ex);
			return false;
		}
	}
	public boolean tokenNotBlackListed(String token) {
		logger.debug("Checking if jwt token already in blacklist");
		String key = getBlackListKeyByToken(token);
		String value = (String) redisTemplate.opsForValue().get(key);
		//this is for temporary
		//boolean value = Util.redisTemplate.contains(token);
		if (value == null/*value==false*/) {
			logger.debug("Jwt token already in BLACK LIST : FALSE");
			return true;
		} else {
			logger.debug("Jwt token already in BLACK LIST : TRUE");
			return false;
		}
	}
	private String getBlackListKeyByToken(String token) {
		if (getTokenType(token).equals(ACCESS_TOKEN)) {
			return BLACK_LIST_ACCESS_TOKEN_PREFIX + token;
		} else {
			return BLACK_LIST_REFRESH_TOKEN_PREFIX + token;
		}
	}
	/*public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}*/
}
