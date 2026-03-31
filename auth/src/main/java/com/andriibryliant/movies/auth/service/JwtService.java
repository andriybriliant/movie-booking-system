package com.andriibryliant.movies.auth.service;

import com.andriibryliant.movies.auth.entity.RefreshToken;
import com.andriibryliant.movies.auth.repository.RefreshTokenRepository;
import com.andriibryliant.movies.auth.repository.UserCredentialRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JwtService {

    private RefreshTokenRepository refreshTokenRepository;

    private UserCredentialRepository userCredentialRepository;

    @Value("${security.jwt.secret}")
    public static String SECRET;

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey()).compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public RefreshToken createRefreshToken(String username) {
        return userCredentialRepository.findByEmail(username)
                .map(userCredential -> {
                    Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserCredentialId(userCredential.getId());
                    RefreshToken refreshToken;
                    if (existingToken.isPresent()) {
                        refreshToken = existingToken.get();
                        refreshToken.setToken(UUID.randomUUID().toString());
                        refreshToken.setExpiryDate(Instant.now().plusMillis(600000).toString());
                    } else {
                        refreshToken = RefreshToken.builder()
                                .userCredential(userCredential)
                                .token(UUID.randomUUID().toString())
                                .expiryDate(Instant.now().plusMillis(600000).toString())
                                .build();
                    }
                    return refreshTokenRepository.save(refreshToken);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (Instant.parse(token.getExpiryDate()).compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new sign in request");
        }
        return token;
    }
}
