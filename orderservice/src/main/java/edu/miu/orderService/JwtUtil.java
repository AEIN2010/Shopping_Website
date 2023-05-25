package edu.miu.orderService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtUtil {
    private static final String SECRET_KEY = "dummy_secret_key"; // Replace with your own secret key

    public static String extractEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("email", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            // Handle invalid token or missing email claim
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }
}

