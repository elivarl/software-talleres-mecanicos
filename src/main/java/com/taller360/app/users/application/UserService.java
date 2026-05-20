package com.taller360.app.users.application;

import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import com.taller360.app.users.application.dto.CreateUserRequest;
import com.taller360.app.users.application.dto.UpdateUserRequest;
import com.taller360.app.users.application.dto.UserResponse;
import com.taller360.app.users.domain.User;
import com.taller360.app.users.infrastructure.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return toResponse(getUserEntity(id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        validateEmailAvailability(normalizedEmail, null);

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setActive(request.active() == null || request.active());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = getUserEntity(id);
        String normalizedEmail = normalizeEmail(request.email());
        validateEmailAvailability(normalizedEmail, id);

        user.setFullName(request.fullName().trim());
        user.setEmail(normalizedEmail);
        user.setRole(request.role());
        user.setActive(request.active());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        return toResponse(user);
    }

    @Transactional
    public UserResponse deactivate(Long id) {
        User user = getUserEntity(id);
        user.setActive(false);
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateEmailAvailability(String email, Long currentUserId) {
        userRepository.findByEmailIgnoreCase(email)
                .filter(existingUser -> !existingUser.getId().equals(currentUserId))
                .ifPresent(existingUser -> {
                    throw new BusinessRuleException("Email is already registered");
                });
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
