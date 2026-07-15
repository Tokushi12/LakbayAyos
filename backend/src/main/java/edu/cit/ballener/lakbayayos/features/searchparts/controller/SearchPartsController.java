package edu.cit.ballener.lakbayayos.features.searchparts.controller;

import edu.cit.ballener.lakbayayos.features.searchparts.dto.PartResult;
import edu.cit.ballener.lakbayayos.features.searchparts.service.SearchPartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class SearchPartsController {

    private final SearchPartsService searchPartsService;

    @Autowired
    public SearchPartsController(SearchPartsService searchPartsService) {
        this.searchPartsService = searchPartsService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<PartResult>> getAvailableParts() {
        return ResponseEntity.ok(searchPartsService.getAvailableParts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PartResult>> searchParts(@RequestParam String query) {
        return ResponseEntity.ok(searchPartsService.search(query));
    }
}
