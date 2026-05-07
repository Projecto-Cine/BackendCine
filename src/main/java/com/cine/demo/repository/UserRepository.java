package com.cine.demo.repository;

import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByIdAndRole(Long id, Role role);
    List<User> findByRole(Role role);
    long countByStatus(String status);

    List<User> findByRoleNot(Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<User> searchClients(@Param("role") Role role, @Param("q") String q);

    @Query("SELECT u FROM User u WHERE u.role <> :role AND " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<User> searchWorkers(@Param("role") Role role, @Param("q") String q);
}