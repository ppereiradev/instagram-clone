package br.edu.ifpb.instagram.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.response.UserDetailsResponse;
import br.edu.ifpb.instagram.repository.UserRepository;
import br.edu.ifpb.instagram.service.impl.UserServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void testGetUsers() throws Exception {

        //Lista a ser retornada
        List<UserDto> usersDtos = Arrays.asList(
            new UserDto(1L, "John Doe", "johndoe", "johndoe@example.com", null, null)
        );

        // Simula o comportamento do serviço
        Mockito.when(userService.findAll()).thenReturn(usersDtos);

        //Chama o /users com o metodo GET
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()) //, verifica se o status foi ok 200
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(usersDtos.get(0).id())) // Verifica se o id do primeiro da lista é igual o de usersDtos
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullName").value(usersDtos.get(0).fullName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value(usersDtos.get(0).username()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(usersDtos.get(0).email()));

        // Verificando se findAll() foi chamado corretamente
        Mockito.verify(userService, Mockito.times(1)).findAll();
    }

    @Test
    void testGetUserById() throws Exception {
        UserDto userDto = new UserDto(1L, "John Doe", "johndoe", "johndoe@example.com", null, null);
        
        Mockito.when(userService.findById(1L)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value(userDto.fullName()));

        Mockito.verify(userService, Mockito.times(1)).findById(1L);
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto updatedUserDto = new UserDto(1L, "John Doe Updated", "johndoe", "johndoe@example.com", null, null);
        
        UserDetailsResponse response = new UserDetailsResponse(1L, "John Doe Updated", "johndoe", "johndoe@example.com");
        
        Mockito.when(userService.updateUser(updatedUserDto)).thenReturn(updatedUserDto);
        
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value(updatedUserDto.fullName()));

        Mockito.verify(userService, Mockito.times(1)).updateUser(updatedUserDto);
    }

    @Test
    void testDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(1L);
    }

}
