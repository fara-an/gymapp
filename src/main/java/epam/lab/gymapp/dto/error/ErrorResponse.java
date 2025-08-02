package epam.lab.gymapp.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String instant;
    private  int statusCode;
    private String error;
    private String message;
    private String path;

}
