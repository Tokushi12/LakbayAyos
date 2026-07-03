package edu.cit.ballener.lakbayayos.DTO.response;

import java.time.LocalDateTime;
public class UserResponse {
    private String id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role;
    private LocalDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(String id, String email, String fullName, String phoneNumber, String role, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
