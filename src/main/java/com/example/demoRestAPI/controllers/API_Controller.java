package com.example.demoRestAPI.controllers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demoRestAPI.models.ApiModels.User;
import com.example.demoRestAPI.services.PetstoreService;

@RestController
public class API_Controller {
    private final PetstoreService petstoreService;

    public API_Controller(PetstoreService petstoreService) {
        this.petstoreService = petstoreService;
    }

    @PostMapping("/api/v3/user")
    public User createUser(@RequestBody User user) {
        return petstoreService.createUser(user);
    }

    @PostMapping("/api/v3/user/createWithList")
    public User createUsers(@RequestBody java.util.List<User> users) {
        return petstoreService.createUsers(users);
    }

    @GetMapping("/api/v3/user/login")
    public ResponseEntity<String> login(@RequestParam(required = false) String username,
                                        @RequestParam(required = false) String password) {
        String message = petstoreService.login(username, password);
        return ResponseEntity.ok()
                .header("X-Rate-Limit", "5000")
                .header("X-Expires-After", Instant.now().plus(1, ChronoUnit.HOURS).toString())
                .body(message);
    }

    @GetMapping("/api/v3/user/logout")
    public String logout() {
        return "logged out user session";
    }

    @GetMapping("/api/v3/user/{username}")
    public User getUser(@PathVariable String username) {
        return petstoreService.getUser(username);
    }

    @PutMapping("/api/v3/user/{username}")
    public ResponseEntity<Void> updateUser(@PathVariable String username, @RequestBody User user) {
        petstoreService.updateUser(username, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/v3/user/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        petstoreService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}