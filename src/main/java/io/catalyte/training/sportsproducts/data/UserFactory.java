package io.catalyte.training.sportsproducts.data;

import io.catalyte.training.sportsproducts.domains.purchase.BillingAddress;
import io.catalyte.training.sportsproducts.domains.purchase.CreditCard;
import io.catalyte.training.sportsproducts.domains.purchase.StateEnum;
import io.catalyte.training.sportsproducts.domains.user.User;
import io.catalyte.training.sportsproducts.domains.user.UserBillingAddress;
import io.catalyte.training.sportsproducts.domains.user.UserRepository;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.dao.DataAccessException;

public class UserFactory {

  private static final Random random = new Random();
  private static final int MAX_NAME_LENGTH = 10;
  private static final int MAX_STREET_DIGITS = 4;
  /**
   * List of actual users
   */
  public static final List<User> ACTUAL_USERS = new ArrayList();
  private static boolean USERS_PERSISTED = false;

  /**
   * Sets ActualUser on instantiation
   */
  static {
    setActualUsers();
  }

  /**
   * Gets a random state from StateEnum
   *
   * @return StateEnum
   */
  public static StateEnum getRandomState() {
    int size = StateEnum.values().length;
    StateEnum state = StateEnum.values()[random.nextInt(size)];
    ;
    return state;
  }

  /**
   * Generates a random lower case string consisting of only letters
   *
   * @param length size of the desired string
   * @return string
   */
  public static String generateRandomString(int length) {
    int leftLimit = 97;
    int rightLimit = 122;
    String randomString = random.ints(leftLimit, rightLimit + 1)
        .limit(length > 0 ? length : 1)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    ;
    return randomString;
  }

  /**
   * Get a random string with the first letter capitalized
   *
   * @param length name length
   * @return String
   */
  public static String generateRandomName(int length) {
    String randomString = generateRandomString(length);
    ;
    return randomString.substring(0, 1).toUpperCase() + randomString.substring(1);
  }

  /**
   * Generates a random integers with maxDigits digits
   *
   * @param maxDigits the number of digits desired for the integer
   * @return int
   */
  public static int getRandomInt(int maxDigits) {
    String numberString = "";
    int digitCount = 0;
    while (digitCount++ < maxDigits) {
      numberString = numberString + Integer.toString(random.nextInt(9));
    }
    ;
    return Integer.parseInt(numberString);
  }

  /**
   * Generates a random phone number
   *
   * @return String
   */
  public static String generateRandomPhoneNumber() {
    String areaCode = Integer.toString(getRandomInt(3));
    String prefix = Integer.toString(getRandomInt(3));
    String lineNumber = Integer.toString(getRandomInt(4));

    while (areaCode.length() < 3) {
      areaCode += "0";
    }

    while (prefix.length() < 3) {
      prefix += "0";
    }

    while (lineNumber.length() < 4) {
      lineNumber += "0";
    }
    ;
    return areaCode + "-" + prefix + "-" + lineNumber;
  }

  /**
   * Generates a random email address with the domain @[random].com
   *
   * @return String
   */
  public static String generateRandomEmailAddress() {
    String userName = generateRandomString(random.nextInt(12));
    String domain = generateRandomString(random.nextInt(12));
    ;
    return userName + "@" + domain + ".com";
  }

  /**
   * Generates a random UserBillingAddress object
   *
   * @return UserBillingAddress
   */
  public static UserBillingAddress generateRandomUserBillingAddress() {
    final UserBillingAddress address = new UserBillingAddress();
    StateEnum state = getRandomState();
    String street1 = generateRandomString(random.nextInt(MAX_STREET_DIGITS)) + " St.";
    String city = generateRandomString(random.nextInt(MAX_NAME_LENGTH));
    String zip = Integer.toString(getRandomInt(5));
    while (zip.length() < 5) {
      zip += "0";
    }
    ;
    address.setBillingStreet(street1);
    if (random.nextBoolean()) {
      address.setBillingStreet2("#" + Integer.toString(getRandomInt(3)));
    }
    address.setBillingCity(city);
    address.setBillingState(state.fullName);
    address.setBillingZip(Integer.valueOf(zip));
    address.setPhone(generateRandomPhoneNumber());

    return address;
  }

  /**
   * Generates a random BillingAddress object. If given null as the email, a random email address
   * will be generated.
   *
   * @param email The email to be attached to the BillingAddress. If null, a random email address
   *              will be generated.
   * @return BillingAddress
   */
  public static BillingAddress generateRandomPurchaseBillingAddress(String email) {
    if (email == null) {
      email = generateRandomEmailAddress();
    }
    UserBillingAddress billingAddress = generateRandomUserBillingAddress();
    return new BillingAddress(
        billingAddress.getBillingStreet(),
        billingAddress.getBillingStreet2(),
        billingAddress.getBillingCity(),
        billingAddress.getBillingState(),
        billingAddress.getBillingZip(),
        email,
        billingAddress.getPhone()
    );
  }

  /**
   * Generates a random CreditCard. If no owner is provided, a random name will be generated.
   *
   * @param owner The card holder. If null, a random name will be generated.
   * @return CreditCard
   */
  public static CreditCard generateRandomCreditCard(String owner) {
    String creditCardNumber = Integer.toString(getRandomInt(5))
        + Integer.toString(getRandomInt(5))
        + Integer.toString(getRandomInt(5))
        + Integer.toString(getRandomInt(1));
    while (creditCardNumber.length() < 16) {
      creditCardNumber += "0";
    }
    String name =
        generateRandomString(MAX_NAME_LENGTH) + " " + generateRandomString(MAX_NAME_LENGTH);
    String cvv = Integer.toString(getRandomInt(3));
    while (cvv.length() < 3) {
      cvv += "0";
    }
    String month = Integer.toString(random.nextInt(12));
    if (month.length() < 2) {
      month = "0" + month;
    }
    int yearMin = Year.now().getValue() + 1;
    int yearMax = yearMin + 4;
    String year = Integer.toString(random.nextInt(yearMax - yearMin) + yearMin);

    return new CreditCard(creditCardNumber, cvv, month + "/" + year, owner == null ? name : owner);
  }

  /**
   * Set the information for actual users that will be persisted in the database.
   */
  private static void setActualUsers() {
    String domain = "@catalyte.io";
    String[] firstNames = {
        "Devin",
        "Casey",
        "Blake",
        "Chandler",
        "Kaschae"
    };
    String[] lastNames = {
        "Duval",
        "Gandy",
        "Miller",
        "Davis",
        "Freeman"
    };
    String[] emails = {
        "dduval" + domain,
        "cgandy" + domain,
        "bmiller" + domain,
        "cdavis" + domain,
        "kfreeman" + domain
    };
    for (int i = 0; i < firstNames.length; i++) {
      ACTUAL_USERS.add(
          new User(emails[i],
              firstNames[i],
              lastNames[i],
              generateRandomUserBillingAddress()));
    }
    ;
  }

  /**
   * Generate a user with all random information
   * @return User
   */
  public static User generateRandomUser() {
    User user = new User();
    user.setFirstName(generateRandomName(random.nextInt(MAX_NAME_LENGTH)));
    user.setLastName(generateRandomName(random.nextInt(MAX_NAME_LENGTH)));
    user.setRole("Customer");
    user.setEmail(generateRandomEmailAddress());
    user.setBillingAddress(generateRandomUserBillingAddress());
    return user;
  }

  /**
   * Save a given User to the given UserRepository
   * @param user User to save
   * @param repo UserRepository to save User in
   * @return User
   */
  public static User persistUser(User user, UserRepository repo) {
    User savedUser = null;
    try {
      savedUser = repo.save(user);
    } catch (DataAccessException dae) {
      throw new ServerError(dae.getMessage());
    }
    user.setId(savedUser.getId());
    return user;
  }

  /**
   * Persist ACTUAL_USERS into the given repository
   *
   * @param repo UserRepository
   */
  public static void persistActualUsers(UserRepository repo) {
    if (ACTUAL_USERS.size() > 0 && !USERS_PERSISTED) {
      try {
        repo.saveAll(ACTUAL_USERS);
        USERS_PERSISTED = true;
      } catch (DataAccessException dao) {
        throw new ServerError(dao.getMessage());
      }
    }
  }

}