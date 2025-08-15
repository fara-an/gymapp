package com.epam.gymapp_gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteValidatorTest {

    private RouteValidator routeValidator;

    @BeforeEach
    void setUp() {
        routeValidator = new RouteValidator();
    }

    private ServerHttpRequest mockRequest(String path) {
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(request.getURI()).thenReturn(URI.create(path));
        return request;
    }

    @Test
    void isSecured_shouldReturnFalse_forOpenApiEndpoints() {
        for (String openEndpoint : RouteValidator.openApiEndpoints) {
            ServerHttpRequest request = mockRequest(openEndpoint);
            assertFalse(routeValidator.isSecured.test(request),
                    "Expected '" + openEndpoint + "' to be considered open");
        }
    }

    @Test
    void isSecured_shouldReturnTrue_forNonOpenEndpoints() {
        ServerHttpRequest request = mockRequest("/secure/data");
        assertTrue(routeValidator.isSecured.test(request),
                "Expected '/secure/data' to be considered secured");
    }

    @Test
    void isSecured_shouldMatchWhenPathContainsOpenEndpointSubstring() {
        ServerHttpRequest request = mockRequest("/users/login/extra");
        assertFalse(routeValidator.isSecured.test(request),
                "Expected '/users/login/extra' to be considered open because it contains '/users/login'");
    }
}
