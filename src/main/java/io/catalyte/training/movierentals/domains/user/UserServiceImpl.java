//package io.catalyte.training.movierentals.domains.user;
//
//import static io.catalyte.training.movierentals.constants.LoggingConstants.EMAIL_NULL;
//import static io.catalyte.training.movierentals.constants.LoggingConstants.EMAIL_TAKEN;
//import static io.catalyte.training.movierentals.constants.LoggingConstants.GOOGLE_AUTHENTICATION_FAILURE;
//import static io.catalyte.training.movierentals.constants.LoggingConstants.NO_EXISTING_USER_FORMAT;
//import static io.catalyte.training.movierentals.constants.LoggingConstants.NO_USER_WITH_EMAIL_FORMAT;
//import static io.catalyte.training.movierentals.constants.LoggingConstants.UPDATED_LAST_ACTIVE_FORMAT;
//import static io.catalyte.training.movierentals.constants.LoggingConstants.UPDATED_USER_FORMAT;
//import static io.catalyte.training.movierentals.constants.Roles.CUSTOMER;
//
//import io.catalyte.training.movierentals.constants.LoggingConstants;
//import io.catalyte.training.movierentals.exceptions.ResourceNotFound;
//import io.catalyte.training.movierentals.exceptions.ServerError;
//import java.util.Date;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//
///**
// * Implements user service interface
// */
//@Service
//@Transactional
//public class UserServiceImpl implements UserService {
//
//  private final Logger logger = LogManager.getLogger(UserServiceImpl.class);
//  private final UserRepository userRepository;
//
//  @Autowired
//  public UserServiceImpl(UserRepository userRepository, GoogleAuthService googleAuthService) {
//    this.userRepository = userRepository;
//    this.googleAuthService = googleAuthService;
//  }
//
//  // METHODS
//
//  /**
//   * Updates user using Profile Page updates
//   */
//  @Override
//  public User updateUser(User user) {
//    // Save the updated user to the database
//    logger.info(String.format("Updated user %d", user.getId()));
//    return userRepository.save(user);
//  }
//
//  /**
//   * Updates user given valid credentials
//   *
//   * @param bearerToken String value in the Authorization property of the header
//   * @param id          id of the user to update
//   * @param updatedUser User to update
//   * @return User - Updated user
//   */
//  @Override
//  public User updateUser(String bearerToken, Long id, User updatedUser) {
//
//    // AUTHENTICATES USER - SAME EMAIL, SAME PERSON
//    boolean isAuthenticated = googleAuthService.authenticateUser(bearerToken, updatedUser);
//
//    if (!isAuthenticated) {
//      logger.error(GOOGLE_AUTHENTICATION_FAILURE);
//      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//          GOOGLE_AUTHENTICATION_FAILURE);
//    }
//
//    // UPDATES USER
//    User existingUser = userRepository.findByEmail(updatedUser.getEmail());
//
//    if (existingUser == null) {
//      logger.error(String.format(NO_USER_WITH_EMAIL_FORMAT, updatedUser.getEmail()));
//      throw new ResourceNotFound(String.format(NO_USER_WITH_EMAIL_FORMAT, updatedUser.getEmail()));
//    }
//    try {
//      existingUser = userRepository.findById(id).orElse(null);
//    } catch (DataAccessException dae) {
//      logger.error(dae.getMessage());
//      throw new ServerError(dae.getMessage());
//    }
//
//    if (existingUser == null) {
//      logger.error(String.format(NO_EXISTING_USER_FORMAT, id));
//      throw new ResourceNotFound(String.format(NO_EXISTING_USER_FORMAT, id));
//    }
//
//    // TEMPORARY LOGIC TO PREVENT USER FROM UPDATING THEIR ROLE
//    updatedUser.setRole(existingUser.getRole());
//
//    // GIVE THE USER ID IF NOT SPECIFIED IN BODY TO AVOID DUPLICATE USERS
//    if (updatedUser.getId() == null) {
//      updatedUser.setId(id);
//    }
//
//    try {
//      logger.info(String.format(UPDATED_USER_FORMAT, updatedUser.getId()));
//      return userRepository.save(updatedUser);
//    } catch (DataAccessException dae) {
//      logger.error(dae.getMessage());
//      throw new ServerError(dae.getMessage());
//    }
//
//  }
//
//  @Override
//  public User updateLastActive(String bearerToken, Long id, User user) {
//    logger.info(String.format(UPDATED_LAST_ACTIVE_FORMAT, id));
//    user.setLastActive(new Date());
//    return updateUser(bearerToken, id, user);
//  }
//
//  /**
//   * Creates user in the database, given email is not null and not taken
//   *
//   * @param user User to create
//   * @return User
//   */
//  @Override
//  public User createUser(User user) {
//
//    String email = user.getEmail();
//
//    // CHECK TO MAKE SURE EMAIL EXISTS ON INCOMING USER
//    if (email == null) {
//      logger.error(EMAIL_NULL);
//      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, EMAIL_NULL);
//    }
//
//    // CHECK TO MAKE SURE USER EMAIL IS NOT TAKEN
//    User existingUser;
//
//    try {
//      existingUser = userRepository.findByEmail(user.getEmail());
//    } catch (DataAccessException dae) {
//      logger.error(dae.getMessage());
//      throw new ServerError(dae.getMessage());
//    }
//
//    if (existingUser != null) {
//      logger.error(EMAIL_TAKEN);
//      throw new ResponseStatusException(HttpStatus.CONFLICT, EMAIL_TAKEN);
//    }
//
//    // SET DEFAULT ROLE TO CUSTOMER
//    // NOT RUNNING CONDITIONAL DUE TO SOMEONE ASSIGNING THEMSELVES A ROLE
//    // if (user.getRole() == null) {
//    user.setRole(CUSTOMER);
//    // }
//
//    // Set lastActive
//    if (user.getLastActive() == null) {
//      user.setLastActive(new Date());
//    }
//
//    // SAVE USER
//    try {
//      logger.info(LoggingConstants.SAVED_USER);
//      return userRepository.save(user);
//    } catch (DataAccessException dae) {
//      logger.error(dae.getMessage());
//      throw new ServerError(dae.getMessage());
//    }
//
//  }
//
//  /**
//   * Gets user by an email
//   *
//   * @param email Email of the user
//   * @return The user
//   */
//  @Override
//  public User getUserByEmail(String email) {
//
//    User user;
//
//    try {
//      user = userRepository.findByEmail(email);
//    } catch (DataAccessException dae) {
//      logger.error(dae.getMessage());
//      throw new ServerError(dae.getMessage());
//    }
//
//    if (user == null) {
//      throw new ResourceNotFound(String.format(NO_USER_WITH_EMAIL_FORMAT, email));
//    }
//
//    return user;
//
//  }
//
//}
