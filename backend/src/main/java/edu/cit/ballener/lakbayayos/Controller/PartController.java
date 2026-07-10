package edu.cit.ballener.lakbayayos.Controller;
import edu.cit.ballener.lakbayayos.DTO.request.PartRequest;
import edu.cit.ballener.lakbayayos.DTO.response.PartResponse;
import edu.cit.ballener.lakbayayos.Service.PartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    @Autowired
    public PartController(PartService partService) {
        this.partService = partService;
    }

    // Admin: add a new part to inventory
    @PostMapping
    public ResponseEntity<PartResponse> createPart(@Valid @RequestBody PartRequest request) {
        PartResponse response = partService.createPart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Admin: view all parts (available and unavailable)
    @GetMapping
    public ResponseEntity<List<PartResponse>> getAllParts() {
        return ResponseEntity.ok(partService.getAllParts());
    }

    // User: view only bookable parts
    @GetMapping("/available")
    public ResponseEntity<List<PartResponse>> getAvailableParts() {
        return ResponseEntity.ok(partService.getAvailableParts());
    }

    // User: search parts by name or category
    @GetMapping("/search")
    public ResponseEntity<List<PartResponse>> searchParts(@RequestParam String query) {
        return ResponseEntity.ok(partService.searchParts(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getPartById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.getPartById(id));
    }

    // Admin: edit part details
    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> updatePart(@PathVariable Long id, @Valid @RequestBody PartRequest request) {
        return ResponseEntity.ok(partService.updatePart(id, request));
    }

    // Admin: toggle availability for booking
    @PatchMapping("/{id}/availability")
    public ResponseEntity<PartResponse> updateAvailability(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean isAvailable = body.get("isAvailable");
        return ResponseEntity.ok(partService.updateAvailability(id, isAvailable));
    }

    // Admin: adjust stock quantity directly
    @PatchMapping("/{id}/stock")
    public ResponseEntity<PartResponse> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer stockQuantity = body.get("stockQuantity");
        return ResponseEntity.ok(partService.updateStock(id, stockQuantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}