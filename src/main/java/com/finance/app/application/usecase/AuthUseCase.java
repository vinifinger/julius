package com.finance.app.application.usecase;

import com.finance.app.domain.entity.SocialProfile;
import com.finance.app.domain.entity.User;
import com.finance.app.domain.exception.DuplicateEmailException;
import com.finance.app.domain.exception.InvalidCredentialsException;
import com.finance.app.domain.port.TokenVerifier;
import com.finance.app.domain.repository.UserRepository;
import com.finance.app.infrastructure.security.JwtTokenProvider;
import com.finance.app.web.dto.request.GoogleAuthRequest;
import com.finance.app.web.dto.request.LoginRequest;
import com.finance.app.web.dto.request.RegisterRequest;
import com.finance.app.web.dto.response.AuthResponse;
import com.finance.app.web.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenVerifier tokenVerifier;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User user = User.create(request.name(), request.email(), hashedPassword);
        User savedUser = userRepository.save(user);

        return UserResponse.fromDomain(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId());

        return new AuthResponse(
                token,
                jwtTokenProvider.getExpiresAt(token),
                user.getId(),
                user.getEmail());
    }

    @Transactional
    public AuthResponse authenticateGoogle(GoogleAuthRequest request) {
        SocialProfile profile = tokenVerifier.verify(request.idToken());

        Optional<User> existingUser = userRepository.findByEmail(profile.email());

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            User newUser = User.createFromSocial(profile.name(), profile.email());
            user = userRepository.save(newUser);
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId());

        return new AuthResponse(
                token,
                jwtTokenProvider.getExpiresAt(token),
                user.getId(),
                user.getEmail());
    }

}
