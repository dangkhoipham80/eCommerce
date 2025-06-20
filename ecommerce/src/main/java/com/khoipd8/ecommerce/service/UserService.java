package com.khoipd8.ecommerce.service;

import com.khoipd8.ecommerce.entity.Address; // Import Address entity
import com.khoipd8.ecommerce.entity.User;
import com.khoipd8.ecommerce.repository.UserRepository;
import com.khoipd8.ecommerce.utils.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Fetch all users
    public List<User> fetchAllUsers() {
        return userRepository.findByStatusNot(UserStatus.DELETED);
    }

    // Fetch all users with pagination
    public Page<User> fetchAllUsersWithPagination(Pageable pageable) {
        return userRepository.findByStatusNot(UserStatus.DELETED, pageable);
    }

    // Get user by ID
    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findByIdAndStatusNot(id, UserStatus.DELETED);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new RuntimeException("User not found with id: " + id);
    }

    // Create new user
    public User createUser(User user) {
        // Validate required fields
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username, email, and password are required");
        }

        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default values
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        if (user.getEmailVerified() == null) {
            user.setEmailVerified(false);
        }
        if (user.getPhoneVerified() == null) {
            user.setPhoneVerified(false);
        }
        if (user.getLoginAttempts() == null) {
            user.setLoginAttempts(0);
        }

        // Handle the Address object
        if (user.getAddress() != null && user.getAddress().getId() == null) {
            // If it's a new address, set up its relationship
            // The cascade type in User entity's @OneToOne will handle saving the new address
        }

        return userRepository.save(user);
    }

    // Full update user
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        // Update all fields
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setPhone(userDetails.getPhone());
        existingUser.setDateOfBirth(userDetails.getDateOfBirth());
        existingUser.setGender(userDetails.getGender());
        existingUser.setAvatarUrl(userDetails.getAvatarUrl());

        // --- UPDATE ADDRESS FIELDS ---
        // Get the address object from userDetails
        Address newAddressDetails = userDetails.getAddress();

        if (newAddressDetails != null) {
            // If existing user doesn't have an address, create a new one
            if (existingUser.getAddress() == null) {
                existingUser.setAddress(new Address());
            }
            // Update fields of the existing user's address object
            existingUser.getAddress().setStreet(newAddressDetails.getStreet());
            existingUser.getAddress().setCity(newAddressDetails.getCity());
            existingUser.getAddress().setState(newAddressDetails.getState());
            existingUser.getAddress().setCountry(newAddressDetails.getCountry());
            existingUser.getAddress().setPostalCode(newAddressDetails.getPostalCode());
        } else {
            // If no address details are provided, and user had an address, you might want to null it out
            // This depends on your business logic: should an address be deletable via update?
            existingUser.setAddress(null); // Or keep the existing address if you don't want to delete it
        }
        // --- END UPDATE ADDRESS FIELDS ---


        if (userDetails.getStatus() != null) {
            existingUser.setStatus(userDetails.getStatus());
        }

        // Encode password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    // Partial update user
    public User partialUpdateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        // Update only non-null fields
        if (userDetails.getUsername() != null) {
            existingUser.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getFirstName() != null) {
            existingUser.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            existingUser.setLastName(userDetails.getLastName());
        }
        if (userDetails.getPhone() != null) {
            existingUser.setPhone(userDetails.getPhone());
        }
        if (userDetails.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userDetails.getDateOfBirth());
        }
        if (userDetails.getGender() != null) {
            existingUser.setGender(userDetails.getGender());
        }
        if (userDetails.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(userDetails.getAvatarUrl());
        }

        // --- PARTIAL UPDATE ADDRESS FIELDS ---
        Address newAddressDetails = userDetails.getAddress();

        if (newAddressDetails != null) {
            // If existing user doesn't have an address, create a new one
            if (existingUser.getAddress() == null) {
                existingUser.setAddress(new Address());
            }

            // Only update address fields if they are explicitly provided in userDetails.getAddress()
            if (newAddressDetails.getStreet() != null) {
                existingUser.getAddress().setStreet(newAddressDetails.getStreet());
            }
            if (newAddressDetails.getCity() != null) {
                existingUser.getAddress().setCity(newAddressDetails.getCity());
            }
            if (newAddressDetails.getState() != null) {
                existingUser.getAddress().setState(newAddressDetails.getState());
            }
            if (newAddressDetails.getCountry() != null) {
                existingUser.getAddress().setCountry(newAddressDetails.getCountry());
            }
            if (newAddressDetails.getPostalCode() != null) {
                existingUser.getAddress().setPostalCode(newAddressDetails.getPostalCode());
            }
        }
        // If userDetails.getAddress() is null, we assume the address is not being updated
        // We do NOT set existingUser.setAddress(null) here for partial update,
        // unless you specifically want to support nulling out the address via partial update.
        // If you want to allow setting address to null, you would add:
        // else if (userDetails.contains("address") && userDetails.get("address") == null) { existingUser.setAddress(null); }
        // but this requires more advanced partial update handling (e.g., using Jackson's @JsonInclude(JsonInclude.Include.ALWAYS) and checking for null explicitly)
        // For simplicity, if newAddressDetails is null, we just skip address update.
        // --- END PARTIAL UPDATE ADDRESS FIELDS ---

        if (userDetails.getStatus() != null) {
            existingUser.setStatus(userDetails.getStatus());
        }

        // Encode password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    // Soft delete user
    public void softDeleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(UserStatus.DELETED);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    // Search users by keyword (This method will need adjustment or a new query)
    // The current query in UserRepository 'findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContaining'
    // directly uses fields from User. If you want to search by address fields, you'll need
    // to modify the repository method to include address fields, possibly via a JOIN.
    public List<User> searchUsers(String keyword, Pageable pageable) {
        // Option 1: Continue searching only on User's direct fields
        // return userRepository.findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContaining(
        //         keyword, keyword, keyword, keyword, pageable);

        // Option 2: Use the custom @Query method in UserRepository which includes address fields
        return userRepository.searchByKeyword(keyword, UserStatus.DELETED, pageable);
    }

    // Get users by status
    public List<User> getUsersByStatus(String status) {
        try {
            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
            return userRepository.findByStatus(userStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    // Count total users
    public Long countUsers() {
        return userRepository.countByStatusNot(UserStatus.DELETED);
    }

    // Check if user exists by username
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Check if user exists by email
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Find user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameAndStatusNot(username, UserStatus.DELETED);
    }

    // Find user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndStatusNot(email, UserStatus.DELETED);
    }

    // Update last login
    public void updateLastLogin(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastLogin(LocalDateTime.now());
            user.setLoginAttempts(0); // Reset login attempts on successful login
            userRepository.save(user);
        }
    }

    // Increment login attempts
    public void incrementLoginAttempts(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLoginAttempts(user.getLoginAttempts() + 1);

            // Lock account after 5 failed attempts for 30 minutes
            if (user.getLoginAttempts() >= 5) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
            }

            userRepository.save(user);
        }
    }

    // Unlock user account
    public void unlockUserAccount(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setAccountLockedUntil(null);
            user.setLoginAttempts(0);
            userRepository.save(user);
        }
    }

    // You will need to modify these methods in UserRepository as well
    // Find users by city (requires joining with Address)
    public List<User> findUsersByCity(String city) {
        return userRepository.findByAddress_City(city); // Example using property expression
    }

    // Find users by country (requires joining with Address)
    public List<User> findUsersByCountry(String country) {
        return userRepository.findByAddress_Country(country); // Example using property expression
    }

    // You might also need to update other methods in UserRepository that relied on direct address fields
    // For example, if you had a findByAddress field, it would now be findByAddress_Street or require a custom query.
}