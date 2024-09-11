package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private UserRepository userRepositoryMock = mock(UserRepository.class);

    private OrderRepository orderRepositoryMock = mock(OrderRepository.class);


    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepositoryMock);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepositoryMock);
    }

    @Test
    public void testSubmitSucess() {
        User mockUser = createUser();
        Cart mockCart = createCart();
        mockUser.setCart(mockCart);

        when(userRepositoryMock.findByUsername(mockUser.getUsername())).thenReturn(mockUser);

        ResponseEntity<UserOrder> responseEntity = orderController.submit(mockUser.getUsername());

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testSubmitFailWhenUserNotFound() {
        when(userRepositoryMock.findByUsername("")).thenReturn(null);

        ResponseEntity<UserOrder> responseEntity = orderController.submit("");

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUserSuccess() {
        User mockUser = createUser();
        UserOrder mockUserOrder = createUserOrder();

        when(userRepositoryMock.findByUsername(mockUser.getUsername())).thenReturn(mockUser);
        when(orderRepositoryMock.findById(1L)).thenReturn(Optional.of(mockUserOrder));

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(mockUser.getUsername());

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUserFailWhenUserNotFound() {
        when(userRepositoryMock.findByUsername("")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(404, response.getStatusCodeValue());
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

    private Cart createCart() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(createItem());

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setTotal(BigDecimal.valueOf(10.00));
        cart.setItems(itemList);
        return cart;
    }

    private UserOrder createUserOrder() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(createItem());

        UserOrder userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder.setItems(itemList);
        userOrder.setUser(createUser());
        userOrder.setTotal(BigDecimal.valueOf(10.00));
        return userOrder;
    }
}
