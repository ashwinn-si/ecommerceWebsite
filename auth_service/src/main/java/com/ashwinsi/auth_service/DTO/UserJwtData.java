package com.ashwinsi.auth_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserJwtData {
    private Long id;
    private String email;
}
