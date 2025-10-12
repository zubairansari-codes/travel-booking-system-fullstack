package com.zubair.travel.service;

import com.zubair.travel.entity.User;
import com.zubair.travel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // CRUD Operations

    public User createUser(User user) {
        validateUser(user);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        validateUser(userDetails);
        
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new UserAlreadyExistsException("Email " + userDetails.getEmail() + " is already taken");
        }
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setAddress(userDetails.getAddress());
        user.setRole(userDetails.getRole());
        
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // Main Business Flows

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return user;
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (!user.getPassword().equals(oldPassword)) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new InvalidInputException("New password cannot be empty");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public void updateUserProfile(Long userId, String name, String phoneNumber, String address) {
        User user = getUserById(userId);
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            user.setPhoneNumber(phoneNumber);
        }
        if (address != null && !address.trim().isEmpty()) {
            user.setAddress(address);
        }
        userRepository.save(user);
    }

    // Validation

    private void validateUser(User user) {
        if (user == null) {
            throw new InvalidInputException("User cannot be null");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new InvalidInputException("User name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new InvalidInputException("User email is required");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new InvalidInputException("Invalid email format");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new InvalidInputException("User password is required");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    // Business Exceptions

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
