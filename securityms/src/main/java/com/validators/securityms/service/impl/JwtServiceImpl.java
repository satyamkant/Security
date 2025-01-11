package com.validators.securityms.service.impl;

import com.validators.securityms.dto.UserDTO;
import com.validators.securityms.dto.UserDetails;
import com.validators.securityms.exception.CustomException;
import com.validators.securityms.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtServiceImpl implements JwtService {

    private final String  SECRET_KEY;
    private final long EXPIRATION_TIME;
    private final AuthenticationManager authenticationManager;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    @Autowired
    public JwtServiceImpl(@Value("${SECRET_KEY}") String secretKey,
                          @Value("${EXPIRATION_TIME}")long jwtExpirationMs,
                          AuthenticationManager authenticationManager) {
        this.SECRET_KEY = secretKey;
        this.EXPIRATION_TIME = jwtExpirationMs;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDTO verifyUser(UserDTO userDto) throws CustomException {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPasswordHash()));
            if (authentication.isAuthenticated()) {
                userDto.setName(((UserDetails)authentication.getPrincipal()).getUserName());
                userDto.setJwtToken(generateTokenFromUsername(userDto));
                return userDto;
            }
        }
        catch (AuthenticationException e) {
            throw new CustomException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getJwtFromHeader(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();  // Return the JWT token from the cookie
                }
            }
        }
        return null;
    }

    @Override
    public String generateTokenFromUsername(UserDTO userDto) {
        return Jwts.builder()
                .subject(userDto.getEmail())
                .claim("userName", userDto.getName())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + TimeUnit.HOURS.toMillis(EXPIRATION_TIME)))
                .signWith(key())
                .compact();
    }

    @Override
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public String getClaimFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload().get("userName", String.class);
    }


    @Override
    public boolean validateJwtToken(String authToken) throws CustomException {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new CustomException("Invalid JWT token",e);
        } catch (ExpiredJwtException e) {
            throw new CustomException("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new CustomException("Unsupported JWT token", e);
        }
    }
}
