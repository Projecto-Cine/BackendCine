package com.cine.demo.security;

import com.cine.demo.model.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final String secret;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${jwt.secret:cine-backend-secret-key-very-long-and-secure-for-hs256-256bits}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMillis) {
        this.secret = secret;
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(Long userId, String email, Role role) {
        return generateToken(userId, email, role.name());
    }

    public String generateToken(Long userId, String email, String role) {
        long nowSeconds = Instant.now().getEpochSecond();
        long expSeconds = nowSeconds + (expirationMillis / 1000L);

        String payloadJson = String.format(
                "{\"sub\":\"%d\",\"email\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                userId, email, role, nowSeconds, expSeconds);

        String headerEncoded = URL_ENCODER.encodeToString(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
        String payloadEncoded = URL_ENCODER.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signature = sign(headerEncoded + "." + payloadEncoded);

        return headerEncoded + "." + payloadEncoded + "." + signature;
    }

    public Map<String, String> validateAndExtract(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Empty or null token");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new InvalidTokenException("Invalid token format");
        }
        String expectedSignature = sign(parts[0] + "." + parts[1]);
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            throw new InvalidTokenException("Invalid token signature");
        }
        String payloadJson = new String(URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, String> claims = parsePayload(payloadJson);

        long expSeconds = Long.parseLong(claims.get("exp"));
        if (Instant.now().getEpochSecond() >= expSeconds) {
            throw new InvalidTokenException("Token expired");
        }
        return claims;
    }

    public long getExpirationMillis() {
        return expirationMillis;
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return URL_ENCODER.encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new InvalidTokenException("Could not sign token: " + e.getMessage());
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private Map<String, String> parsePayload(String json) {
        Map<String, String> claims = new HashMap<>();
        String trimmed = json.trim();
        if (trimmed.startsWith("{")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("}")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        for (String pair : trimmed.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length != 2) continue;
            String key = stripQuotes(kv[0].trim());
            String value = stripQuotes(kv[1].trim());
            claims.put(key, value);
        }
        return claims;
    }

    private String stripQuotes(String s) {
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}
