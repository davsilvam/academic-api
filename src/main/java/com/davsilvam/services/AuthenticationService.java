package com.davsilvam.services;

import com.davsilvam.domain.user.User;
import com.davsilvam.dtos.authentication.LoginRequest;
import com.davsilvam.dtos.authentication.RegisterResponse;
import com.davsilvam.dtos.user.UserResponse;
import com.davsilvam.exceptions.user.EmailAlreadyUsedException;
import com.davsilvam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    public UserResponse login(LoginRequest request) {
        return null;
    }

    public RegisterResponse register(String name, String email, String password) throws EmailAlreadyUsedException {
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyUsedException();
        }

        User user = new User(name, email, password);
        this.userRepository.save(user);

        return new RegisterResponse(user.getId(), user.getName(), user.getEmail());
    }
}
