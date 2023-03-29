package com.cloudkeep.CloudKeep.config;

import com.cloudkeep.CloudKeep.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String JWT_SECRET;
    public String extractUsername(String header) {
        return extractClaim(header, Claims::getSubject);
    }

    public Date extractExpiration(String header) {
        return extractClaim(header, Claims::getExpiration);
    }

    public Long extractId(String header) {
        return Long.valueOf(extractClaim(header, Claims::getId));
    }

    public <T> T extractClaim(String header, Function<Claims, T> claimsResolver) {
        final String token = extractTokenFromHeader(header);
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public Boolean isTokenExpired(String header) {
        return extractExpiration(header).before(new Date());
    }

    public Boolean isTokenValid(String header, UserDetails userDetails) {
        final String username = extractUsername(header);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(header));
    }

    public String generateToken(Map<String, Object> extraClaims, User user) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setId(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 10))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] decodedKey = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    private String extractTokenFromHeader(String header) {
        return header.substring(7);
    }
}
