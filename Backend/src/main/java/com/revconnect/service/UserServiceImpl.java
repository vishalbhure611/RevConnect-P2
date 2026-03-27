package com.revconnect.service;

import com.revconnect.dto.UpdateAccountRequestDTO;
import com.revconnect.entity.Role;
import com.revconnect.entity.User;
import com.revconnect.entity.Privacy;
import com.revconnect.exception.ConflictException;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.repository.ProfileRepository;
import com.revconnect.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    @Override
    public User createUser(User user) {

        if (user.getRole() == null) {
            user.setRole(Role.PERSONAL);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        //  Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        String q = keyword.trim();

        List<User> byUsername = userRepository.findByUsernameContainingIgnoreCase(q);
        List<User> byName = profileRepository.findByFullNameContainingIgnoreCase(q)
                .stream()
                .map(p -> p.getUser())
                .toList();

        return java.util.stream.Stream.concat(byUsername.stream(), byName.stream())
                .collect(java.util.stream.Collectors.toMap(
                        User::getId,
                        u -> u,
                        (a, b) -> a
                ))
                .values()
                .stream()
                .toList();
    }

    @Override
    public User updateUser(Long userId, User updatedUser) {

        User user = getUserById(userId);

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());

        return userRepository.save(user);
    }

    @Override
    public User updateAccount(Long userId, UpdateAccountRequestDTO request) {

        User user = getUserById(userId);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            String username = request.getUsername().trim();
            if (!username.equalsIgnoreCase(user.getUsername())
                    && userRepository.existsByUsername(username)) {
                throw new ConflictException("Username already exists");
            }
            user.setUsername(username);
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = request.getEmail().trim();
            if (!email.equalsIgnoreCase(user.getEmail())
                    && userRepository.existsByEmail(email)) {
                throw new ConflictException("Email already exists");
            }
            user.setEmail(email);
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User findByEmailOrUsername(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new ResourceNotFoundException("User not found");
        }

        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void updatePrivacy(Long userId, Privacy privacy) {
        User user = getUserById(userId);
        user.setPrivacy(privacy == null ? user.getPrivacy() : privacy);
        userRepository.save(user);
    }
}
