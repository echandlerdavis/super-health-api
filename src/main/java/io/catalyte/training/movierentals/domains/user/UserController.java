package io.catalyte.training.movierentals.domains.user;

import static io.catalyte.training.movierentals.constants.LoggingConstants.UPDATE_LAST_ACTIVE;
import static io.catalyte.training.movierentals.constants.LoggingConstants.UPDATE_USER_REQUEST;
import static io.catalyte.training.movierentals.constants.Paths.USERS_PATH;
import static io.catalyte.training.movierentals.constants.StringConstants.AUTHORIZATION_HEADER;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for the user entity
 */
@RestController
@RequestMapping(value = USERS_PATH)
public class UserController {

  @Autowired
  private final UserServiceImpl userService;
  Logger logger = LogManager.getLogger(UserController.class);

  public UserController(UserServiceImpl userService) {
    this.userService = userService;
  }

  // METHODS

  /**
   * Controller method for logging the user in
   *
   * @param user        User to login
   * @param bearerToken String value in the Authorization property of the header
   * @return User
   */
  @PostMapping()
  public ResponseEntity<User> createUser(
      @RequestBody User user,
      @RequestHeader("Authorization") String bearerToken
  ) {
    logger.info("Request received for createUser");

    return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
  }

  /**
   * Controller method for updating the user by id
   *
   * @param id          id of the user to update
   * @param user        User to update
   * @param bearerToken String value in the Authorization property of the header
   * @return User - Updated user
   */
  @PutMapping(path = "/{id}")
  public ResponseEntity<User> updateUser(
      @PathVariable Long id,
      @RequestBody User user,
      @RequestHeader(AUTHORIZATION_HEADER) String bearerToken
  ) {
    logger.info(UPDATE_USER_REQUEST);
    return new ResponseEntity<>(userService.updateUser(bearerToken, id, user), HttpStatus.OK);
  }

  @PutMapping(path = "/{id}/updateLastActive")
  public ResponseEntity<Boolean> updateLastActive(
      @PathVariable Long id,
      @RequestHeader(AUTHORIZATION_HEADER) String bearerToken,
      @RequestBody User user
  ) {
    logger.info(UPDATE_LAST_ACTIVE);
    User savedUser = userService.updateLastActive(bearerToken, id, user);
    if (savedUser != null) {
      return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
  }

  /**
   * Controller method for getting a user by email
   *
   * @param email Email to get user by
   * @return User found in database
   */
  @GetMapping(path = "/{email}")
  public ResponseEntity<User> getUserByEmail(
      @PathVariable String email
  ) {
    logger.info("Request received for getUserByEmail");
    return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
  }

  /**
   * Controller method for updating the user by email
   *
   * @param email       email of the user to update
   * @param updatedUser User to update
   * @param bearerToken String value in the Authorization property of the header
   * @return User - Updated user
   */
  @PutMapping("/email/{email}")
  public ResponseEntity<User> updateUser(
      @PathVariable String email,
      @RequestBody User updatedUser,
      @RequestHeader(AUTHORIZATION_HEADER) String bearerToken
  ) {
    logger.info("Received request to update user " + email);
    try {
      User existingUser = userService.getUserByEmail(email);
      if (existingUser == null) {
        // User not found, return appropriate response
        return ResponseEntity.notFound().build();
      }

      // Update the fields of the existing user with the new values
      existingUser.setFirstName(updatedUser.getFirstName());
      existingUser.setLastName(updatedUser.getLastName());
      existingUser.setEmail(updatedUser.getEmail());
      existingUser.setBillingAddress(updatedUser.getBillingAddress());

      // Save the updated user back to the database
      User savedUser = userService.updateUser(existingUser);

      // Return the updated user in the response
      return ResponseEntity.ok(savedUser);
    } catch (Exception e) {
      // Handle any errors that occur during the update process
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
