package com.chatApi.chatapi.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final Key key;
    private final long jwtExpirationMs;
    private final String secretUsedBase64;

    /**
     * This constructor tries to use the configured property `app.jwtSecret` (exact name).
     * If the property is missing or not long enough for HS256, it will generate a secure random key
     * and log a warning. This prevents startup failures due to WeakKeyException.
     *
     * Property names expected:
     * - app.jwtSecret (String) : raw or base64 secret
     * - app.jwtExpirationMs (long) : expiration in milliseconds (optional, default 86400000)
     */
    public JwtTokenProvider(@Value("${app.jwtSecret:}") String configuredSecret,
                            @Value("${app.jwtExpirationMs:86400000}") long jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;

        Key tmpKey = null;
        String base64Key = null;

        if (configuredSecret != null && !configuredSecret.isBlank()) {
            // Accept either a base64-encoded secret or raw text; try both
            try {
                // If user provided a base64 encoded string, decode it and create key
                byte[] maybeDecoded = null;
                try {
                    maybeDecoded = Base64.getDecoder().decode(configuredSecret);
                } catch (IllegalArgumentException ex) {
                    // not base64 -> treat as raw bytes below
                }

                if (maybeDecoded != null && maybeDecoded.length >= 32) {
                    tmpKey = Keys.hmacShaKeyFor(maybeDecoded);
                    base64Key = Base64.getEncoder().encodeToString(maybeDecoded);
                    LOG.info("JWT secret loaded from base64 configuration (length bytes = {}).", maybeDecoded.length);
                } else {
                    // treat it as raw text; ensure it's long enough
                    byte[] raw = configuredSecret.getBytes(StandardCharsets.UTF_8);
                    if (raw.length >= 32) {
                        tmpKey = Keys.hmacShaKeyFor(raw);
                        base64Key = Base64.getEncoder().encodeToString(raw);
                        LOG.info("JWT secret loaded from raw configuration (length bytes = {}).", raw.length);
                    } else {
                        LOG.warn("Configured app.jwtSecret is too short ({} bytes). Will generate a secure key instead.", raw.length);
                    }
                }
            } catch (WeakKeyException wke) {
                LOG.warn("Provided `app.jwtSecret` was detected too weak by jjwt: {}. A new secure key will be generated.", wke.getMessage());
            } catch (Exception e) {
                LOG.warn("Error while interpreting `app.jwtSecret` (will generate a secure key). Cause: {}", e.toString());
            }
        } else {
            LOG.warn("No app.jwtSecret provided (property 'app.jwtSecret' is empty). A secure key will be generated at runtime.");
        }

        if (tmpKey == null) {
            // generate strong key for HS256 (256-bit or more)
            tmpKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            base64Key = Base64.getEncoder().encodeToString(tmpKey.getEncoded());
            LOG.warn("Generated a new secure JWT signing key (HS256). **This is fine for local testing**, but consider setting a stable key via 'app.jwtSecret' in application.properties or an environment variable for production.");
        }

        this.key = tmpKey;
        this.secretUsedBase64 = base64Key;
        LOG.info("JWT token provider initialized. Token expiration (ms) = {}", this.jwtExpirationMs);
    }

    public String getSecretBase64() {
        return secretUsedBase64;
    }

    public String generateToken(String username, Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            LOG.debug("Invalid JWT: {}", ex.getMessage());
            return false;
        }
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        Object idObj = claims.get("userId");
        if (idObj == null) return null;
        if (idObj instanceof Integer) {
            return ((Integer) idObj).longValue();
        } else if (idObj instanceof Long) {
            return (Long) idObj;
        } else if (idObj instanceof String) {
            try { return Long.parseLong((String) idObj); } catch (NumberFormatException ex) { return null; }
        } else {
            return null;
        }
    }
}
