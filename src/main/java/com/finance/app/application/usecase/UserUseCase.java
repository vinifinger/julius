package com.finance.app.application.usecase;

import com.finance.app.domain.exception.UserNotFoundException;
import com.finance.app.domain.repository.UserRepository;
import com.finance.app.web.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

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