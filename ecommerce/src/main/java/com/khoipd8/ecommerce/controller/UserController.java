package com.khoipd8.ecommerce.controller;

import com.khoipd8.ecommerce.entity.User;
import com.khoipd8.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user accounts in the E-commerce platform")
public class UserController {

    private final UserService userService;

    // Get all users
    @Operation(
            summary = "Retrieve all users",
            description = "Fetches a list of all active users in the system. " +
                    "Note: This endpoint does not support pagination or sorting directly."
    )
    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.fetchAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get all users with pagination and sorting
    @Operation(
            summary = "Retrieve all users with pagination and sorting",
            description = "Fetches a paginated and sortable list of users. " +
                    "Allows specifying page number, size, sort by field, and sort direction."
    )
    @GetMapping("/page")
    public ResponseEntity<Page<User>> getAllUsersWithPagination(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (e.g., 'id', 'username', 'email')", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userService.fetchAllUsersWithPagination(pageable);
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @Operation(
            summary = "Retrieve a user by ID",
            description = "Fetches detailed information for a specific user using their unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID of the user to retrieve", example = "1")
            @PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Create new user
    @Operation(
            summary = "Create a new user",
            description = "Registers a new user account with the provided details. " +
                    "Returns the newly created user object."
    )
    @PostMapping("")
    public ResponseEntity<User> createUser(
            @Parameter(description = "User object to be created. Exclude 'id' as it's auto-generated.")
            @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    // Update user by ID
    @Operation(
            summary = "Update an existing user by ID (Full Update)",
            description = "Updates all fields of an existing user identified by their ID. " +
                    "Missing fields in the request body will be set to null or default values."
    )
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of the user to update", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated user object. All fields will be replaced.")
            @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    // Partial update user by ID
    @Operation(
            summary = "Partially update an existing user by ID",
            description = "Updates specific fields of an existing user identified by their ID. " +
                    "Only provided fields in the request body will be updated."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<User> partialUpdateUser(
            @Parameter(description = "ID of the user to partially update", example = "1")
            @PathVariable Long id,
            @Parameter(description = "User object with fields to be updated. Missing fields will be ignored.")
            @RequestBody User user) {
        User updatedUser = userService.partialUpdateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    // Soft delete user by ID
    @Operation(
            summary = "Soft delete a user by ID",
            description = "Marks a user as inactive/deleted without removing their record from the database. " +
                    "Returns true if the operation was successful."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> softDeleteUser(
            @Parameter(description = "ID of the user to soft delete", example = "1")
            @PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok(true);
    }

    // Search users by keyword (optional feature)
    @Operation(
            summary = "Search users by keyword with pagination",
            description = "Searches for users whose username or email contains the given keyword. " +
                    "Supports pagination."
    )
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @Parameter(description = "Keyword to search for in username or email", example = "john")
            @RequestParam String keyword,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<User> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(users);
    }

    // Get users by status
    @Operation(
            summary = "Retrieve users by status",
            description = "Fetches a list of users based on their active/inactive status."
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(
            @Parameter(description = "User status (e.g., 'active', 'inactive')", example = "active")
            @PathVariable String status) {
        List<User> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    // Count total users
    @Operation(
            summary = "Count total number of users",
            description = "Returns the total count of users currently in the system."
    )
    @GetMapping("/count")
    public ResponseEntity<Long> countUsers() {
        Long count = userService.countUsers();
        return ResponseEntity.ok(count);
    }
}