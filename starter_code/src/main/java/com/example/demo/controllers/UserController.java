package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Log4j
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String USERNAME_IS_EMPTY = "Username can't be empty";

    private static final String USERNAME_EXISTS = "Username already exists";

    private static final String PASSWORD_IS_INVALID = "Password is invalid";

    private static final String CONFIRM_PASSWORD_DO_NOT_MATCH = "Password and confirm password don't match";

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
//        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
        if (user == null) {
            log.info("User not found");
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(user);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        // check if username is empty
        if (createUserRequest.getUsername() == null || createUserRequest.getUsername().isEmpty()) {
            log.info(USERNAME_IS_EMPTY);
            return ResponseEntity.badRequest().body(USERNAME_IS_EMPTY);
        }

        // check if username exists
        if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
            log.info(USERNAME_EXISTS);
            return ResponseEntity.badRequest().body(USERNAME_EXISTS);
        }

        // check if password is empty or less than 7 characters
        if (createUserRequest.getPassword() == null || createUserRequest.getPassword().isEmpty() || createUserRequest.getPassword().length() < 7) {
            log.info(PASSWORD_IS_INVALID);
            return ResponseEntity.badRequest().body(PASSWORD_IS_INVALID);
        }

        // check if confirm password is empty or matched with password
        if (createUserRequest.getConfirmPassword().isEmpty() || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.info(CONFIRM_PASSWORD_DO_NOT_MATCH);
            return ResponseEntity.badRequest().body(CONFIRM_PASSWORD_DO_NOT_MATCH);
        }

        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

}
