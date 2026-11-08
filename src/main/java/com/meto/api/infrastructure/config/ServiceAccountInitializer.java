package com.meto.api.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Compte de service des applications clientes (ex. platform-back), défini par l'environnement :
 * METEO_SERVICE_EMAIL et METEO_SERVICE_PASSWORD_HASH (hash BCrypt, jamais le mot de passe en clair).
 * Créé au démarrage, ou mis à jour s'il existe déjà. Rôle USER : accès à /meteo-api/** seulement.
 */
@Slf4j
@Component
public class ServiceAccountInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final String email;
    private final String passwordHash;

    public ServiceAccountInitializer(JdbcTemplate jdbc,
                                     @Value("${meteo.service-account.email:}") String email,
                                     @Value("${meteo.service-account.password-hash:}") String passwordHash) {
        this.jdbc = jdbc;
        this.email = email == null ? "" : email.strip();
        this.passwordHash = passwordHash == null ? "" : passwordHash.strip();
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isEmpty() && passwordHash.isEmpty()) {
            return; // pas de compte de service configuré
        }
        if (email.isEmpty() || !passwordHash.startsWith("$2")) {
            throw new IllegalStateException(
                    "METEO_SERVICE_EMAIL et METEO_SERVICE_PASSWORD_HASH (hash BCrypt $2a/$2b/$2y) sont requis ensemble");
        }
        int updated = jdbc.update("update user_app set password = ?, role = 'USER' where email = ?", passwordHash, email);
        if (updated == 0) {
            jdbc.update("insert into user_app (email, password, firstname, lastname, role) values (?, ?, ?, ?, 'USER')",
                    email, passwordHash, "Service", "Account");
        }
        log.info("Compte de service prêt : {}", email);
    }
}
