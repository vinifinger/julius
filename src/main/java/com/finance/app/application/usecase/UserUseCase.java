package com.finance.app.application.usecase;

import com.finance.app.domain.entity.User;
import com.finance.app.domain.exception.DuplicateEmailException;
import com.finance.app.domain.exception.UserNotFoundException;
import com.finance.app.domain.repository.UserRepository;
import com.finance.app.web.dto.request.CreateUserRequest;
import com.finance.app.web.dto.response.UserResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = User.create(request.name(), request.email(), request.password());
        User savedUser = userRepository.save(user);

        return UserResponse.fromDomain(savedUser);
    }

    public UserResponse getById(UUID id) {
        return userRepository.findById(id)
                .map(UserResponse::fromDomain)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<UserResponse> listAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromDomain)
                .toList();
    }

}