package com.example.demoRestAPI.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demoRestAPI.models.ApiModels.ApiResponse;
import com.example.demoRestAPI.models.ApiModels.Order;
import com.example.demoRestAPI.models.ApiModels.Pet;
import com.example.demoRestAPI.models.ApiModels.User;

@Service
public class PetstoreService {
    private final Map<Long, Pet> pets = new ConcurrentHashMap<>();
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final AtomicLong petIds = new AtomicLong();
    private final AtomicLong orderIds = new AtomicLong();
    private final AtomicLong userIds = new AtomicLong();

    public Pet createPet(Pet pet) {
        validatePet(pet);
        Long id = pet.id();
        if (id == null) {
            id = petIds.incrementAndGet();
        } else {
            petIds.accumulateAndGet(id, Math::max);
        }
        Pet savedPet = new Pet(id, pet.name(), pet.category(), List.copyOf(pet.photoUrls()),
                pet.tags() == null ? List.of() : List.copyOf(pet.tags()), pet.status());
        pets.put(id, savedPet);
        return savedPet;
    }

    public Pet updatePet(Pet pet) {
        validatePet(pet);
        if (pet.id() == null || !pets.containsKey(pet.id())) {
            throw notFound("Pet not found");
        }
        pets.put(pet.id(), new Pet(pet.id(), pet.name(), pet.category(), List.copyOf(pet.photoUrls()),
                pet.tags() == null ? List.of() : List.copyOf(pet.tags()), pet.status()));
        return pets.get(pet.id());
    }

    public List<Pet> findPetsByStatus(List<String> statuses) {
        return pets.values().stream()
                .filter(pet -> statuses.contains(pet.status()))
                .toList();
    }

    public List<Pet> findPetsByTags(List<String> tags) {
        return pets.values().stream()
                .filter(pet -> pet.tags() != null && pet.tags().stream().anyMatch(tag -> tags.contains(tag.name())))
                .toList();
    }

    public Pet getPet(long id) {
        Pet pet = pets.get(id);
        if (pet == null) {
            throw notFound("Pet not found");
        }
        return pet;
    }

    public Pet updatePet(long id, String name, String status) {
        Pet current = getPet(id);
        Pet updated = new Pet(current.id(), name == null ? current.name() : name, current.category(),
                current.photoUrls(), current.tags(), status == null ? current.status() : status);
        pets.put(id, updated);
        return updated;
    }

    public void deletePet(long id) {
        if (pets.remove(id) == null) {
            throw notFound("Pet not found");
        }
    }

    public ApiResponse uploadPetImage(long id, String metadata, String filename) {
        getPet(id);
        String message = filename == null ? "Image uploaded" : "Image uploaded: " + filename;
        if (metadata != null && !metadata.isBlank()) {
            message += " (" + metadata + ")";
        }
        return new ApiResponse(200, "unknown", message);
    }

    public Map<String, Integer> getInventory() {
        Map<String, Integer> inventory = new LinkedHashMap<>();
        inventory.put("available", 0);
        inventory.put("pending", 0);
        inventory.put("sold", 0);
        pets.values().forEach(pet -> {
            if (inventory.containsKey(pet.status())) {
                inventory.compute(pet.status(), (status, count) -> count + 1);
            }
        });
        return inventory;
    }

    public Order placeOrder(Order order) {
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is required");
        }
        Long id = order.id();
        if (id == null) {
            id = orderIds.incrementAndGet();
        } else {
            orderIds.accumulateAndGet(id, Math::max);
        }
        Order savedOrder = new Order(id, order.petId(), order.quantity(), order.shipDate(),
                order.status() == null ? "placed" : order.status(),
                order.complete() == null ? Boolean.FALSE : order.complete());
        orders.put(id, savedOrder);
        return savedOrder;
    }

    public Order getOrder(long id) {
        Order order = orders.get(id);
        if (order == null) {
            throw notFound("Order not found");
        }
        return order;
    }

    public void deleteOrder(long id) {
        if (orders.remove(id) == null) {
            throw notFound("Order not found");
        }
    }

    public User createUser(User user) {
        if (user == null || user.username() == null || user.username().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        Long id = user.id();
        if (id == null) {
            id = userIds.incrementAndGet();
        } else {
            userIds.accumulateAndGet(id, Math::max);
        }
        User savedUser = new User(id, user.username(), user.firstName(), user.lastName(), user.email(),
                user.password(), user.phone(), user.userStatus());
        users.put(savedUser.username(), savedUser);
        return savedUser;
    }

    public User createUsers(List<User> usersToCreate) {
        if (usersToCreate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User list is required");
        }
        List<User> createdUsers = new ArrayList<>();
        usersToCreate.forEach(user -> createdUsers.add(createUser(user)));
        if (createdUsers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one user is required");
        }
        return createdUsers.getFirst();
    }

    public User getUser(String username) {
        User user = users.get(username);
        if (user == null) {
            throw notFound("User not found");
        }
        return user;
    }

    public void updateUser(String username, User user) {
        if (!users.containsKey(username)) {
            throw notFound("User not found");
        }
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is required");
        }
        User updated = new User(user.id() == null ? users.get(username).id() : user.id(), username,
                user.firstName(), user.lastName(), user.email(), user.password(), user.phone(), user.userStatus());
        users.remove(username);
        users.put(updated.username(), updated);
    }

    public void deleteUser(String username) {
        if (users.remove(username) == null) {
            throw notFound("User not found");
        }
    }

    public String login(String username, String password) {
        User user = users.get(username);
        if (user == null || !java.util.Objects.equals(user.password(), password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username/password supplied");
        }
        return "logged in user session: " + username;
    }

    private void validatePet(Pet pet) {
        if (pet == null || pet.name() == null || pet.name().isBlank() || pet.photoUrls() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet name and photoUrls are required");
        }
        if (pet.status() != null && !List.of("available", "pending", "sold").contains(pet.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet status");
        }
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}