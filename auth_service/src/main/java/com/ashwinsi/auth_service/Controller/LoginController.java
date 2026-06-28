package com.ashwinsi.auth_service.Controller;

import com.ashwinsi.auth_service.Service.LoginService;
import com.ashwinsi.auth_service.Utils.ResponseHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Data
@AllArgsConstructor
class LoginDTO{
    private String email;
    private String password;
}

@RestController
@RequestMapping("/auth/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/user")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO){
        return ResponseHandler.handleResponse(HttpStatus.OK, loginService.userLogin(loginDTO.getEmail(), loginDTO.getPassword()), "Login Success");
    }

    @PostMapping("/seller")
    public ResponseEntity<?> loginSeller(@RequestBody LoginDTO loginDTO){
        return ResponseHandler.handleResponse(HttpStatus.OK, loginService.sellerLogin(loginDTO.getEmail(), loginDTO.getPassword()), "Login Success");
    }
}
