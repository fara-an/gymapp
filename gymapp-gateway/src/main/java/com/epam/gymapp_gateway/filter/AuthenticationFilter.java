package com.epam.gymapp_gateway.filter;

import com.epam.gymapp_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator routeValidator;

    public AuthenticationFilter(RouteValidator routeValidator) {
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                JwtUtil.validateToken(authHeader);
            }
            return chain.filter(exchange);
        });

    }


    public static class Config {}
}
