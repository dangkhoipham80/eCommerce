package com.khoipd8.ecommerce.repository;

import com.khoipd8.ecommerce.entity.User;
import com.khoipd8.ecommerce.utils.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by username
    Optional<User> findByUsername(String username);

    // Find by email
    Optional<User> findByEmail(String email);

    // Find by username and exclude deleted users
    Optional<User> findByUsernameAndStatusNot(String username, UserStatus status);

    // Find by email and exclude deleted users
    Optional<User> findByEmailAndStatusNot(String email, UserStatus status);

    // Find by ID and exclude deleted users
    Optional<User> findByIdAndStatusNot(Long id, UserStatus status);

    // Find all users excluding deleted ones
    List<User> findByStatusNot(UserStatus status);

    // Find all users excluding deleted ones with pagination
    Page<User> findByStatusNot(UserStatus status, Pageable pageable);

    // Find users by specific status
    List<User> findByStatus(UserStatus status);

    // Find users by status with pagination
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    // Check if username exists
    boolean existsByUsername(String username);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if username exists excluding specific user
    boolean existsByUsernameAndIdNot(String username, Long id);

    // Check if email exists excluding specific user
    boolean existsByEmailAndIdNot(String email, Long id);

    // Count users excluding deleted ones
    long countByStatusNot(UserStatus status);

    // Count users by specific status
    long countByStatus(UserStatus status);

    // Search users by keyword in multiple fields (UPDATED to include Address fields)
    @Query("SELECT u FROM User u LEFT JOIN u.address a WHERE u.status != :excludeStatus AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.street) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + // Search in street
            "LOWER(a.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +    // Search in city
            "LOWER(a.state) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +   // Search in state
            "LOWER(a.country) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + // Search in country
            "LOWER(a.postalCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")  // Search in postal code
    List<User> searchByKeyword(@Param("keyword") String keyword,
                               @Param("excludeStatus") UserStatus excludeStatus,
                               Pageable pageable);

    // Alternative method for search without @Query annotation (UPDATED for Address fields)
    // NOTE: This method name can become very long and hard to read.
    // The @Query approach is generally preferred for complex searches involving relationships.
    List<User> findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContainingOrAddress_StreetContainingOrAddress_CityContainingOrAddress_StateContainingOrAddress_CountryContainingOrAddress_PostalCodeContaining(
            String username, String email, String firstName, String lastName,
            String addressStreet, String addressCity, String addressState, String addressCountry, String addressPostalCode,
            Pageable pageable);


    // Find users by phone number
    Optional<User> findByPhone(String phone);

    // Find users created between dates
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find users by city (UPDATED to use Address relationship)
    // Spring Data JPA can infer the join through the 'address_' prefix
    List<User> findByAddress_City(String city);

    // Find users by country (UPDATED to use Address relationship)
    List<User> findByAddress_Country(String country);

    // Find verified users
    List<User> findByEmailVerifiedTrue();

    // Find unverified users
    List<User> findByEmailVerifiedFalse();

    // Find users with account locked
    @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > :currentTime")
    List<User> findLockedUsers(@Param("currentTime") LocalDateTime currentTime);

    // Find users who haven't logged in for a certain period
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NULL OR u.lastLogin < :cutoffDate")
    List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find users by creation date range and status
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate AND u.status = :status")
    List<User> findByDateRangeAndStatus(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        @Param("status") UserStatus status);

    // Find top users by last login (most recent first)
    List<User> findTop10ByStatusNotOrderByLastLoginDesc(UserStatus status);

    // Find users with high login attempts
    @Query("SELECT u FROM User u WHERE u.loginAttempts >= :attempts")
    List<User> findUsersWithHighLoginAttempts(@Param("attempts") Integer attempts);

    // Custom query to get user statistics
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> getUserStatisticsByStatus();

    // Find users by full name (combining first and last name)
    // This query is fine as firstName and lastName are still direct fields of User
    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<User> findByFullNameContaining(@Param("fullName") String fullName);

    // Find users who need password reset token cleanup
    @Query("SELECT u FROM User u WHERE u.passwordResetTokenExpiry IS NOT NULL AND u.passwordResetTokenExpiry < :currentTime")
    List<User> findUsersWithExpiredPasswordResetTokens(@Param("currentTime") LocalDateTime currentTime);

    // Find users who need email verification token cleanup
    @Query("SELECT u FROM User u WHERE u.emailVerificationTokenExpiry IS NOT NULL AND u.emailVerificationTokenExpiry < :currentTime")
    List<User> findUsersWithExpiredEmailVerificationTokens(@Param("currentTime") LocalDateTime currentTime);
}