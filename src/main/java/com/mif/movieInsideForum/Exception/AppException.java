package com.mif.movieInsideForum.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class AppException extends RuntimeException {
    private HttpStatus status;
    private String message;

}
