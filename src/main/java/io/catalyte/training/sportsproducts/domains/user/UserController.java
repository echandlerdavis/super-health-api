package io.catalyte.training.sportsproducts.domains.user;

import static io.catalyte.training.sportsproducts.constants.LoggingConstants.UPDATE_LAST_ACTIVE;
import static io.catalyte.training.sportsproducts.constants.LoggingConstants.UPDATE_USER_REQUEST;
import static io.catalyte.training.sportsproducts.constants.Paths.USERS_PATH;
import static io.catalyte.training.sportsproducts.constants.StringConstants.AUTHORIZATION_HEADER;

import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Rest controller for the user entity
 */
@RestController
@RequestMapping(value = USERS_PATH)
public class UserController {

  Logger logger = LogManager.getLogger(UserController.class);

  @Autowired
  private final UserServiceImpl userService;

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
   * Controller method for updating the user
   *
   * @param id          Id of the user to update
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
      return new ResponseEntity<>(Boolean.TRUE ,HttpStatus.OK);
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
}
