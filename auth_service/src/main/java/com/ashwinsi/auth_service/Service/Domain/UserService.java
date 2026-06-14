package com.ashwinsi.auth_service.Service.Domain;

import com.ashwinsi.auth_service.Domain.User;
import com.ashwinsi.auth_service.Repository.UserRepository;
import com.ashwinsi.auth_service.Utils.BcryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private BcryptService bcryptService;

    public User createUser(String email, String password){
        String hashPassword = bcryptService.encrypt(password);

        User user = new User(email, hashPassword);

        return userRepository.save(user);
    }

    public boolean isUserExists(Integer id){
        return userRepository.findById(id).isPresent();
    }

    public boolean isUserExists(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public User getUser(Integer id){
        return userRepository.findById(id).get();
    }

    public User getUser(String email){
        return userRepository.findByEmail(email).get();
    }


}
