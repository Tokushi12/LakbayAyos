package edu.cit.ballener.lakbayayos.Mapper;

import edu.cit.ballener.lakbayayos.DTO.request.RegisterRequest;
import edu.cit.ballener.lakbayayos.DTO.response.UserResponse;
import edu.cit.ballener.lakbayayos.Entity.User;

public class UserMapper {
    public static User toEntity(RegisterRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
