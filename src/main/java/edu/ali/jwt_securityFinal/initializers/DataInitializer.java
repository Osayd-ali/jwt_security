package edu.ali.jwt_securityFinal.initializers;


import edu.ali.jwt_securityFinal.entities.Role;
import edu.ali.jwt_securityFinal.entities.User;
import edu.ali.jwt_securityFinal.repositories.RoleRepository;
import edu.ali.jwt_securityFinal.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostConstruct
    public void init() {
        if (userRepository.count() == 0 && roleRepository.count() == 0) {
            Role roleUser = new Role("USER");
            Role roleManager = new Role("MANAGER");
            Role roleAdmin = new Role("ADMIN");
            roleRepository.save(roleUser);
            roleRepository.save(roleManager);
            roleRepository.save(roleAdmin);
            User user = new User("user", passwordEncoder.encode("user123"), Set.of(roleUser));
            User manager =
                    new User("manager", passwordEncoder.encode("manager123"), Set.of(roleManager));
            User admin = new User("admin", passwordEncoder.encode("admin123"), Set.of(roleAdmin));
            userRepository.save(user);
            userRepository.save(manager);
            userRepository.save(admin);
            System.out.println("Initial users and roles inserted.");
        } else {
            System.out.println("Users and roles already exist, skipping initialization.");
        }
    }
}