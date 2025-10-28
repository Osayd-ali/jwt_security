package edu.ali.jwt_securityFinal.repositories;

import edu.ali.jwt_securityFinal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
