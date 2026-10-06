package com.example.demoRestAPI.models;

import java.util.List;

public final class ApiModels {
    private ApiModels() {
    }

    public record Category(Long id, String name) {
    }

    public record Tag(Long id, String name) {
    }

    public record Pet(
            Long id,
            String name,
            Category category,
            List<String> photoUrls,
            List<Tag> tags,
            String status) {
    }

    public record Order(
            Long id,
            Long petId,
            Integer quantity,
            String shipDate,
            String status,
            Boolean complete) {
    }

    public record User(
            Long id,
            String username,
            String firstName,
            String lastName,
            String email,
            String password,
            String phone,
            Integer userStatus) {
    }

    public record ApiResponse(Integer code, String type, String message) {
    }
}