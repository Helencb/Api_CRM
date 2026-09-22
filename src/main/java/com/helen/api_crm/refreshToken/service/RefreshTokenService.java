package com.helen.api_crm.refreshToken.service;

import com.helen.api_crm.auth.repository.UserRepository;
import com.helen.api_crm.exception.ResourceNotFoundException;
import com.helen.api_crm.refreshToken.model.RefreshToken;
import com.helen.api_crm.refreshToken.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${api.security.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // Only the SHA-256 hash is persisted, so a leaked database dump does not expose usable refresh tokens.
    private static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    public RefreshToken findByToken(String rawToken) {
        return refreshTokenRepository.findByToken(hash(rawToken))
                .orElseThrow(() -> new ResourceNotFoundException("Refresh Token not found"));
    }

    /**
     * Returns the plaintext refresh token to hand to the client; only its hash is stored.
     */
    public String createRefreshToken(String email) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email)));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        String rawToken = UUID.randomUUID().toString();
        refreshToken.setToken(hash(rawToken));
        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new ResourceNotFoundException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
