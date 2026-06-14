package com.ashwinsi.auth_service.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public String encrypt(String password){
        return bCryptPasswordEncoder.encode(password);
    }

    public Boolean compare(String hashPassowrd, String password){
        return bCryptPasswordEncoder.matches(hashPassowrd, password);
    }
}
