package com.uade.tpo.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.demo.entity.Role;
import com.uade.tpo.demo.entity.User;

public interface UserService {

    Page<User> getAllUsers(Pageable pageable);

    User updateUserRole(Long userId, Role role);
}
