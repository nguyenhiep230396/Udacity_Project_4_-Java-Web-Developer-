package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Optional;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private CartRepository cartRepositoryMock = mock(CartRepository.class);

    private UserRepository userRepositoryMock = mock(UserRepository.class);

    private ItemRepository itemRepositoryMock = mock(ItemRepository.class);


    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepositoryMock);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepositoryMock);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepositoryMock);
    }

    @Test
    public void testAddItemToCartSuccess() {
        User mockUser = createUser();
        Item mockItem = createItem();

        when(userRepositoryMock.findByUsername(mockUser.getUsername())).thenReturn(mockUser);
        when(itemRepositoryMock.findById(mockItem.getId())).thenReturn(Optional.of(mockItem));

        ModifyCartRequest modifyCartRequest = createModifyCartRequest();
        modifyCartRequest.setUsername(mockUser.getUsername());

        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        Cart cartResponse = responseEntity.getBody();

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
        Assertions.assertNotNull(cartResponse);
        Assertions.assertEquals("Description", cartResponse.getItems().get(0).getDescription());
    }

    @Test
    public void testAddItemToCartFailWhenUserNotFound() {
        Item mockItem = createItem();

        when(userRepositoryMock.findByUsername("")).thenReturn(null);
        when(itemRepositoryMock.findById(mockItem.getId())).thenReturn(Optional.of(mockItem));

        ModifyCartRequest modifyCartRequest = createModifyCartRequest();
        modifyCartRequest.setUsername("");

        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testAddItemToCartFailWhenItemNotFound() {
        User mockUser = createUser();

        when(userRepositoryMock.findByUsername(mockUser.getUsername())).thenReturn(mockUser);
        when(itemRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = createModifyCartRequest();
        modifyCartRequest.setUsername(mockUser.getUsername());

        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testRemoveItemFromCartSuccess() {
        User mockUser = createUser();
        Item mockItem = createItem();

        when(userRepositoryMock.findByUsername(mockUser.getUsername())).thenReturn(mockUser);
        when(itemRepositoryMock.findById(mockItem.getId())).thenReturn(Optional.of(mockItem));

        ModifyCartRequest modifyCartRequest = createModifyCartRequest();
        modifyCartRequest.setUsername(mockUser.getUsername());

        cartController.addTocart(modifyCartRequest);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testRemoveItemFromCartFailWhenUserNotFound() {
        Item mockItem = createItem();

        when(userRepositoryMock.findByUsername("")).thenReturn(null);
        when(itemRepositoryMock.findById(mockItem.getId())).thenReturn(Optional.of(mockItem));

        ModifyCartRequest modifyCartRequest = createModifyCartRequest();
        modifyCartRequest.setUsername("");

        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testRemoveItemFromCartFailWhenItemNotFound() {
        User mockUser = createUser();

        when(userRepositoryMock.findByUsername(mockUser.getUsername())).thenReturn(mockUser);
        when(itemRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = createModifyCartRequest();
        modifyCartRequest.setUsername(mockUser.getUsername());

        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    private User createUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("UserName");
        user.setPassword("P@ssw0rd");
        user.setCart(new Cart());
        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("ItemName");
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setDescription("Description");
        return item;
    }

    private ModifyCartRequest createModifyCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        return modifyCartRequest;
    }
}
