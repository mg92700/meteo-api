package com.meto.api;

import com.meto.api.domain.model.User;
import com.meto.api.application.port.UserRepositoryPort;
import com.meto.api.infrastructure.adapter.security.LoginReq;
import com.meto.api.infrastructure.adapter.security.LoginRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @Test
    void testLogin() {
        String email = "test@example.com";
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, "USER");
        user.setId(1L);

        when(userRepositoryPort.findByEmail(email)).thenReturn(user);

        LoginReq loginReq = new LoginReq(email, password);

        webTestClient.post()
                .uri("/rest/auth/login")
                .bodyValue(loginReq)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginRes.class)
                .value(res -> res.email().equals(email));
    }
}
