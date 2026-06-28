package com.ashwinsi.email_service.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
class ResponseData <T>{
    private HttpStatus httpStatus;
    private String message;
    private T data;
}


public class ResponseHandler {
    public static <T> ResponseEntity<ResponseData<T>> handleResponse  (HttpStatus httpStatus, T data, String message){
        return ResponseEntity.status(httpStatus).body(new ResponseData(httpStatus, message, data));
    }
}
