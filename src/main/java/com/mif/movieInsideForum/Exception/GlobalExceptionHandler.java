package com.mif.movieInsideForum.Exception;

import com.mif.movieInsideForum.DTO.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseWrapper<Object>> handlingRuntimeException(RuntimeException ex) {
        ResponseWrapper<Object> response = ResponseWrapper.<Object>builder()
                .status("error")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleValidationException(MethodArgumentNotValidException ex) {

        ResponseWrapper<Object> response = ResponseWrapper.<Object>builder()
                .status("error")
                .message(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ResponseWrapper<Object>> handlingAppException(AppException ex) {
        ResponseWrapper<Object> response = ResponseWrapper.<Object>builder()
                .status("error")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, ex.getStatus());
    }
}
