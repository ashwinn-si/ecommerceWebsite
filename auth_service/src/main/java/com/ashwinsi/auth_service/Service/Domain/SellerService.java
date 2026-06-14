package com.ashwinsi.auth_service.Service.Domain;

import com.ashwinsi.auth_service.Domain.Seller;
import com.ashwinsi.auth_service.Repository.SellerRepository;
import com.ashwinsi.auth_service.Utils.BcryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;
    private BcryptService bcryptService;

    public boolean isSellerExists(Integer id){
        return sellerRepository.findById(id).isPresent();
    }

    public boolean isSellerExists(String email){
        return sellerRepository.findByEmail(email).isPresent();
    }

    public Seller getSeller(Integer id){
        return sellerRepository.findById(id).get();
    }

    public Seller getSeller(String email){
        return sellerRepository.findByEmail(email).get();
    }

    public Seller createSeller(String email, String password){
        String hashPassword = bcryptService.encrypt(password);

        Seller seller = new Seller(email, hashPassword, false);

        return sellerRepository.save(seller);
    }

    public Seller createAdmin(String email, String password){
        String hashPassword = bcryptService.encrypt(password);

        Seller seller = new Seller(email, hashPassword, true);

        return sellerRepository.save(seller);
    }


}
