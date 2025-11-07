package edu.ali.jwt_securityFinal.repositories;

import edu.ali.jwt_securityFinal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// UserRepository interface for performing CRUD operations on User entities
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
