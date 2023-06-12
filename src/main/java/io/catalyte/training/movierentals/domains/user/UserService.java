package io.catalyte.training.movierentals.domains.user;

/**
 * This interface provides an abstraction layer for the User Service
 */
public interface UserService {

  User updateUser(User user);

  User updateUser(String credentials, Long id, User user);

  User createUser(User user);

  User getUserByEmail(String email);

  User updateLastActive(String credentials, Long id, User user);
}
