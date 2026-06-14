package com.ashwinsi.auth_service.Utils;

import org.springframework.stereotype.Component;

@Component
public class BcryptService {
    //TODO need to impelement using becrypt
    public String encrypt(String password){
        return password;
    }

    public Boolean compare(String hashPassowrd, String password){
        return true;
    }
}
