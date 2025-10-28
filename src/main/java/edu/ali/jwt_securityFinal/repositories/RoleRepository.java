package edu.ali.jwt_securityFinal.repositories;

import edu.ali.jwt_securityFinal.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
