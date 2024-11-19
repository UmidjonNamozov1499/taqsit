package com.example.taqsit.secret;

import com.example.taqsit.entity.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {
    @Value("${app.jwtSecret}")
    private String secretKey;

    @Value("${token.lifecycle.millisecond}")
    private long tokenExpireTime;

    public Map<String, Object> generateToken(User user) {
        Date date = new Date();
        long l = date.getTime() + tokenExpireTime;
        Date expireDate = new Date(l);

        return Map.of(
                "expiredDate", expireDate,
                "token", Jwts
                        .builder()
                        .setSubject(user.getId().toString())
                        .setIssuedAt(date)
                        .claim("data", Map.of(
                                "id", user.getId(),
                                "username", user.getUsername(),
                                "lastName", user.getLastName(),
                                "firstName", user.getFirstName()
                        ))
                        .setExpiration(expireDate)
                        .signWith(SignatureAlgorithm.HS512, secretKey)
                        .compact()
        );
    }


    public boolean validateToken(String token) {
        try {
            Jwts
                    .parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Muddati o'tgan");
        } catch (MalformedJwtException malformedJwtException) {
            System.err.println("Buzilgan token");
        } catch (SignatureException s) {
            System.err.println("Kalit so'z xato");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            System.err.println("Qo'llanilmagan token");
        } catch (IllegalArgumentException ex) {
            System.err.println("Bo'sh token");
        }
        return false;
    }

    public String getUserIdFromToken(String token) {
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
