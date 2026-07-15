package edu.cit.ballener.lakbayayos.features.registeruser.controller;

import edu.cit.ballener.lakbayayos.features.registeruser.dto.RegisterUserRequest;
import edu.cit.ballener.lakbayayos.features.registeruser.dto.RegisterUserResponse;
import edu.cit.ballener.lakbayayos.features.registeruser.service.RegisterUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class RegisterUserController {

    private final RegisterUserService registerUserService;

    @Autowired
    public RegisterUserController(RegisterUserService registerUserService) {
        this.registerUserService = registerUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserResponse response = registerUserService.handle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
