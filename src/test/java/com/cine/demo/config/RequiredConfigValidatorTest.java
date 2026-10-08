package com.cine.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequiredConfigValidatorTest {

    private final RequiredConfigValidator validator = new RequiredConfigValidator();

    private static MockEnvironment fullyConfigured() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("spring.datasource.password", "una-password");
        env.setProperty("cloudinary.cloud-name", "mi-cloud");
        env.setProperty("cloudinary.api-key", "123456789");
        env.setProperty("cloudinary.api-secret", "un-secreto");
        env.setProperty("spring.mail.username", "yo@example.com");
        env.setProperty("spring.mail.password", "una-app-password");
        env.setProperty("app.mail.from", "yo@example.com");
        env.setProperty("jwt.secret", "un-secreto-de-al-menos-256-bits-de-longitud-vale");
        env.setProperty("app.cors.allowed-origins", "http://localhost:5173");
        env.setProperty("stripe.secret-key", "sk_test_abc");
        env.setProperty("stripe.publishable-key", "pk_test_abc");
        env.setProperty("stripe.webhook-secret", "whsec_abc");
        return env;
    }

    @Test
    void passesWhenEverythingIsConfigured() {
        assertThatCode(() -> validator.validate(fullyConfigured())).doesNotThrowAnyException();
    }

    @Test
    void failsWhenAPropertyIsAbsent() {
        MockEnvironment env = fullyConfigured();
        env.getPropertySources().remove("mockProperties");
        env.setProperty("jwt.secret", "un-secreto-de-al-menos-256-bits-de-longitud-vale");

        assertThatThrownBy(() -> validator.validate(env))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CONFIGURACION INCOMPLETA");
    }

    @Test
    void reportsTheEnvironmentVariableNameSoItIsActionable() {
        MockEnvironment env = fullyConfigured();
        env.setProperty("jwt.secret", "");

        assertThatThrownBy(() -> validator.validate(env))
                .hasMessageContaining("jwt.secret")
                .hasMessageContaining("JWT_SECRET");
    }

    /**
     * El fallo que motivo esta clase: un placeholder sin resolver llegaba como
     * texto literal y acababa usandose como credencial, produciendo un
     * "Access denied for user 'root'" que no apunta a la causa.
     */
    @Test
    void treatsUnresolvedPlaceholderAsMissing() {
        MockEnvironment env = fullyConfigured();
        env.setProperty("spring.datasource.password", "${DB_PASS}");

        assertThatThrownBy(() -> validator.validate(env))
                .hasMessageContaining("spring.datasource.password")
                .hasMessageContaining("DB_PASS");
    }

    @Test
    void treatsUnfilledTemplatePlaceholderAsMissing() {
        MockEnvironment env = fullyConfigured();
        env.setProperty("cloudinary.api-secret", "YOUR_API_SECRET");

        assertThatThrownBy(() -> validator.validate(env))
                .hasMessageContaining("cloudinary.api-secret");
    }

    @Test
    void treatsUngeneratedJwtSecretAsMissing() {
        MockEnvironment env = fullyConfigured();
        env.setProperty("jwt.secret", "GENERATED_BY_SETUP_SCRIPT");

        assertThatThrownBy(() -> validator.validate(env))
                .hasMessageContaining("jwt.secret");
    }

    @Test
    void treatsStripeTemplateKeysAsMissing() {
        MockEnvironment env = fullyConfigured();
        env.setProperty("stripe.secret-key", "sk_test_YOUR_SECRET_KEY");

        assertThatThrownBy(() -> validator.validate(env))
                .hasMessageContaining("stripe.secret-key");
    }

    @Test
    void listsEveryMissingPropertyAtOnceInsteadOfOnePerRestart() {
        MockEnvironment env = fullyConfigured();
        env.setProperty("jwt.secret", "");
        env.setProperty("cloudinary.api-secret", "");
        env.setProperty("stripe.webhook-secret", "");

        assertThatThrownBy(() -> validator.validate(env))
                .hasMessageContaining("jwt.secret")
                .hasMessageContaining("cloudinary.api-secret")
                .hasMessageContaining("stripe.webhook-secret");
    }

    @Test
    void messageExplainsHowToFixItLocallyAndInProduction() {
        MockEnvironment env = fullyConfigured();
        env.setProperty("jwt.secret", "");

        assertThatThrownBy(() -> validator.validate(env))
                .hasMessageContaining("setup-local.ps1")
                .hasMessageContaining("Render");
    }
}
