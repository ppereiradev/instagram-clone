package br.edu.ifpb.instagram.controller;


import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;
import br.edu.ifpb.instagram.repository.UserRepository;
import br.edu.ifpb.instagram.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testSignIn_Success() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setFullName("Eduardo Dudu");
        userEntity.setUsername("dudu_ads");
        userEntity.setEmail("davi@gmail.com");
        userEntity.setEncryptedPassword(passwordEncoder.encode("password123"));
        userRepository.save(userEntity);

        LoginRequest loginRequest = new LoginRequest("dudu_ads", "password123");

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dudu_ads"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testSignUp_Success() throws Exception {
        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(
                null, "josias@gmail.com", "password123", "Josias Teixeira", "josias_t"
        );

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists()) // Verifica se o ID foi gerado
                .andExpect(jsonPath("$.fullName").value("Josias Teixeira"))
                .andExpect(jsonPath("$.username").value("josias_t"))
                .andExpect(jsonPath("$.email").value("josias@gmail.com"));
    }

    @Test
    public void testSignIn_Failure_InvalidCredentials() throws Exception {
        UserDto userEntity = new UserDto(null, "Josias", "josiascta", "josias@gmail.com", "josias123", null);
        userService.createUser(userEntity);

        LoginRequest loginRequest = new LoginRequest("josiascta", "senha12345");

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());

    }









}

