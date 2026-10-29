package com.uam.tecnouam.prestamos.security;

import com.uam.tecnouam.prestamos.config.JwtProperties;
import com.uam.tecnouam.prestamos.model.Rol;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/** Creates and validates HS256-signed tokens carrying the username (subject) and a "rol" claim. */
@Service
public class JwtService {

    private static final String CLAIM_ROL = "rol";

    private final SecretKey key;
    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(String username, Rol rol) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_ROL, rol.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.expiration())))
                .signWith(key)
                .compact();
    }

    public long expiracionSegundos() {
        return properties.expiration().toSeconds();
    }

    /** Returns the verified identity, or empty when the token is malformed, tampered with or expired. */
    public Optional<TokenData> validar(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            Rol rol = Rol.valueOf(claims.get(CLAIM_ROL, String.class));
            return Optional.of(new TokenData(claims.getSubject(), rol));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public record TokenData(String username, Rol rol) {
    }
}
