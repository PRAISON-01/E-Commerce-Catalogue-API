package ng.dev.praise.ecommercecatalogueapi.data.repositories;

import ng.dev.praise.ecommercecatalogueapi.data.models.User;
import org.reactivestreams.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    User findUserByEmail(String email);

}

