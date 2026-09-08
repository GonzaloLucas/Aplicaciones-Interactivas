package com.uade.tpo.demo.controllers.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.controllers.dtoResponses.UserResponse;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /users?page=0&size=10 -> listado de usuarios (solo ADMIN, ver SecurityConfig)
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Page<User> users = (page == null || size == null)
                ? userService.getAllUsers(PageRequest.of(0, Integer.MAX_VALUE))
                : userService.getAllUsers(PageRequest.of(page, size));
        return ResponseEntity.ok(users.map(this::toResponse));
    }

    // PUT /users/{id}/role -> cambia el rol de un usuario existente (solo ADMIN)
    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestBody RoleUpdateRequest request) {
        User updated = userService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok(toResponse(updated));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
