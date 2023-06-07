//package com.tigerit.soa.loginsecurity.auth.jwt;
//
//
//import com.tigerit.soa.auth.SecurityConstants;
//import com.tigerit.soa.auth.exception.InvalidJwtAuthenticationException;
//import com.tigerit.soa.repository.UsersRepository;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.servlet.http.HttpServletRequest;
//import java.security.Key;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static com.tigerit.soa.auth.SecurityConstants.*;
//import static com.tigerit.soa.util.ResponseMessages.*;
//import static com.tigerit.soa.util.SessionKey.TYPE_OF_TOKEN;
//import static com.tigerit.soa.util.SessionKey.USER_DETAILS;
//
///**
// *
// */
//@Component
//public class JwtHelper {
//
//    //@Loggable
//    //private static Logger logger;
//    private Logger logger = LoggerFactory.getLogger(JwtHelper.class);
//
//
//    @Value("${security.jwt.token.secret-key}")
//    private String signingKey;
//
//    @Value("${security.jwt.token.access.expire}")
//    private long accessTokenValidity;
//
//    //@Value("${security.jwt.token.afis.expire}")
//    //private long afisTokenValidity;
//
//    @Value("${security.jwt.token.refresh.expire}")
//    private Long refreshTokenValidity;
//
//    //@Value("${private.key.cert.path}")
//    //private String privateKeyFilePath;
//
//    //@Value("${public.key.cert.path}")
//    //private String publicKeyFilePath;
//
//    private JwtParser jwtParser;
//    private Key key;
//    private final UsersRepository userRepository;
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public JwtHelper(UsersRepository userRepository, RedisTemplate redisTemplate) {
//        this.userRepository = userRepository;
//        this.redisTemplate = redisTemplate;
//    }
//
//    @PostConstruct
//    protected void init() {
//        this.key = Keys.hmacShaKeyFor(signingKey.getBytes());
//        this.jwtParser = Jwts.parser().setSigningKey(key);
//    }
//
//    public String createToken(String username, List<String> roles, String tokenType, Date tokenCreateTime) {
//        logger.debug("Token create request for username : {}, tokenType : {}, tokenCreateTime : {}",
//                username, tokenType, tokenCreateTime);
//        Claims claims = Jwts.claims().setSubject(username);
//        Date tokenValidity;
//
//        if (tokenType.equals(SecurityConstants.ACCESS_TOKEN)) {
//            claims.put("roles", roles);
//            logger.debug("roles : {}", roles);
//            tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
//        } else {
//            logger.debug("roles : {}", roles);
//            tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(refreshTokenValidity));
//        }
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setHeaderParam(TOKEN_TYPE, tokenType)
//                .setIssuedAt(tokenCreateTime)
//                .setExpiration(tokenValidity)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    /*public String createAfisToken(String username, String voterId, String tokenType,
//                                  Date tokenCreateTime, boolean isEncrypted, String uuid)
//            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
//        logger.debug("Afis Token create request for username : {}, tokenType : {}, tokenCreateTime : {}",
//                username, tokenType, tokenCreateTime);
//        Claims claims = Jwts.claims().setSubject(username);
//        claims.setId(voterId);
//        claims.put("user_name", username);
//        claims.put("encrypted_request", isEncrypted);
//        claims.put("uuid", uuid);
//        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(afisTokenValidity));
//        return Jwts.builder()
//                .setId(UUID.randomUUID().toString())
//                .setClaims(claims)
//                .setHeaderParam(TOKEN_TYPE, tokenType)
//                .setIssuedAt(tokenCreateTime)
//                .setExpiration(tokenValidity)
//                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
//                .compact();
//    }*/
//
//    /*private Key getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        File resource = new ClassPathResource(privateKeyFilePath).getFile();
//        byte[] keyBytes = Files.readAllBytes(resource.toPath());
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PrivateKey privateKey = kf.generatePrivate(spec);
//        logger.debug("private KEY Format : {} ,  Algo {},  key {}", privateKey.getFormat(), privateKey.getAlgorithm(), privateKey.getEncoded());
//
//        return privateKey;
//    }*/
//
//    public Authentication getAuthentication(Claims claims, HttpServletRequest request, String token) {
//        logger.debug("Authentication request of token received");
//        String username = getUsername(claims);
//        String tokenType = getTokenType(token);
//        List<String> roles;
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        if (tokenType.equals(ACCESS_TOKEN)) {
//            roles = getRoles(claims);
//            logger.debug("tokenType : access_token, username : {}, -> roles : {}", username, roles);
//            roles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
//        } else {
//            //roles = Utils.methodAccessListToUniqueRoles(userRepository.findMethodAccessListByUsername(username));
//            //test-apu
//            roles=null;
//            logger.debug("tokenType : refresh_token, Username : {}, -> roles : {}", username, roles);
//            roles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
//        }
//        //UserDetails userDetails = new Nid2UserDetails(username, null, roles);
//        //test
//        UserDetails userDetails=null;//apu-test null
//        request.getSession().setAttribute(USER_DETAILS, userDetails);
//        request.getSession().setAttribute(TYPE_OF_TOKEN, tokenType);
//        return new UsernamePasswordAuthenticationToken(userDetails, "", grantedAuthorities);
//    }
//
//    public Claims resolveClaims(HttpServletRequest req) {
//        try {
//            logger.debug("Trying to resolve claims token ");
//            String bearerToken = req.getHeader(TOKEN_HEADER);
//            if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
//                return parseJwtClaims(bearerToken.substring(TOKEN_PREFIX.length()));
//            }
//            return null;
//        } catch (ExpiredJwtException ex) {
//            logger.debug("Could not parse jwt claims, Token Expired ", ex);
//            req.setAttribute(EXPIRED, ex.getMessage());
//            throw new InvalidJwtAuthenticationException(EXPIRED_TOKEN, ex);
//        } catch (Exception ex) {
//            logger.debug("Could not parse jwt claims, Token Invalid ", ex);
//            req.setAttribute(INVALID, ex.getMessage());
//            throw new InvalidJwtAuthenticationException(INVALID_TOKEN, ex);
//        }
//    }
//
//    public String resolveToken(HttpServletRequest request) {
//        logger.debug("Resolving jwt token from request");
//        String bearerToken = request.getHeader(TOKEN_HEADER);
//        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
//            logger.debug("Bearer token found in header, returning token");
//            return bearerToken.substring(TOKEN_PREFIX.length());
//        }
//        logger.debug("Bearer token NOT found in header, returning token : null");
//        return null;
//    }
//
//    public boolean validateClaims(Claims claims) throws InvalidJwtAuthenticationException {
//        try {
//            logger.debug("Validating jwt token Claims");
//            return claims.getExpiration().after(new Date());
//        } catch (Exception e) {
//            logger.debug("Exception while parsing Claims");
//            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token", e);
//        }
//    }
//
//    public boolean tokenNotBlackListed(String token) {
//        logger.debug("Checking if jwt token already in blacklist");
//        String key = getBlackListKeyByToken(token);
//        String value = (String) redisTemplate.opsForValue().get(key);
//        if (value == null) {
//            logger.debug("Jwt token already in BLACK LIST : FALSE");
//            return true;
//        } else {
//            logger.debug("Jwt token already in BLACK LIST : TRUE");
//            return false;
//        }
//    }
//
//    public String getUsername(Claims claims) {
//        return claims.getSubject();
//    }
//
//    private List<String> getRoles(Claims claims) {
//        return (List<String>) claims.get("roles");
//    }
//
//    private Claims parseJwtClaims(String token) {
//        return jwtParser.parseClaimsJws(token).getBody();
//    }
//    public String getTokenType(String token) {
//        logger.debug("Parsing token type from token : {}", token);
//        try {
//            if (token != null) {
//                return (String) jwtParser.parse(token).getHeader().get(TOKEN_TYPE);
//            }
//            logger.debug("Token is Null, returning TYPE : null");
//            return null;
//        } catch (ExpiredJwtException ex) {
//            logger.debug("Could not parse jwt claims, Token Expired ", ex);
//            throw new InvalidJwtAuthenticationException(EXPIRED_TOKEN, ex);
//        } catch (Exception ex) {
//            logger.debug("Could not parse jwt claims, Token Invalid ", ex);
//            throw new InvalidJwtAuthenticationException(INVALID_TOKEN, ex);
//        }
//    }
//
//    private String getBlackListKeyByToken(String token) {
//        if (getTokenType(token).equals(ACCESS_TOKEN)) {
//            return BLACK_LIST_ACCESS_TOKEN_PREFIX + token;
//        } else {
//            return BLACK_LIST_REFRESH_TOKEN_PREFIX + token;
//        }
//    }
//
//    public boolean insertTokenToBlackList(HttpServletRequest request, String tokenType) {
//        try {
//            if (tokenType.equals(ACCESS_TOKEN)) {
//                String accessToken = resolveToken(request);
//                String blAccessTokenKey = SecurityConstants.BLACK_LIST_ACCESS_TOKEN_PREFIX + accessToken;
//                redisTemplate.opsForValue().set(blAccessTokenKey, accessToken,
//                        TimeUnit.MINUTES.toMillis(accessTokenValidity), TimeUnit.MILLISECONDS);
//                return true;
//            } else {
//                String refreshToken = resolveToken(request);
//                String blRefreshTokenKey = SecurityConstants.BLACK_LIST_REFRESH_TOKEN_PREFIX + refreshToken;
//                redisTemplate.opsForValue().set(blRefreshTokenKey, refreshToken,
//                        TimeUnit.MINUTES.toMillis(refreshTokenValidity), TimeUnit.MILLISECONDS);
//                return true;
//            }
//        } catch (Exception ex) {
//            logger.debug("Error While Insert JWT Toke To BlackList", ex);
//            return false;
//        }
//    }
//
//    /*public Claims getClaims(String token) {
//        try {
//            Claims claims = Jwts.claims();
//            File resource = new ClassPathResource(publicKeyFilePath).getFile();
//            byte[] keyBytes = Files.readAllBytes(resource.toPath());
//            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
//            KeyFactory kf = KeyFactory.getInstance("RSA");
//            PublicKey publicKey = kf.generatePublic(spec);
//            claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
//            return claims;
//        } catch (ExpiredJwtException e) {
//            return e.getClaims();
//        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
//            logger.debug("Exception while signing Key", e);
//        }
//        return null;
//    }*/
//
//    /*public String getPayload(String token) {
//        Base64.Decoder decoder = Base64.getUrlDecoder();
//        String[] parts = token.split("\\."); // Splitting header, payload and signature
//        return new String(decoder.decode(parts[1]));
//    }*/
//}
