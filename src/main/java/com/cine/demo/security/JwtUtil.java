package com.cine.demo.security;

import com.cine.demo.model.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Emision y validacion de JWT sobre jjwt.
 *
 * La version anterior implementaba el formato a mano: construia el payload con
 * String.format y lo parseaba con split(","). Eso traia tres problemas que una
 * libreria resuelve de serie:
 *
 *  - El email se interpolaba sin escapar en el JSON, de modo que un valor con
 *    comillas o comas rompia el parseo o alteraba los claims.
 *  - No habia issuer ni validacion del mismo, asi que un token emitido por otro
 *    sistema que compartiera secreto era aceptado.
 *  - El parseo artesanal no distinguia un token mal formado de uno manipulado.
 *
 * jjwt ya estaba declarado en el pom.xml y no se usaba.
 */
@Component
public class JwtUtil {

    /** HS256 exige una clave de al menos 256 bits. */
    private static final int MIN_SECRET_BYTES = 32;

    private static final String ISSUER = "lumen-cinema";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";

    private final SecretKey key;
    private final long expirationMillis;

    /**
     * Sin valor por defecto a proposito. Antes habia uno literal en el codigo:
     * al estar el repositorio publico, cualquiera podia firmar un token valido
     * con el rol que quisiera contra cualquier despliegue que no definiera
     * JWT_SECRET. Es preferible que la aplicacion no arranque.
     */
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMillis) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "jwt.secret no definido. Define la variable de entorno JWT_SECRET.");
        }
        int secretBytes = secret.getBytes(StandardCharsets.UTF_8).length;
        if (secretBytes < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "jwt.secret demasiado corto: " + secretBytes + " bytes. HS256 requiere al menos "
                    + MIN_SECRET_BYTES + " bytes (256 bits).");
        }
        if (expirationMillis <= 0) {
            throw new IllegalStateException("jwt.expiration debe ser positivo.");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(Long userId, String email, Role role) {
        return generateToken(userId, email, role.name());
    }

    public String generateToken(Long userId, String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(String.valueOf(userId))
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLE, role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Devuelve los claims como mapa de cadenas. Lanza InvalidTokenException ante
     * cualquier token ausente, mal formado, con firma invalida o caducado.
     */
    public Map<String, String> validateAndExtract(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Empty or null token");
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Map<String, String> result = new HashMap<>();
            result.put("sub", claims.getSubject());
            result.put(CLAIM_EMAIL, claims.get(CLAIM_EMAIL, String.class));
            result.put(CLAIM_ROLE, claims.get(CLAIM_ROLE, String.class));
            if (claims.getIssuedAt() != null) {
                result.put("iat", String.valueOf(claims.getIssuedAt().toInstant().getEpochSecond()));
            }
            if (claims.getExpiration() != null) {
                result.put("exp", String.valueOf(claims.getExpiration().toInstant().getEpochSecond()));
            }
            return result;

        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token expired");
        } catch (SignatureException e) {
            throw new InvalidTokenException("Invalid token signature");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid token format");
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid token: " + e.getMessage());
        }
    }

    public long getExpirationMillis() {
        return expirationMillis;
    }
}
