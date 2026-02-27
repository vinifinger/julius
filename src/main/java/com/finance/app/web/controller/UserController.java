package com.finance.app.web.controller;

import com.finance.app.application.usecase.UserUseCase;
import com.finance.app.web.dto.request.CreateUserRequest;
import com.finance.app.web.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse response = userUseCase.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<UserResponse> response = userUseCase.listAll();
        return ResponseEntity.ok(response);
    }

}
