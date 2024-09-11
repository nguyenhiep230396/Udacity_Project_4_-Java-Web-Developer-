package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepositoryMock = mock(UserRepository.class);

    private CartRepository cartRepositoryMock = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoderMock = mock(BCryptPasswordEncoder.class);

    private static final String USERNAME_IS_EMPTY = "Username can't be empty";

    private static final String USERNAME_EXISTS = "Username already exists";

    private static final String PASSWORD_IS_INVALID = "Password is invalid";

    private static final String CONFIRM_PASSWORD_DO_NOT_MATCH = "Password and confirm password don't match";

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepositoryMock);
        TestUtils.injectObjects(userController, "cartRepository", cartRepositoryMock);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoderMock);
    }

    @Test
    public void testFindByIdSuccess() {
        User mockUser = createUser();
        when(userRepositoryMock.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        ResponseEntity<User> responseEntity = userController.findById(mockUser.getId());
        User userResponse = responseEntity.getBody();

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(1, userResponse.getId());
    }

    @Test
    public void testFindByUserNameSuccess() {
        User mockUser = createUser();
        when(userRepositoryMock.findByUsername(mockUser.getUsername())).thenReturn(mockUser);

        ResponseEntity<User> responseEntity = userController.findByUserName(mockUser.getUsername());
        User userResponse = responseEntity.getBody();

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
        Assertions.assertEquals("UserName", userResponse.getUsername());
    }

    @Test
    public void testFindByUserNameFail() {
        when(userRepositoryMock.findByUsername("")).thenReturn(null);

        ResponseEntity<User> responseEntity = userController.findByUserName("");

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testCreateUserSuccess() {
        CreateUserRequest mockCreateUserRequest = createUserRequest();

        when(userRepositoryMock.findByUsername(mockCreateUserRequest.getUsername())).thenReturn(null);

        ResponseEntity<?> responseEntity = userController.createUser(mockCreateUserRequest);
        User userResponse = (User) responseEntity.getBody();

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
        Assertions.assertEquals("UserName", userResponse.getUsername());
    }

    @Test
    public void testCreateUserFailWhenUsernameIsEmpty() {
        CreateUserRequest mockCreateUserRequest = createUserRequest();
        mockCreateUserRequest.setUsername("");

        when(userRepositoryMock.findByUsername(mockCreateUserRequest.getUsername())).thenReturn(null);

        ResponseEntity<?> responseEntity = userController.createUser(mockCreateUserRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(400, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(USERNAME_IS_EMPTY, responseEntity.getBody());
    }

    @Test
    public void testCreateUserFailWhenUsernameExists() {
        CreateUserRequest mockCreateUserRequest = createUserRequest();

        when(userRepositoryMock.findByUsername(mockCreateUserRequest.getUsername())).thenReturn(createUser());

        ResponseEntity<?> responseEntity = userController.createUser(mockCreateUserRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(400, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(USERNAME_EXISTS, responseEntity.getBody());
    }

    @Test
    public void testCreateUserFailWhenPasswordIsEmpty() {
        CreateUserRequest mockCreateUserRequest = createUserRequest();
        mockCreateUserRequest.setPassword("");

        when(userRepositoryMock.findByUsername(mockCreateUserRequest.getUsername())).thenReturn(null);

        ResponseEntity<?> responseEntity = userController.createUser(mockCreateUserRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(400, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(PASSWORD_IS_INVALID, responseEntity.getBody());
    }

    @Test
    public void testCreateUserFailWhenPasswordIsLessThan7() {
        CreateUserRequest mockCreateUserRequest = createUserRequest();
        mockCreateUserRequest.setPassword("123456");

        when(userRepositoryMock.findByUsername(mockCreateUserRequest.getUsername())).thenReturn(null);

        ResponseEntity<?> responseEntity = userController.createUser(mockCreateUserRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(400, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(PASSWORD_IS_INVALID, responseEntity.getBody());
    }

    @Test
    public void testCreateUserFailWhenConfirmPasswordIsEmpty() {
        CreateUserRequest mockCreateUserRequest = createUserRequest();
        mockCreateUserRequest.setConfirmPassword("");

        when(userRepositoryMock.findByUsername(mockCreateUserRequest.getUsername())).thenReturn(null);

        ResponseEntity<?> responseEntity = userController.createUser(mockCreateUserRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(400, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(CONFIRM_PASSWORD_DO_NOT_MATCH, responseEntity.getBody());
    }

    @Test
    public void testCreateUserFailWhenConfirmPasswordIsNotMatched() {
        CreateUserRequest mockCreateUserRequest = createUserRequest();
        mockCreateUserRequest.setConfirmPassword("P@ssw0rd_");

        when(userRepositoryMock.findByUsername(mockCreateUserRequest.getUsername())).thenReturn(null);

        ResponseEntity<?> responseEntity = userController.createUser(mockCreateUserRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(400, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(CONFIRM_PASSWORD_DO_NOT_MATCH, responseEntity.getBody());
    }

    private User createUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("UserName");
        user.setPassword("P@ssw0rd");
        user.setCart(new Cart());
        return user;
    }

    private CreateUserRequest createUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("UserName");
        createUserRequest.setPassword("P@ssw0rd");
        createUserRequest.setConfirmPassword("P@ssw0rd");
        return createUserRequest;
    }
}
