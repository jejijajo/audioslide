package com.audioslide.auth;

import com.audioslide.auth.entity.RefreshToken;
import com.audioslide.auth.entity.User;
import com.audioslide.auth.exception.RefreshTokenInvalidException;
import com.audioslide.auth.repository.RefreshTokenRepository;
import com.audioslide.auth.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "ttl", 3600000L);
    }

    @Test
    void issueMethodShouldSaveHashAndReturnRawToken() {

        User user = new User();

        String raw = refreshTokenService.issue(user);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        RefreshToken saved = captor.getValue();

        assertNotNull(raw);
        assertNotEquals(raw, saved.getTokenHash());
        assertEquals(user, saved.getUser());
        assertTrue(saved.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void shouldThrowExceptionIfTokenExpired() {
        RefreshToken mockToken = new RefreshToken();
        mockToken.setExpiresAt(Instant.now().minusSeconds(10));

        when(refreshTokenRepository.findByTokenHash(anyString()))
                .thenReturn(Optional.of(mockToken));

        assertThrows(RefreshTokenInvalidException.class,
                () -> refreshTokenService.validate("123"));

        verify(refreshTokenRepository).delete(mockToken);
    }

}
