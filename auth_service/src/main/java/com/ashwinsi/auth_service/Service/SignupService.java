package com.ashwinsi.auth_service.Service;

import com.ashwinsi.auth_service.DTO.CustomError;
import com.ashwinsi.auth_service.Domain.Seller;
import com.ashwinsi.auth_service.Domain.User;
import com.ashwinsi.auth_service.Service.Domain.SellerService;
import com.ashwinsi.auth_service.Service.Domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SignupService {
    @Autowired
    private SellerService sellerService;
    private UserService userService;

    public User signinUser(String email, String password){

        if(userService.isUserExists(email)){
            throw new CustomError("User already Exists", HttpStatus.CONFLICT);
        }

        return userService.createUser(email, password);
    }

    public Seller singinSeller(String email, String password){

        if(sellerService.isSellerExists(email)){
            throw new CustomError("Seller already Exists", HttpStatus.CONFLICT);
        }

        return sellerService.createSeller(email, password);
    }

    public Seller signinAdmin(String email, String password){

        if(sellerService.isSellerExists(email)){
            throw new CustomError("Admin already Exists", HttpStatus.CONFLICT);
        }

        return sellerService.createAdmin(email, password);
    }


}
