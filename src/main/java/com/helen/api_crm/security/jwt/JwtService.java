package com.helen.api_crm.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    // Chave secreta (ideal: guardar no application.properties)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Gera um token JWT a partir do username
    public String generateToken(String username) {
        // Tempo de expiração do token (ex: 1 hora)
        long expirationMillis = 3600000;
        return Jwts.builder()
                .setSubject(username) // aqui colocamos o username
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(secretKey)
                .compact();
    }

    // Extrai o username (subject) do token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Valida se o token é válido (assinatura e expiração)
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Método interno que decodifica o token
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
