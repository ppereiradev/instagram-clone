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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Test
    void shouldCreateUserSuccessfully() {
        var id = 1L;

        var userDto = new UserDto(id,
                "The user entity name",
                "User Entity",
                "userEntity@mail.com",
                "userPass",
                passwordEncoder.encode("userPass"));

        UserEntity mockedUserEntity = new UserEntity();
        mockedUserEntity.setId(id);
        mockedUserEntity.setUsername("User Entity");
        mockedUserEntity.setFullName("The user entity name");
        mockedUserEntity.setEmail("userEntity@mail.com");
        mockedUserEntity.setEncryptedPassword(passwordEncoder.encode("userPass"));

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockedUserEntity);

        UserDto createdUserDto = userService.createUser(userDto);

        assertNotNull(createdUserDto);
        assertEquals(userDto.id(), createdUserDto.id());
        assertEquals(userDto.fullName(), createdUserDto.fullName());
        assertEquals(userDto.email(), createdUserDto.email());
        assertNull(createdUserDto.password());
        assertNull(createdUserDto.encryptedPassword());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        var id = 1L;

        var originalUserDto = new UserDto(id,
                "The user entity name",
                "User Entity",
                "userEntity@mail.com",
                "userPass",
                passwordEncoder.encode("userPass"));

        var mockedUserEntity = new UserEntity();
        mockedUserEntity.setId(id);
        mockedUserEntity.setUsername("User Entity");
        mockedUserEntity.setFullName("The user entity name");
        mockedUserEntity.setEmail("userEntity@mail.com");
        mockedUserEntity.setEncryptedPassword(passwordEncoder.encode("userPass"));

        var updatedUserDto = new UserDto(id,
                "Updated user entity name",
                "User Entity",
                "userEntity@mail.com",
                "userPass",
                passwordEncoder.encode("userPass"));

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockedUserEntity);
        userService.createUser(originalUserDto);

        when(userRepository.updatePartialUser(
                updatedUserDto.fullName(),
                updatedUserDto.email(),
                updatedUserDto.username(),
                updatedUserDto.encryptedPassword(),
                updatedUserDto.id()
        )).thenReturn(1);

        UserDto returnedUserDto = userService.updateUser(updatedUserDto);

        assertNotNull(returnedUserDto);
        assertEquals(updatedUserDto.id(), returnedUserDto.id());
        assertEquals(updatedUserDto.fullName(), returnedUserDto.fullName());
        assertEquals(updatedUserDto.email(), returnedUserDto.email());
        assertEquals(updatedUserDto.username(), returnedUserDto.username());
        assertNull(returnedUserDto.password());
        assertNull(returnedUserDto.encryptedPassword());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundDuringUpdate() {
        var id = 1L;

        var updatedUserDto = new UserDto(id,
                "Updated user entity name",
                "User Entity",
                "userEntity@mail.com",
                "userPass",
                passwordEncoder.encode("userPass"));

        when(userRepository.updatePartialUser(
                updatedUserDto.fullName(),
                updatedUserDto.email(),
                updatedUserDto.username(),
                updatedUserDto.encryptedPassword(),
                updatedUserDto.id()
        )).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(updatedUserDto));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        var id = 1L;
        doNothing().when(userRepository).deleteById(id);
        userService.deleteUser(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeleteFails() {
        var id = 1L;
        doThrow(new RuntimeException("User not found")).when(userRepository).deleteById(id);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(id));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldReturnListOfUserDtosWhenUsersExist() {
        var id = 1L;
        var userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setFullName("John Doe");
        userEntity.setUsername("john");
        userEntity.setEmail("john@example.com");
        userEntity.setEncryptedPassword("encryptedPassword");

        when(userRepository.findAll()).thenReturn(List.of(userEntity));

        var result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).fullName());
        assertEquals("john", result.get(0).username());
        assertEquals("john@example.com", result.get(0).email());
    }

    @Test
    void shouldThrowExceptionWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.findAll());

        assertEquals("Users not found", exception.getMessage());
    }
}
