package com.ashwinsi.auth_service.Controller;

import com.ashwinsi.auth_service.Service.SignupService;
import com.ashwinsi.auth_service.Utils.ResponseHandler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
class SignupDTO{
    private String email;
    private String password;
}

@RestController
@RequestMapping("/auth/signup")
public class SignupController {
    @Autowired
    private SignupService signupService;

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody SignupDTO signupDTO){
        return ResponseHandler.handleResponse(HttpStatus.OK,
                signupService.signinUser(signupDTO.getEmail(),
                        signupDTO.getPassword()), "User Created Success");
    }

    @PostMapping("/seller")
    public ResponseEntity<?> createSeller(@RequestBody SignupDTO signupDTO){
        return ResponseHandler.handleResponse(HttpStatus.OK,
                signupService.singinSeller(signupDTO.getEmail(),
                        signupDTO.getPassword()), "Seller Created Success");
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createAdmin(@RequestBody SignupDTO signupDTO){
        return ResponseHandler.handleResponse(HttpStatus.OK,
                signupService.signinAdmin(signupDTO.getEmail(),
                        signupDTO.getPassword()), "Seller Created Success");
    }
}
