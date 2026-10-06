package com.example.demoRestAPI.services;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demoRestAPI.models.ApiModels.Category;
import com.example.demoRestAPI.models.ApiModels.Order;
import com.example.demoRestAPI.models.ApiModels.Pet;
import com.example.demoRestAPI.models.ApiModels.Tag;
import com.example.demoRestAPI.models.ApiModels.User;

class PetstoreServiceTests {
    private final PetstoreService service = new PetstoreService();

    @Test
    void petCanBeCreatedFilteredUpdatedAndDeleted() {
        Pet created = service.createPet(new Pet(null, "Milo", new Category(1L, "dogs"),
                List.of("milo.jpg"), List.of(new Tag(1L, "friendly")), "available"));

        assertNotNull(created.id());
        assertEquals(List.of(created), service.findPetsByStatus(List.of("available")));
        assertEquals(List.of(created), service.findPetsByTags(List.of("friendly")));
        assertEquals(1, service.getInventory().get("available"));

        Pet updated = service.updatePet(created.id(), "Milo II", "sold");
        assertEquals("Milo II", updated.name());
        assertEquals(1, service.getInventory().get("sold"));

        service.deletePet(created.id());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> service.getPet(created.id()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void ordersAndUsersCanBeCreatedAndLookedUp() {
        Order order = service.placeOrder(new Order(null, 4L, 1, null, null, null));
        assertNotNull(service.getOrder(order.id()));

        User user = service.createUser(new User(null, "milo", "Milo", "Dog", "milo@example.test",
                "secret", "555-0100", 1));
        assertEquals(user, service.getUser("milo"));
        assertEquals("logged in user session: milo", service.login("milo", "secret"));
    }
}