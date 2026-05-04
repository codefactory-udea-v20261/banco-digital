package com.udea.bancodigital.infrastructure.security;

import com.udea.bancodigital.infrastructure.security.dto.TokenValidationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private IdentityServiceClient identityServiceClient;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldSetAuthenticationWhenTokenIsValid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        
        TokenValidationResponse validationResponse = new TokenValidationResponse();
        validationResponse.setActive(true);
        validationResponse.setUid(UUID.randomUUID().toString());
        validationResponse.setClienteId(UUID.randomUUID().toString());
        validationResponse.setSub("user@test.com");
        validationResponse.setAuthorities(List.of("ROLE_USER"));
        
        when(identityServiceClient.validateToken("valid.jwt.token")).thenReturn(validationResponse);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() != null;
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthenticationWhenTokenIsInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.jwt.token");
        
        TokenValidationResponse validationResponse = new TokenValidationResponse();
        validationResponse.setActive(false);
        
        when(identityServiceClient.validateToken("invalid.jwt.token")).thenReturn(validationResponse);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Test
    void doFilterInternal_ShouldContinueChainOnException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(identityServiceClient.validateToken("valid.jwt.token")).thenThrow(new RuntimeException("Error"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Test
    void doFilterInternal_ShouldContinueChainWhenNoToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
