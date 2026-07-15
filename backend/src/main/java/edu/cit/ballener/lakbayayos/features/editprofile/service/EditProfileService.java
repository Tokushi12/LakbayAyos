package edu.cit.ballener.lakbayayos.features.editprofile.service;

import edu.cit.ballener.lakbayayos.exception.UserNotFoundException;
import edu.cit.ballener.lakbayayos.features.editprofile.dto.UpdateUserRequest;
import edu.cit.ballener.lakbayayos.features.editprofile.dto.UpdateUserResponse;
import edu.cit.ballener.lakbayayos.shared.entity.User;
import edu.cit.ballener.lakbayayos.shared.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Password reset itself happens client-side via Supabase Auth's own
// endpoint, same pattern as login - this slice only covers the profile
// fields (name, phone) that live in our own database.
@Service
public class EditProfileService {

    private final UserRepository userRepository;

    @Autowired
    public EditProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UpdateUserResponse handle(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user found with id: " + id));

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        User updated = userRepository.save(user);

        return new UpdateUserResponse(
                updated.getId(),
                updated.getEmail(),
                updated.getFullName(),
                updated.getPhoneNumber(),
                updated.getRole(),
                updated.getCreatedAt()
        );
    }
}
