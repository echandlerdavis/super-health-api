package io.catalyte.training.movierentals.domains.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByEmail(String email);

  User save(User user);

}
