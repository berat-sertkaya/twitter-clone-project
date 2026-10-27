package com.workintech.twitter.service;

import com.workintech.twitter.dto.RegisterRequest;
import com.workintech.twitter.dto.UserResponse;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ApiException;
import com.workintech.twitter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ApiException("Bu kullanıcı adı zaten alınmış", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException("Bu email zaten kayıtlı", HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setBirthday(request.birthday());
        user.setLocation(request.location());
        user.setBio(request.bio());
        return toResponse(userRepository.save(user));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı: " + id, HttpStatus.NOT_FOUND));
    }

    public UserResponse me(String username) {
        return toResponse(getByUsername(username));
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
