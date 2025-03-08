package br.edu.ifpb.instagram.repository;

import br.edu.ifpb.instagram.model.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest 
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeEach
    public void setUp() {
        
        user = new UserEntity();
        user.setUsername("testUser");
        user.setFullName("Test User");
        user.setEmail("testuser@example.com");
        user.setEncryptedPassword("testPassword");
    }


 @Test
    public void testFindById() {
       
        userRepository.save(user);
        
       
        Optional<UserEntity> foundUser = userRepository.findById(user.getId());
        
        
        assertEquals(user.getUsername(), foundUser.get().getUsername());
    }

    @Test
    public void testUpdateUser() {
       
        UserEntity savedUser = userRepository.save(user);
        
      
        savedUser.setFullName("Updated User");
        userRepository.save(savedUser);
        
        Optional<UserEntity> updatedUser = userRepository.findById(savedUser.getId());
        
        assertEquals("Updated User", updatedUser.get().getFullName());
    }

    @Test
    public void testDeleteUser() {
       
        UserEntity savedUser = userRepository.save(user);
        
      
        userRepository.delete(savedUser);
       
        Optional<UserEntity> deletedUser = userRepository.findById(savedUser.getId());
        assertEquals(Optional.empty(), deletedUser);
    }

    @Test
    public void testFindByUsername() {
       
        userRepository.save(user);
        
     
        Optional<UserEntity> foundUser = userRepository.findByUsername(user.getUsername());
        
       
        assertEquals(user.getUsername(), foundUser.get().getUsername());
    }
}
