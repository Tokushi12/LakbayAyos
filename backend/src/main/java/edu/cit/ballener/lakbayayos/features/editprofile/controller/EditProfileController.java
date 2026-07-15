package edu.cit.ballener.lakbayayos.features.editprofile.controller;

import edu.cit.ballener.lakbayayos.features.editprofile.dto.UpdateUserRequest;
import edu.cit.ballener.lakbayayos.features.editprofile.dto.UpdateUserResponse;
import edu.cit.ballener.lakbayayos.features.editprofile.service.EditProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class EditProfileController {

    private final EditProfileService editProfileService;

    @Autowired
    public EditProfileController(EditProfileService editProfileService) {
        this.editProfileService = editProfileService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserResponse> updateProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(editProfileService.handle(id, request));
    }
}
