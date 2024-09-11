package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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


public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepositoryMock = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepositoryMock);
    }

    @Test
    public void testGetItems() {
        Item mockItem = createItem();
        List<Item> itemList = new ArrayList<>();
        itemList.add(mockItem);

        when(itemRepositoryMock.findAll()).thenReturn(itemList);

        ResponseEntity<List<Item>> responseEntity = itemController.getItems();

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
        Assertions.assertEquals("ItemName", responseEntity.getBody().get(0).getName());
    }

    @Test
    public void testGetItemByIdSuccess() {
        Item mockItem = createItem();

        when(itemRepositoryMock.findById(mockItem.getId())).thenReturn(Optional.of(mockItem));

        ResponseEntity<Item> responseEntity = itemController.getItemById(mockItem.getId());

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(10, responseEntity.getBody().getPrice().intValue());
    }

    @Test
    public void testGetItemByIdFail() {
        when(itemRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testGetItemsByNameSuccess() {
        List<Item> itemList = createItemList();

        when(itemRepositoryMock.findByName("ItemName")).thenReturn(itemList);

        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("ItemName");

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    public void testGetItemsByNameFail() {
        List<Item> itemList = new ArrayList<>();

        when(itemRepositoryMock.findByName("ItemName")).thenReturn(itemList);

        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("ItemName");

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("ItemName");
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setDescription("Description");
        return item;
    }

    private List<Item> createItemList() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("ItemName");
        item1.setPrice(BigDecimal.valueOf(10.00));
        item1.setDescription("Description_1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("ItemName");
        item2.setPrice(BigDecimal.valueOf(20.00));
        item2.setDescription("Description_2");

        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);

        return itemList;
    }
}
