package edu.cit.ballener.lakbayayos.features.loginuser.service;

import edu.cit.ballener.lakbayayos.exception.UserNotFoundException;
import edu.cit.ballener.lakbayayos.features.loginuser.dto.UserProfileResponse;
import edu.cit.ballener.lakbayayos.shared.entity.User;
import edu.cit.ballener.lakbayayos.shared.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Login itself (verifying email/password) happens client-side via Supabase
// Auth, not in this backend. Once Supabase confirms the credentials and
// returns the user's id, the frontend calls this slice to fetch that
// user's profile (full name, phone, role) - the piece of "logging in"
// that actually lives in our own database.
@Service
public class GetUserProfileService {

    private final UserRepository userRepository;

    @Autowired
    public GetUserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse handle(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user found with id: " + id));

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
