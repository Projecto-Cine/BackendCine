package com.cine.demo.security;

import com.cine.demo.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("test-secret-key-for-jwt-signing-must-be-long-enough-256bits", 30L);
    }

    @Test
    void generateToken_returnsThreePartJwt() {
        String token = jwtUtil.generateToken(1L, "ana@test.com", Role.CLIENT);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void validateAndExtract_returnsClaims_whenTokenValid() {
        String token = jwtUtil.generateToken(42L, "admin@cine.com", Role.ADMIN);

        Map<String, String> claims = jwtUtil.validateAndExtract(token);

        assertThat(claims.get("sub")).isEqualTo("42");
        assertThat(claims.get("email")).isEqualTo("admin@cine.com");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
        assertThat(claims).containsKeys("iat", "exp");
    }

    @Test
    void validateAndExtract_throwsInvalidTokenException_whenTokenIsNull() {
        assertThatThrownBy(() -> jwtUtil.validateAndExtract(null))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Empty or null token");
    }

    @Test
    void validateAndExtract_throwsInvalidTokenException_whenTokenIsBlank() {
        assertThatThrownBy(() -> jwtUtil.validateAndExtract("   "))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Empty or null token");
    }

    @Test
    void validateAndExtract_throwsInvalidTokenException_whenFormatIsInvalid() {
        assertThatThrownBy(() -> jwtUtil.validateAndExtract("not.a.valid.jwt.token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid token format");
    }

    @Test
    void validateAndExtract_throwsInvalidTokenException_whenSignatureTampered() {
        String token = jwtUtil.generateToken(1L, "ana@test.com", Role.CLIENT);
        String[] parts = token.split("\\.");
        String tampered = parts[0] + "." + parts[1] + ".invalidSignatureHere";

        assertThatThrownBy(() -> jwtUtil.validateAndExtract(tampered))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid token signature");
    }

    @Test
    void validateAndExtract_throwsInvalidTokenException_whenSignedWithDifferentSecret() {
        JwtUtil other = new JwtUtil("a-different-secret-key-256-bits-long-for-testing-purpose", 30L);
        String forged = other.generateToken(1L, "ana@test.com", Role.CLIENT);

        assertThatThrownBy(() -> jwtUtil.validateAndExtract(forged))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid token signature");
    }

    @Test
    void validateAndExtract_throwsInvalidTokenException_whenTokenExpired() throws Exception {
        JwtUtil shortLived = new JwtUtil("test-secret-key-256-bits-must-be-long-enough-here-ok", 0L);
        String token = shortLived.generateToken(1L, "ana@test.com", Role.CLIENT);
        forceExpired(token, shortLived);

        assertThatThrownBy(() -> shortLived.validateAndExtract(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Token expired");
    }

    @Test
    void getExpirationMillis_returnsConfiguredValueInMillis() {
        JwtUtil util15 = new JwtUtil("any-secret-key-of-sufficient-length-for-tests-here-ok", 15L);

        assertThat(util15.getExpirationMillis()).isEqualTo(15L * 60_000L);
    }

    @Test
    void generateToken_producesDistinctSignaturesForDifferentSecrets() {
        JwtUtil another = new JwtUtil("another-secret-key-very-different-but-also-256-bits-len", 30L);

        String tokenA = jwtUtil.generateToken(1L, "ana@test.com", Role.CLIENT);
        String tokenB = another.generateToken(1L, "ana@test.com", Role.CLIENT);

        assertThat(tokenA.split("\\.")[2]).isNotEqualTo(tokenB.split("\\.")[2]);
    }

    @Test
    void generatedToken_hasExpInTheFuture() {
        long before = Instant.now().getEpochSecond();
        String token = jwtUtil.generateToken(1L, "ana@test.com", Role.CLIENT);

        Map<String, String> claims = jwtUtil.validateAndExtract(token);

        long exp = Long.parseLong(claims.get("exp"));
        assertThat(exp).isGreaterThan(before);
    }

    private void forceExpired(String token, JwtUtil util) throws Exception {
        Field field = JwtUtil.class.getDeclaredField("expirationMillis");
        field.setAccessible(true);
        field.setLong(util, -60_000L);
    }
}
