package com.mif.movieInsideForum.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseWrapper<T> {
    private String status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;

    public ResponseWrapper(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseWrapper(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
