package com.uam.tecnouam.prestamos.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** JWT settings bound from {@code app.jwt.*}. Fails fast when the secret is missing or too short. */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, Duration expiration) {

    private static final int MIN_SECRET_LENGTH = 32;

    public JwtProperties {
        if (secret == null || secret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "JWT_SECRET must be set and have at least " + MIN_SECRET_LENGTH + " characters");
        }
        if (expiration == null || expiration.isNegative() || expiration.isZero()) {
            throw new IllegalStateException("app.jwt.expiration must be a positive duration");
        }
    }
}
