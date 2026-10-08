package com.cine.demo.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Valida que la configuracion obligatoria este presente antes de que arranque
 * el contexto.
 *
 * Sin esto, una variable de entorno ausente no produce un error util: Spring
 * deja el placeholder sin resolver y lo pasa tal cual al componente que lo use,
 * de modo que una DB_PASS olvidada acaba como "Access denied for user 'root'"
 * y una CLOUDINARY_API_SECRET ausente como un fallo en la primera subida de
 * imagen, ya en ejecucion. Aqui se detectan todas de golpe, con el nombre de la
 * variable y que hacer.
 *
 * Se registra como ApplicationContextInitializer en DemoApplication: se ejecuta
 * con el Environment ya resuelto pero antes de instanciar cualquier bean, de
 * modo que el mensaje llega antes que cualquier error derivado.
 */
public class RequiredConfigValidator implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    /** Propiedad -> variable de entorno que la alimenta en produccion. */
    private static final Map<String, String> REQUIRED = new LinkedHashMap<>();
    static {
        REQUIRED.put("spring.datasource.password", "DB_PASS");
        REQUIRED.put("cloudinary.cloud-name", "CLOUDINARY_CLOUD_NAME");
        REQUIRED.put("cloudinary.api-key", "CLOUDINARY_API_KEY");
        REQUIRED.put("cloudinary.api-secret", "CLOUDINARY_API_SECRET");
        REQUIRED.put("spring.mail.username", "MAIL_USERNAME");
        REQUIRED.put("spring.mail.password", "MAIL_PASSWORD");
        REQUIRED.put("app.mail.from", "MAIL_FROM");
        REQUIRED.put("jwt.secret", "JWT_SECRET");
        REQUIRED.put("app.cors.allowed-origins", "CORS_ALLOWED_ORIGINS");
        REQUIRED.put("stripe.secret-key", "STRIPE_SECRET_KEY");
        REQUIRED.put("stripe.publishable-key", "STRIPE_PUBLISHABLE_KEY");
        REQUIRED.put("stripe.webhook-secret", "STRIPE_WEBHOOK_SECRET");
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        validate(applicationContext.getEnvironment());
    }

    void validate(Environment environment) {
        List<String> missing = REQUIRED.entrySet().stream()
                .filter(e -> !isConfigured(environment, e.getKey()))
                .map(e -> "  - " + e.getKey() + "   (variable de entorno: " + e.getValue() + ")")
                .toList();

        if (!missing.isEmpty()) {
            throw new IllegalStateException(buildMessage(missing));
        }
    }

    private boolean isConfigured(Environment environment, String key) {
        String value;
        try {
            value = environment.getProperty(key);
        } catch (IllegalArgumentException unresolvablePlaceholder) {
            // La propiedad existe pero apunta a una variable de entorno ausente.
            return false;
        }
        if (value == null || value.isBlank()) {
            return false;
        }
        // Placeholder sin resolver que llego como texto literal.
        if (value.startsWith("${")) {
            return false;
        }
        // Plantilla sin rellenar: application-local.properties.example
        return !value.startsWith("YOUR_")
                && !value.contains("YOUR_SECRET_KEY")
                && !value.contains("YOUR_PUBLISHABLE_KEY")
                && !value.contains("YOUR_WEBHOOK_SECRET")
                && !value.equals("GENERATED_BY_SETUP_SCRIPT");
    }

    private String buildMessage(List<String> missing) {
        return """

                ===========================================================================
                CONFIGURACION INCOMPLETA: la aplicacion no va a arrancar.

                Falta por definir:
                %s

                Ninguna de estas claves tiene valor por defecto, a proposito. Un arranque
                con un secreto por defecto conocido es peor que un arranque fallido, porque
                nadie se entera de que esta usando una clave publica.

                En local:
                    setup-local.ps1
                    mvnw spring-boot:run -Dspring-boot.run.profiles=local

                En produccion (Render):
                    define las variables de entorno indicadas arriba en el panel del servicio.
                ===========================================================================
                """.formatted(String.join("\n", missing));
    }
}
