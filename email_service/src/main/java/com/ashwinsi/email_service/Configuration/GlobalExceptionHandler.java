package com.ashwinsi.auth_service.Configuration;

import com.ashwinsi.auth_service.DTO.CustomError;
import com.ashwinsi.auth_service.Utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomError.class)
    public ResponseEntity<?> handleCustomError(CustomError customError){
        return ResponseHandler.handleResponse(customError.getHttpStatus(), null, customError.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleError(Exception e){
        return ResponseHandler.handleResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
    }

}
