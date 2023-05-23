package io.catalyte.training.sportsproducts.domains.user;

/**
 * This interface provides an abstraction layer for the User Service
 */
public interface UserService {

  User updateUser(String credentials, Long id, User user);

  User createUser(User user);

  User getUserByEmail(String email);

  User updateLastActive(String credentials, Long id, User user);
}
