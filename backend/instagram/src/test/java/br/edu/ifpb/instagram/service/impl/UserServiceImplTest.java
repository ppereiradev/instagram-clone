package br.edu.ifpb.instagram.service.impl;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(PER_METHOD)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findById_WithNoExistingUserEntity_ThrowsRuntimeException() {
        var id = 1L;
        var sut = catchThrowable(() -> userService.findById(id));
        assertThat(sut)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

    }

    @Test
    void findById_WithExistingUser_ReturnsUserEntity() {
        var id = 1L;
        var userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setEmail("userEntity@mail.com");
        userEntity.setFullName("The user entity name");
        userEntity.setEncryptedPassword("userPass");
        ArgumentCaptor<Long> userId = ArgumentCaptor.forClass(Long.class);
        when(userRepository.findById(userId.capture())).thenReturn(Optional.of(userEntity));

        var sut = userService.findById(id);

        assertNotNull(sut);
        assertInstanceOf(UserDto.class, sut,"Must be instance of UserDto");
        assertEquals(userEntity.getEmail(), sut.email());
        assertEquals(id, userId.getValue());
    }
}
