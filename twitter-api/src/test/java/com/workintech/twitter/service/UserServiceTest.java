package com.workintech.twitter.service;

import com.workintech.twitter.dto.RegisterRequest;
import com.workintech.twitter.dto.UserResponse;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ApiException;
import com.workintech.twitter.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @Test
    void register_encodesPasswordAndSaves() {
        when(userRepository.existsByUsername("berat")).thenReturn(false);
        when(userRepository.existsByEmail("b@b.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("HASH");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.register(new RegisterRequest("berat", "b@b.com", "123456", null, null, null));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("HASH", captor.getValue().getPassword());
        assertEquals("berat", response.username());
    }

    @Test
    void register_duplicateUsername_throws409() {
        when(userRepository.existsByUsername("berat")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class,
                () -> userService.register(new RegisterRequest("berat", "b@b.com", "123456", null, null, null)));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getById_notFound_throws404() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> userService.getById(5L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}
