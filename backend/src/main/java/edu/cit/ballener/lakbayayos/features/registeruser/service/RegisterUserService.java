package edu.cit.ballener.lakbayayos.features.registeruser.service;

import edu.cit.ballener.lakbayayos.exception.DuplicateEmailException;
import edu.cit.ballener.lakbayayos.features.registeruser.dto.RegisterUserRequest;
import edu.cit.ballener.lakbayayos.features.registeruser.dto.RegisterUserResponse;
import edu.cit.ballener.lakbayayos.shared.entity.User;
import edu.cit.ballener.lakbayayos.shared.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService {

    private final UserRepository userRepository;

    @Autowired
    public RegisterUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterUserResponse handle(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email is already registered: " + request.getEmail());
        }

        User user = new User();
        user.setId(request.getId());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());

        User saved = userRepository.save(user);

        return new RegisterUserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getFullName(),
                saved.getPhoneNumber(),
                saved.getRole(),
                saved.getCreatedAt()
        );
    }
}
