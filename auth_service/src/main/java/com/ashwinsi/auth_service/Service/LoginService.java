package com.ashwinsi.auth_service.Service;

import com.ashwinsi.auth_service.DTO.CustomError;
import com.ashwinsi.auth_service.DTO.SellerJwtData;
import com.ashwinsi.auth_service.DTO.UserJwtData;
import com.ashwinsi.auth_service.Domain.Seller;
import com.ashwinsi.auth_service.Domain.User;
import com.ashwinsi.auth_service.Service.Domain.SellerService;
import com.ashwinsi.auth_service.Service.Domain.UserService;
import com.ashwinsi.auth_service.Utils.BcryptService;
import com.ashwinsi.auth_service.Utils.JwtService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Data
@AllArgsConstructor
class LoginDataDTO{
    private String jwtToken;
}

@Service
public class LoginService {
    @Autowired
    private SellerService sellerService;
    private UserService userService;
    private JwtService jwtService;
    private BcryptService bcryptService;


    public LoginDataDTO userLogin(String email, String password) {
        if(!userService.isUserExists(email)){
            throw  new CustomError("User Not Found", HttpStatus.NOT_FOUND);
        }

        User user = userService.getUser(email);

        if(!bcryptService.compare(user.getPassword(), password)){
            throw  new CustomError("Invalid Password", HttpStatus.CONFLICT);
        }

        String jwtToken =  jwtService.generateUserToken(new UserJwtData(user.getId(), user.getEmail()));
        return new LoginDataDTO(jwtToken);
    }

    public LoginDataDTO sellerLogin(String email, String password) {
        if(!userService.isUserExists(email)){
            throw  new CustomError("Seller Not Found", HttpStatus.NOT_FOUND);
        }

        Seller seller = sellerService.getSeller(email);

        if(!bcryptService.compare(seller.getPassword(), password)){
            throw  new CustomError("Invalid Password", HttpStatus.CONFLICT);
        }

        String jwtToken = jwtService.generateSellerToken(new SellerJwtData(seller.getId(), seller.getEmail(), seller.isAdmin()));

        return new LoginDataDTO(jwtToken);
    }
}
