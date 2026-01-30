package com.inventory.inventory_backend.dto;

import com.inventory.inventory_backend.model.ERole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String email;
    private ERole role;
}
