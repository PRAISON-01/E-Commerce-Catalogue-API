package ng.dev.praise.ecommercecatalogueapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Role;
import ng.dev.praise.ecommercecatalogueapi.data.enums.Status;
import ng.dev.praise.ecommercecatalogueapi.data.models.User;
import ng.dev.praise.ecommercecatalogueapi.data.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seeder.admin.email}")
    private String rootEmail;

    @Value("${app.seeder.admin.password}")
    private String rootPassword;


    @Override
    public void run(String... args) {

        if (!userRepository.existsByEmail(rootEmail)) {

            User seededAdmin = User.builder()
                    .firstName("Ikechukwu")
                    .lastName("Nwankwo")
                    .email(rootEmail)
                    .phoneNumber("09139265584")
                    .password(passwordEncoder.encode(rootPassword))
                    .role(Role.SEEDED_ADMIN)
                    .status(Status.ACTIVE)
                    .build();

            userRepository.save(seededAdmin);
            log.info("SEEDED_ADMIN successfully initialized: {}", rootEmail);
        } else {
            log.info("SEEDED_ADMIN already exists. Skipping seeding.");
        }
    }
}
