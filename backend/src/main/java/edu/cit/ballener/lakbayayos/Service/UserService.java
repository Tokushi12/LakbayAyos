package edu.cit.ballener.lakbayayos.Service;

import edu.cit.ballener.lakbayayos.DTO.request.RegisterRequest;
import edu.cit.ballener.lakbayayos.DTO.response.UserResponse;
import edu.cit.ballener.lakbayayos.Entity.User;
import edu.cit.ballener.lakbayayos.Exception.DuplicateEmailException;
import edu.cit.ballener.lakbayayos.Exception.UserNotFoundException;
import edu.cit.ballener.lakbayayos.Mapper.UserMapper;
import edu.cit.ballener.lakbayayos.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email is already registered: " + request.getEmail());
        }

        User user = UserMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user found with email: " + email));
        return UserMapper.toResponse(user);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user found with id: " + id));
        return UserMapper.toResponse(user);
    }
}
