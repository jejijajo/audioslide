package com.audioslide.auth;

import com.audioslide.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Array;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @BeforeEach
    void setUp() throws Exception {
        KeyPair kp = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        String priv = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        String pub = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());

        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "privateKeyPem", priv);
        ReflectionTestUtils.setField(jwtService, "publicKeyPem", pub);
        ReflectionTestUtils.setField(jwtService, "validityInMilliseconds", 60_000L);

        jwtService.init();
    }

    @InjectMocks
    private JwtService jwtService;

    @Test
    void shouldParseToken() {
        String token = jwtService.createToken(1L, "a@b.com", Set.of("USER"));

        Claims claims = jwtService.validateAndParse(token);

        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("email")).isEqualTo("a@b.com");
        assertThat(claims.get("roles", List.class)).containsExactly("USER");

    }

    @Test
    void shouldContainsServiceTypeScopesAndFiveMinuteTtl() {
        String internalJwt = jwtService.createServiceToken("test-service",Set.of("read","write"));

        Claims claims = jwtService.validateAndParse(internalJwt);

        assertThat(claims.getSubject()).isEqualTo("test-service");
        assertThat(claims.get("type")).isEqualTo("service");
        assertThat(claims.get("scopes")).isEqualTo(List.of("write","read"));
        assertThat(claims.getIssuer()).isEqualTo("auth-service");
    }
}
