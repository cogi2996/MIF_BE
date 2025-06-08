package com.mif.movieInsideForum.Filter;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mif.movieInsideForum.Property.JwtProperties;
import com.mif.movieInsideForum.Module.User.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    public String generateToken(UserDetails userDetails,  Long expireTime) {
        return generateToken(new HashMap<>(), userDetails,expireTime);
    }



    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        if (extraClaims.get("email") == null) {
            userRepository.findById(new ObjectId(userDetails.getUsername()))
                    .ifPresent(user -> {
                        extraClaims.put("email", user.getEmail());
                    });
        }
        extraClaims.put("role", userDetails.getAuthorities().stream()
        .filter(authority -> authority.getAuthority().startsWith("ROLE_")).findFirst().get().getAuthority().substring(5));
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration())) // expire after
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expireTime) {
        if (extraClaims.get("email") == null) {
            userRepository.findById(new ObjectId(userDetails.getUsername()))
                    .ifPresent(user -> {
                        extraClaims.put("email", user.getEmail());
                    });
        }
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +expireTime)) // expire after
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keysByte = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keysByte);
    }
}