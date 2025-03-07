package br.edu.ifpb.instagram.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;

@SpringBootTest
public class UserServiceImplTest {

    @MockitoBean
    UserRepository userRepository; // Repositório simulado

    @Autowired
    UserServiceImpl userService; // Classe sob teste

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @Test
    void testFindById_ReturnsUserDto() {
        // Configurar o comportamento do mock
        Long userId = 1L;

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(userId);
        mockUserEntity.setFullName("Paulo Pereira");
        mockUserEntity.setEmail("paulo@ppereira.dev");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));

        // Executar o método a ser testado
        UserDto userDto = userService.findById(userId);

        // Verificar o resultado
        assertNotNull(userDto);
        assertEquals(mockUserEntity.getId(), userDto.id());
        assertEquals(mockUserEntity.getFullName(), userDto.fullName());
        assertEquals(mockUserEntity.getEmail(), userDto.email());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_ThrowsExceptionWhenUserNotFound() {
        // Configurar o comportamento do mock
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Executar e verificar a exceção
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(userId);
        });

        assertEquals("User not found", exception.getMessage());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    
    @Test
    void testCreateUser_ReturnsUserDto() {
        Long userId = 1L;
        UserEntity mockUserEntity = new UserEntity();

        mockUserEntity.setId(userId);
        mockUserEntity.setFullName("Paulo Pereira");
        mockUserEntity.setUsername("paulo123");
        mockUserEntity.setEmail("paulo@ppereira.dev");

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

        UserDto userDto = userService.createUser(
            new UserDto(null, "Paulo Pereira", "paulo123", "paulo@ppereira.dev", "teste", null)

        );
        assertNotNull(userDto);
        assertEquals(mockUserEntity.getId(), userDto.id());
        assertEquals(mockUserEntity.getFullName(), userDto.fullName());
        assertEquals(mockUserEntity.getEmail(), userDto.email());

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUserPasswordNull_ReturnUserDto() {
        Long userID = 1L;

        UserDto mockUserDto = new UserDto(userID, "Paulo Pereira", "paulo123", "paulo@ppereira.dev", null, null);

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(userID);
        mockUserEntity.setFullName("Paulo Pereira");
        mockUserEntity.setUsername("paulo123");
        mockUserEntity.setEmail("paulo@ppereira.dev");

        when(userRepository.findById(userID)).thenReturn(Optional.of(mockUserEntity));

        when(userRepository.updatePartialUser(
            "Paulo Pereira", "paulo@ppereira.dev", "paulo123", null, userID))
            .thenReturn(1);

        UserDto userDto = userService.updateUser(mockUserDto);

        assertNotNull(userDto);
        assertEquals(mockUserDto.id(), userDto.id());
        assertEquals(mockUserDto.email(), userDto.email());
        assertEquals(mockUserDto.username(), userDto.username());
        assertEquals(mockUserDto.fullName(), userDto.fullName());

        verify(userRepository, times(1)).updatePartialUser(
            "Paulo Pereira", "paulo@ppereira.dev", "paulo123", null, userID);
    }

    @Test
    void testUpdateUserPasswordNotNull_ReturnUserDto() {
        Long userID = 1L;

        // Crie um mock do UserDto com senha fornecida
        String novaSenha = "novaSenha123";
        UserDto mockUserDto = new UserDto(userID, "Paulo Pereira", "paulo123", "paulo@ppereira.dev", novaSenha, null);

        // Crie o mock do UserEntity, que será retornado pelo findById
        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(userID);
        mockUserEntity.setFullName("Paulo Pereira");
        mockUserEntity.setUsername("paulo123");
        mockUserEntity.setEmail("paulo@ppereira.dev");

        // Mock do findById, retornando o mockUserEntity dentro de um Optional
        when(userRepository.findById(userID)).thenReturn(Optional.of(mockUserEntity));

        // Mock do passwordEncoder para retornar uma senha codificada específica
        String senhaCodificada = "senhaCodificada123";
        when(passwordEncoder.encode(novaSenha)).thenReturn(senhaCodificada);

        // Mock do updatePartialUser, retornando 1 para indicar sucesso na atualização
        when(userRepository.updatePartialUser(
            "Paulo Pereira", "paulo@ppereira.dev", "paulo123", 
            senhaCodificada, userID))  // Senha codificada
            .thenReturn(1);

        // Chama o método updateUser
        UserDto userDto = userService.updateUser(mockUserDto);

        // Verificações para garantir que o método funcione como esperado
        assertNotNull(userDto);
        assertEquals(mockUserDto.id(), userDto.id());
        assertEquals(mockUserDto.email(), userDto.email());
        assertEquals(mockUserDto.username(), userDto.username());
        assertEquals(mockUserDto.fullName(), userDto.fullName());

        // Verifica se o updatePartialUser foi chamado corretamente com a senha codificada
        verify(userRepository, times(1)).updatePartialUser(
            "Paulo Pereira", "paulo@ppereira.dev", "paulo123", 
            senhaCodificada, userID);  // Verifique se a senha foi codificada
    }

}
