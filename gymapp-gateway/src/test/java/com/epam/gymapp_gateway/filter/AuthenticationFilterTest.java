package com.epam.gymapp_gateway.filter;

import com.epam.gymapp_gateway.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationFilterTest {

    private RouteValidator routeValidator;
    private AuthenticationFilter authenticationFilter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        routeValidator = new RouteValidator();
        authenticationFilter = new AuthenticationFilter(routeValidator);
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void openEndpoint_shouldNotRequireAuthHeader() {
        routeValidator.isSecured = req -> false;

        MockServerHttpRequest request = MockServerHttpRequest.get("/open").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        assertDoesNotThrow(() -> authenticationFilter.apply(new AuthenticationFilter.Config())
                .filter(exchange, chain).block());

        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void securedEndpoint_missingAuthHeader_shouldThrow() {
        routeValidator.isSecured = req -> true;

        MockServerHttpRequest request = MockServerHttpRequest.get("/secure").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                authenticationFilter.apply(new AuthenticationFilter.Config())
                        .filter(exchange, chain).block()
        );

        assertEquals("missing authorization header", ex.getMessage());
        verify(chain, never()).filter(exchange);
    }

    @Test
    void securedEndpoint_withValidBearerToken_shouldCallValidateToken() {
        routeValidator.isSecured = req -> true;

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken("valid-token")).thenAnswer(invocation -> null);

            MockServerHttpRequest request = MockServerHttpRequest.get("/secure")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .build();
            MockServerWebExchange exchange = MockServerWebExchange.from(request);

            authenticationFilter.apply(new AuthenticationFilter.Config())
                    .filter(exchange, chain).block();

            jwtUtilMock.verify(() -> JwtUtil.validateToken("valid-token"), times(1));
            verify(chain, times(1)).filter(exchange);
        }
    }

    @Test
    void securedEndpoint_withInvalidToken_shouldPropagateException() {
        routeValidator.isSecured = req -> true;

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.validateToken("bad-token"))
                    .thenThrow(new RuntimeException("invalid token"));

            MockServerHttpRequest request = MockServerHttpRequest.get("/secure")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer bad-token")
                    .build();
            MockServerWebExchange exchange = MockServerWebExchange.from(request);

            RuntimeException ex = assertThrows(RuntimeException.class, () ->
                    authenticationFilter.apply(new AuthenticationFilter.Config())
                            .filter(exchange, chain).block()
            );

            assertEquals("invalid token", ex.getMessage());
            jwtUtilMock.verify(() -> JwtUtil.validateToken("bad-token"), times(1));
            verify(chain, never()).filter(exchange);
        }
    }
}
