package com.uade.tpo.demo.controllers.dtoResponses;

import com.uade.tpo.demo.entity.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
