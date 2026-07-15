package edu.cit.ballener.lakbayayos.features.loginuser.controller;

import edu.cit.ballener.lakbayayos.features.loginuser.dto.UserProfileResponse;
import edu.cit.ballener.lakbayayos.features.loginuser.service.GetUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class GetUserProfileController {

    private final GetUserProfileService getUserProfileService;

    @Autowired
    public GetUserProfileController(GetUserProfileService getUserProfileService) {
        this.getUserProfileService = getUserProfileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable String id) {
        return ResponseEntity.ok(getUserProfileService.handle(id));
    }
}
