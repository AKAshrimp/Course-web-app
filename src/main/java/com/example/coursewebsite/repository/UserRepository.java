package com.example.coursewebsite.repository;

import com.example.coursewebsite.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<User> findByUsername(@Param("username") String username);
    
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    boolean existsByUsername(@Param("username") String username);
    
    boolean existsByEmail(String email);

    @Query("""
            SELECT DISTINCT u FROM User u
            LEFT JOIN u.roles r
            WHERE (:role = 'ALL' OR r = :role)
            AND (
                :search = ''
                OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(u.phoneNumber, '')) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<User> searchAdminUsers(@Param("search") String search, @Param("role") String role, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") String role);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM votes WHERE user_id = :userId", nativeQuery = true)
    void deleteUserVotes(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM comments WHERE user_id = :userId", nativeQuery = true)
    void deleteUserComments(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_roles WHERE user_id = :userId", nativeQuery = true)
    void deleteUserRoles(@Param("userId") Long userId);
} 