package com.cine.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordConfigTest {

    private final PasswordConfig config = new PasswordConfig();

    @Test
    void passwordEncoder_returnsBCryptInstance() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void passwordEncoder_encodeProducesBCryptHash() {
        PasswordEncoder encoder = config.passwordEncoder();
        String hash = encoder.encode("secret");
        assertThat(hash).startsWith("$2");
    }

    @Test
    void passwordEncoder_matchesReturnsTrueForCorrectPassword() {
        PasswordEncoder encoder = config.passwordEncoder();
        String hash = encoder.encode("myPassword");
        assertThat(encoder.matches("myPassword", hash)).isTrue();
    }

    @Test
    void passwordEncoder_matchesReturnsFalseForWrongPassword() {
        PasswordEncoder encoder = config.passwordEncoder();
        String hash = encoder.encode("correct");
        assertThat(encoder.matches("wrong", hash)).isFalse();
    }

    @Test
    void passwordEncoder_twoEncodesOfSamePasswordProduceDifferentHashes() {
        PasswordEncoder encoder = config.passwordEncoder();
        String hash1 = encoder.encode("same");
        String hash2 = encoder.encode("same");
        assertThat(hash1).isNotEqualTo(hash2);
    }
}
