package ng.dev.praise.ecommercecatalogueapi.data.repositories;

import ng.dev.praise.ecommercecatalogueapi.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
