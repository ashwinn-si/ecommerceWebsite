package com.ashwinsi.email_service.DTO;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomError extends RuntimeException{
    private HttpStatus httpStatus;

    public CustomError(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getStatusCode(){
        return this.httpStatus;
    }
}
