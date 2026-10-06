package com.example.demoRestAPI.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoRestAPI.models.ApiModels.Order;
import com.example.demoRestAPI.services.PetstoreService;

@RestController
public class StoreController {
    private final PetstoreService petstoreService;

    public StoreController(PetstoreService petstoreService) {
        this.petstoreService = petstoreService;
    }

    @GetMapping("/api/v3/store/inventory")
    public Map<String, Integer> inventory() {
        return petstoreService.getInventory();
    }

    @PostMapping("/api/v3/store/order")
    public Order placeOrder(@RequestBody Order order) {
        return petstoreService.placeOrder(order);
    }

    @GetMapping("/api/v3/store/order/{orderId}")
    public Order getOrder(@PathVariable long orderId) {
        return petstoreService.getOrder(orderId);
    }

    @DeleteMapping("/api/v3/store/order/{orderId}")
    public void deleteOrder(@PathVariable long orderId) {
        petstoreService.deleteOrder(orderId);
    }
}