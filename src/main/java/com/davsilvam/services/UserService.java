package com.davsilvam.services;

import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.user.RegisterUserRequest;
import com.davsilvam.dtos.user.UserResponse;
import com.davsilvam.exceptions.user.EmailAlreadyUsedException;
import com.davsilvam.exceptions.user.UserNotFoundException;
import com.davsilvam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public UserResponse get(UUID id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public UserResponse create(RegisterUserRequest request) throws EmailAlreadyUsedException {
        User user;

        user = userRepository.findByEmail(request.email());

        if (user != null) {
            throw new EmailAlreadyUsedException();
        }

        user = new User(request.name(), request.email(), request.password());
        userRepository.save(user);

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
