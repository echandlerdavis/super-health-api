package io.catalyte.training.sportsproducts.data;

import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductRepository;
import io.catalyte.training.sportsproducts.domains.promotions.PromotionalCode;
import io.catalyte.training.sportsproducts.domains.promotions.PromotionalCodeRepository;
import io.catalyte.training.sportsproducts.domains.promotions.PromotionalCodeType;
import io.catalyte.training.sportsproducts.domains.purchase.LineItem;
import io.catalyte.training.sportsproducts.domains.purchase.LineItemRepository;
import io.catalyte.training.sportsproducts.domains.purchase.Purchase;
import io.catalyte.training.sportsproducts.domains.purchase.PurchaseRepository;
import io.catalyte.training.sportsproducts.domains.review.Review;
import io.catalyte.training.sportsproducts.domains.review.ReviewRepository;
import io.catalyte.training.sportsproducts.domains.user.User;
import io.catalyte.training.sportsproducts.domains.user.UserBillingAddress;
import io.catalyte.training.sportsproducts.domains.user.UserRepository;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Because this class implements CommandLineRunner, the run method is executed as soon as the server
 * successfully starts and before it begins accepting requests from the outside. Here, we use this
 * as a place to run some code that generates and saves a list of random products into the
 * database.
 */
@Component
public class DemoData implements CommandLineRunner {

  public static final int DEFAULT_NUMBER_OF_PRODUCTS = 500;
  public static final int MAX_PURCHASES_PER_USER = 20;
  public static final int MIN_PURCHASES_PER_USER = 5;
  private final Logger logger = LogManager.getLogger(DemoData.class);
  private final ProductFactory productFactory = new ProductFactory();
  private final PurchaseFactory purchaseFactory = new PurchaseFactory();
  private final UserFactory userFactory = new UserFactory();
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private LineItemRepository lineItemRepository;
  @Autowired
  private PurchaseRepository purchaseRepository;
  @Autowired
  private PromotionalCodeRepository promotionalCodeRepository;
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private Environment env;

  @Override
  public void run(String... strings) {
    boolean loadData;

    try {
      // Retrieve the value of custom property in application.yml
      loadData = Boolean.parseBoolean(env.getProperty("products.load"));
    } catch (NumberFormatException nfe) {
      logger.error("config variable loadData could not be parsed, falling back to default");
      loadData = true;
    }

    if (loadData) {
      seedDatabase();
    }
  }

  private void seedDatabase() {
    int numberOfProducts;

    try {
      // Retrieve the value of custom property in application.yml
      numberOfProducts = Integer.parseInt(env.getProperty("products.number"));
    } catch (NumberFormatException nfe) {
      logger.error("config variable numberOfProducts could not be parsed, falling back to default");
      // If it's not a string, set it to be a default value
      numberOfProducts = DEFAULT_NUMBER_OF_PRODUCTS;
    }
    // Generate products
    List<Product> productList = productFactory.generateRandomProducts(numberOfProducts);

    // Persist them to the database and save list to purchaseFactory
    logger.info("Loading " + numberOfProducts + " products...");
    purchaseFactory.setAvailableProducts(productRepository.saveAll(productList));
    //save actual users if that hasn't happened
    UserFactory.persistActualUsers(userRepository);

    //Generate reviews for each product and persist them to the database.
    for (Product product : productList) {
      List<Review> reviewList = productFactory.generateRandomReviews(product);
      product.setReviews(reviewList);
      reviewRepository.saveAll(reviewList);
    }
    logger.info("Data load completed. You can make requests now.");

    Calendar cal = Calendar.getInstance();
    Date today = new Date();
    cal.setTime(today);
    cal.add(Calendar.DATE, 1);
    Date end = cal.getTime();

    promotionalCodeRepository.save(
        new PromotionalCode(
            "FlatTest",
            "Flat rate test",
            PromotionalCodeType.FLAT,
            BigDecimal.valueOf(25),
            today,
            end));

    promotionalCodeRepository.save(
        new PromotionalCode(
            "PercentageTest",
            "Percentage rate test",
            PromotionalCodeType.PERCENT,
            BigDecimal.valueOf(25),
            today,
            end));
    cal.add(Calendar.DATE, -2);
    promotionalCodeRepository.save(
        new PromotionalCode(
            "ExpiredCode",
            "Expired test",
            PromotionalCodeType.FLAT,
            BigDecimal.valueOf(10),
            today,
            cal.getTime()
        )
    );
    cal.add(Calendar.DATE, 2);
    Date tomorrow = cal.getTime();
    cal.add(Calendar.DATE, 1);
    promotionalCodeRepository.save(
        new PromotionalCode(
            "InactiveCode",
            "Inactive test",
            PromotionalCodeType.PERCENT,
            BigDecimal.valueOf(10),
            tomorrow,
            cal.getTime()
        )
    );

    //set promotional code list in purchaseFactory
    purchaseFactory.setAvailablePromoCodes(promotionalCodeRepository.findAll());
    //anonymous user
    User amir = new User("amir@amir.com", "Customer", "Amir", "Sharapov",
        new UserBillingAddress("123 Main St", "", "Seattle", "Washington", 98101));

    //generate purchases for actual users
    for (User u : UserFactory.ACTUAL_USERS) {
      int numberPurchases = new Random().nextInt(MAX_PURCHASES_PER_USER);
      numberPurchases =
          numberPurchases > MIN_PURCHASES_PER_USER ? numberPurchases : MIN_PURCHASES_PER_USER;
      int count = 0;
      while (count++ < numberPurchases) {
        Purchase newPurchase = purchaseFactory.generateRandomPurchase(u);
        Purchase savedPurchase = purchaseRepository.save(newPurchase);
        for (LineItem line : savedPurchase.getProducts()) {
          line.setPurchase(savedPurchase);
        }
        lineItemRepository.saveAll(savedPurchase.getProducts());
      }
      Purchase anonymousPurchase = null;
      if (new Random().nextBoolean()) {
        anonymousPurchase = purchaseFactory.generateRandomPurchase(amir);
      } else {
        anonymousPurchase = purchaseFactory.generateRandomPurchase();
      }
      purchaseRepository.save(anonymousPurchase);
    }
    logger.info("Data load completed. You can make requests now.");


  }

}
