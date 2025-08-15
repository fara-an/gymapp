package com.epam.gymapp_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MessageResponse {
    private String message;
}