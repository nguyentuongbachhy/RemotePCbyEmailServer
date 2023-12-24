package com.example.server.Config.Jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.server.Role.Role;
import com.example.server.User.UserInformation;
import com.example.server.User.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
    
    @Autowired
    private UserService userService;

    public static final String SERECT = "PTKT2701NTBH0803withLovefriday171120230911076983";

    public String generateToken(String username) {
        Map<String , Object> claims = new HashMap<>();
        UserInformation user = userService.findByUsername(username);
        boolean isAdmin = false;
        boolean isUser = false;
        if(user != null && !user.getRoles().isEmpty()) {
            List<Role> listRoles = user.getRoles();
            for(Role role : listRoles) {
                if(role.getRoleName().equals("ADMIN")) {
                    isAdmin = true;
                    break;
                }
                if(role.getRoleName().equals("USER")) {
                    isUser = true;
                    break;
                }
            }

        }
        claims.put("isAdmin", isAdmin);
        claims.put("isUser", isUser);

        return createJWToken(claims, username);
    }

    private String createJWToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 120 * 60 * 1000))
                    .signWith(SignatureAlgorithm.HS256, getSignedKey())
                    .compact();
    }

    private Key getSignedKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SERECT);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSignedKey()).parseClaimsJws(token).getBody();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private Boolean isJWTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateJWToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isJWTokenExpired(token));
    }
}
