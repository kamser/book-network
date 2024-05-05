package com.kamser.booknetwork.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.awt.desktop.SystemEventListener;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;
    @Value("${application.security.jwt.security-key}")
    private String securityKey;

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails){ //The claims are just extra information that I could add to the token, and userDetails is the information related to the user.

        return buildToken(claims, userDetails, jwtExpiration);

    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Long jwtExpiration){
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts
                .builder()                                                  //Create the jwt token
                .setClaims(extraClaims)                                     //set the extra information that I set on the map
                .setSubject(userDetails.getUsername())                      //attach the user to the token
                .setIssuedAt(new Date(System.currentTimeMillis()))          //Set the start valid date for the token
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))    //Set the expiration date for the token.
                .claim("authorities", authorities)                          //set other claims for the token
                .signWith(getSignInKey())                                       // Place the unique sign to the token, this is the security step.
                .compact();                                                     //wrapper all.
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }


    private Key getSignInKey() {
        byte [] keyBytes = Decoders.BASE64.decode(securityKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
