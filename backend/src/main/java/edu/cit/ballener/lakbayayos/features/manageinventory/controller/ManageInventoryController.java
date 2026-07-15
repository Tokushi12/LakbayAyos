package edu.cit.ballener.lakbayayos.features.manageinventory.controller;

import edu.cit.ballener.lakbayayos.features.manageinventory.dto.PartRequest;
import edu.cit.ballener.lakbayayos.features.manageinventory.dto.PartResponse;
import edu.cit.ballener.lakbayayos.features.manageinventory.service.ManageInventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parts")
public class ManageInventoryController {

    private final ManageInventoryService manageInventoryService;

    @Autowired
    public ManageInventoryController(ManageInventoryService manageInventoryService) {
        this.manageInventoryService = manageInventoryService;
    }

    @PostMapping
    public ResponseEntity<PartResponse> createPart(@Valid @RequestBody PartRequest request) {
        PartResponse response = manageInventoryService.createPart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PartResponse>> getAllParts() {
        return ResponseEntity.ok(manageInventoryService.getAllParts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getPartById(@PathVariable Long id) {
        return ResponseEntity.ok(manageInventoryService.getPartById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> updatePart(@PathVariable Long id, @Valid @RequestBody PartRequest request) {
        return ResponseEntity.ok(manageInventoryService.updatePart(id, request));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<PartResponse> updateAvailability(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean isAvailable = body.get("isAvailable");
        return ResponseEntity.ok(manageInventoryService.updateAvailability(id, isAvailable));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<PartResponse> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer stockQuantity = body.get("stockQuantity");
        return ResponseEntity.ok(manageInventoryService.updateStock(id, stockQuantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        manageInventoryService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}
