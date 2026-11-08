package com.meto.api;

import com.meto.api.application.port.ForecastPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

/** Sécurité réelle (profil par défaut) : 401 sans jeton ou avec un jeton invalide, santé publique. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"meteo.service-account.email=platform@meteo.local",
                "meteo.service-account.password-hash=$2a$10$9kLE2fsSsLj/RQLuQMQyceWRrOccP6Af/lzkysXJy8c2CHxSXGTc2"})
@AutoConfigureWebTestClient
class SecurityIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ForecastPort forecastPort;

    @Test
    void anonymousAndForgedTokensAreRejected() {
        webTestClient.get().uri("/meteo-api/v2/forecast?city=Paris").exchange().expectStatus().isUnauthorized();
        webTestClient.get().uri("/meteo-api/v2/forecast?city=Paris")
                .header("Authorization", "Bearer forged.token.value")
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void healthIsPublic() {
        webTestClient.get().uri("/actuator/health").exchange().expectStatus().isOk();
    }

    @Test
    void serviceAccountCanSignInAndCallTheApi() {
        // Le hash de test correspond au mot de passe "s3rvice-pass"
        String token = webTestClient.post().uri("/rest/auth/login")
                .bodyValue(java.util.Map.of("email", "platform@meteo.local", "password", "s3rvice-pass"))
                .exchange().expectStatus().isOk()
                .expectBody(java.util.Map.class).returnResult().getResponseBody().get("token").toString();

        webTestClient.get().uri("/meteo-api/v2/cities?q=P")
                .header("Authorization", "Bearer " + token)
                .exchange().expectStatus().isOk();
    }
}
