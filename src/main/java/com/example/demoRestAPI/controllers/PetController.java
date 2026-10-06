package com.example.demoRestAPI.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demoRestAPI.models.ApiModels.ApiResponse;
import com.example.demoRestAPI.models.ApiModels.Pet;
import com.example.demoRestAPI.services.PetstoreService;

@RestController
public class PetController {
    private final PetstoreService petstoreService;

    public PetController(PetstoreService petstoreService) {
        this.petstoreService = petstoreService;
    }

    @PostMapping("/api/v3/pet")
    public Pet addPet(@RequestBody Pet pet) {
        return petstoreService.createPet(pet);
    }

    @PutMapping("/api/v3/pet")
    public Pet updatePet(@RequestBody Pet pet) {
        return petstoreService.updatePet(pet);
    }

    @GetMapping("/api/v3/pet/findByStatus")
    public List<Pet> findByStatus(@RequestParam(defaultValue = "available") List<String> status) {
        return petstoreService.findPetsByStatus(status);
    }

    @GetMapping("/api/v3/pet/findByTags")
    public List<Pet> findByTags(@RequestParam List<String> tags) {
        return petstoreService.findPetsByTags(tags);
    }

    @GetMapping("/api/v3/pet/{petId}")
    public Pet getPet(@PathVariable long petId) {
        return petstoreService.getPet(petId);
    }

    @PostMapping("/api/v3/pet/{petId}")
    public Pet updatePetWithForm(@PathVariable long petId,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String status) {
        return petstoreService.updatePet(petId, name, status);
    }

    @DeleteMapping("/api/v3/pet/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable long petId) {
        petstoreService.deletePet(petId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/api/v3/pet/{petId}/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse uploadMultipartImage(@PathVariable long petId,
                                            @RequestParam(required = false) String additionalMetadata,
                                            @RequestPart(required = false) MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "No file uploaded");
        }
        String filename = file.getOriginalFilename();
        return petstoreService.uploadPetImage(petId, additionalMetadata, filename);
    }

    @PostMapping(value = "/api/v3/pet/{petId}/uploadImage", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ApiResponse uploadBinaryImage(@PathVariable long petId,
                                         @RequestParam(required = false) String additionalMetadata,
                                         @RequestBody(required = false) byte[] file) {
        if (file == null || file.length == 0) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "No file uploaded");
        }
        return petstoreService.uploadPetImage(petId, additionalMetadata,
            "upload");
    }
}