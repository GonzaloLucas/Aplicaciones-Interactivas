package com.uade.tpo.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Role;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.exceptions.SelfDeletionException;
import com.uade.tpo.demo.exceptions.UserNotFoundException;
import com.uade.tpo.demo.repository.CartRepository;
import com.uade.tpo.demo.repository.OrderRepository;
import com.uade.tpo.demo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public User updateUserRole(Long userId, Role role) {
        if (role == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userId));

        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, Long requesterId) {
        // Evita que un ADMIN se borre a sí mismo y deje el sistema sin administradores
        if (userId.equals(requesterId)) {
            throw new SelfDeletionException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userId));

        // 1) Borrar su carrito (con sus ítems, por cascade/orphanRemoval): no es historial.
        cartRepository.findByUserId(userId).ifPresent(cartRepository::delete);

        // 2) Desvincular (no borrar) sus órdenes, para conservar el historial de ventas.
        orderRepository.detachUser(userId);

        // 3) Recién ahora se puede borrar el usuario sin romper ninguna FK.
        userRepository.delete(user);
    }
}
