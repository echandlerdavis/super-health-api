package io.catalyte.training.sportsproducts.domains.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import io.catalyte.training.sportsproducts.constants.StringConstants;
import io.catalyte.training.sportsproducts.data.ProductFactory;
import io.catalyte.training.sportsproducts.domains.review.Review;
import io.catalyte.training.sportsproducts.exceptions.BadRequest;
import io.catalyte.training.sportsproducts.exceptions.ResourceNotFound;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(ProductServiceImpl.class)
public class ProductServiceImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  Product testProduct1;
  Product testProduct2;
  Review testReview1;
  Review testReview2;
  Review testReview3;
  ProductFactory productFactory;
  List<Product> testProductsList = new ArrayList<>();
  List<Review> testReviewsListForProduct1 = new ArrayList<>();
  List<Review> testReviewsListForProduct2 = new ArrayList<>();
  List<String> brands = new ArrayList<>();
  List<String> categories = new ArrayList<>();
  List<String> demographics = new ArrayList<>();
  List<String> primaryColors = new ArrayList<>();
  List<String> materials = new ArrayList<>();
  String priceMin;
  String priceMax;
  @InjectMocks
  private ProductServiceImpl productServiceImpl;
  @Mock
  private ProductRepository productRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    setTestProducts();
    getMinMaxPrice();

    when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct1));
    when(productRepository.findAll()).thenReturn(testProductsList);
    when(productRepository.findDistinctBrands()).thenReturn(
        Arrays.asList(testProduct1.getBrand(), testProduct2.getBrand()));
    when(productRepository.findDistinctCategories()).thenReturn(
        Arrays.asList(testProduct1.getCategory(), testProduct2.getCategory()));
    when(productRepository.findDistinctDemographics()).thenReturn(
        Arrays.asList(testProduct1.getDemographic(), testProduct2.getDemographic()));
    when(productRepository.findDistinctPrimaryColors()).thenReturn(
        Arrays.asList(testProduct1.getPrimaryColorCode(), testProduct2.getPrimaryColorCode()));
    when(productRepository.findDistinctSecondaryColors()).thenReturn(
        Arrays.asList(testProduct1.getSecondaryColorCode(), testProduct2.getSecondaryColorCode()));
    when(productRepository.findDistinctTypes()).thenReturn(
        Arrays.asList(testProduct1.getType(), testProduct2.getType()));
    when(productRepository.findDistinctMaterials()).thenReturn(
        Arrays.asList(testProduct1.getMaterial(), testProduct2.getMaterial()));
    when(productRepository.save(any())).thenReturn(testProduct1);

  }

  private void setTestProducts() {

    // Create Two Random Test Products
    productFactory = new ProductFactory();
    testProduct1 = new Product(
        "Test1",
        "product created for testing purposes",
        "Men",
        "Baseball",
        "Hoodie",
        "2003-12-02",
        "Champion",
        "www.myimageurl.com",
        "Nylon",
        1,
        180.35,
        false,
        "po-6123888",
        "sc88763",
        "#f092b0",
        "#51b46d",
        testReviewsListForProduct1
    );
    testProduct2 = new Product(
        "Test2",
        "product created for testing purposes",
        "Women",
        "Soccer",
        "Pant",
        "2014-09-24",
        "Adidas",
        "www.myimageurl.com",
        "Polyester",
        1,
        75.95,
        true,
        "po-2151664",
        "sc72141",
        "#3079ab",
        "sc72141",
        testReviewsListForProduct2
    );

    testProductsList.add(testProduct1);
    testProductsList.add(testProduct2);

    //Create two random reviews to construct test products.
    testReview1 = new Review(
        "Test Review 1",
        4,
        "This is an example of a review for test product 1",
        "2005-11-01",
        "testUserNameOne",
        testProduct1
    );
    testReview2 = new Review(
        "Test Review 2",
        2,
        "This is a second example of a review for test product 1",
        "2007-25-03",
        "testUserNameTwo",
        testProduct1
    );
    testReview3 = new Review(
        "Test Review 3",
        5,
        "This is an example of a review for test product 2",
        "2010-13-01",
        "testUserNameThree",
        testProduct2
    );
    testReviewsListForProduct1.add(testReview1);
    testReviewsListForProduct1.add(testReview2);
    testReviewsListForProduct2.add(testReview3);
  }

  /**
   * Test Helper method used to compare values of the test products prices Assigns min and max value
   * that will be used to ensure prices filtered are between these values
   */
  private void getMinMaxPrice() {
    priceMin = String.valueOf(Double.min(testProduct1.getPrice(), testProduct2.getPrice()));
    priceMax = String.valueOf(Double.max(testProduct1.getPrice(), testProduct2.getPrice()));
  }

  @Test
  public void getProductByIdReturnsProduct() {
    Product actual = productServiceImpl.getProductById(123L);
    assertEquals(testProduct1, actual);
  }

  @Test
  public void getProductByIdThrowsErrorWhenNotFound() {
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> productServiceImpl.getProductById(123L));
  }

  @Test
  public void getProductByOneBrandReturnsListOfProducts() {
    List<Product> actual = productServiceImpl.getProductsByBrands(productRepository.findAll(),
        testProduct2.getBrand());
    assertEquals(Collections.singletonList(testProduct2), actual);
  }

  @Test
  public void getProductByTwoBrandsListOfProducts() {
    brands.add(testProduct1.getBrand());
    brands.add(testProduct2.getBrand());
    String brandsString = String.join("|", brands);
    List<Product> actual = productServiceImpl.getProductsByBrands(productRepository.findAll(),
        brandsString);
    assertEquals(testProductsList, actual);
  }

  @Test
  public void getProductByOneCategoryReturnsListOfProducts() {
    categories.add(testProduct2.getCategory());
    List<Product> actual = productServiceImpl.getProductsByCategories(productRepository.findAll(),
        testProduct2.getCategory());
    assertEquals(Collections.singletonList(testProduct2), actual);
  }

  @Test
  public void getProductByTwoCategoriesListOfProducts() {
    categories.add(testProduct1.getCategory());
    categories.add(testProduct2.getCategory());
    String categoryString = String.join("|", categories);
    List<Product> actual = productServiceImpl.getProductsByCategories(productRepository.findAll(),
        categoryString);
    assertEquals(testProductsList, actual);
  }

  @Test
  public void getProductByOneDemographicReturnsListOfProducts() {

    List<Product> actual = productServiceImpl.getProductsByDemographics(productRepository.findAll(),
        testProduct2.getDemographic());
    assertEquals(Collections.singletonList(testProduct2), actual);
  }

  @Test
  public void getProductByTwoDemographicsListOfProducts() {
    demographics.add(testProduct1.getDemographic());
    demographics.add(testProduct2.getDemographic());
    String demographicsString = String.join("|", demographics);
    List<Product> actual = productServiceImpl.getProductsByDemographics(productRepository.findAll(),
        demographicsString);
    assertEquals(testProductsList, actual);
  }

  @Test
  public void getProductByTwoPricesReturnsListOfProducts() {

    List<Product> actual = productServiceImpl.getProductsByPrice(productRepository.findAll(),
        priceMin, priceMax);
    assertEquals(testProductsList, actual);
  }

  @Test
  public void getProductByTwoPricesThrowsErrorIfOnePriceIsNotANumber() {
    priceMin = "abc";
    assertThrows(BadRequest.class,
        () -> productServiceImpl.getProductsByPrice(productRepository.findAll(), priceMin,
            priceMax));
  }

  @Test
  public void getProductByOnePrimaryColorReturnsListOfProducts() {

    List<Product> actual = productServiceImpl.getProductsByPrimaryColors(
        productRepository.findAll(), testProduct2.getPrimaryColorCode());
    assertEquals(Collections.singletonList(testProduct2), actual);
  }

  @Test
  public void getProductByTwoPrimaryColorsReturnsListOfProducts() {
    primaryColors.add(testProduct1.getPrimaryColorCode());
    primaryColors.add(testProduct2.getPrimaryColorCode());
    String primaryColorsString = String.join("|", primaryColors);
    List<Product> actual = productServiceImpl.getProductsByPrimaryColors(
        productRepository.findAll(), primaryColorsString);
    assertEquals(testProductsList, actual);
  }

  @Test
  public void getProductByOneMaterialReturnsListOfProducts() {
    List<Product> actual = productServiceImpl.getProductsByMaterials(productRepository.findAll(),
        testProduct2.getMaterial());
    assertEquals(Collections.singletonList(testProduct2), actual);
  }

  @Test
  public void getProductByTwoMaterialsReturnsListOfProducts() {
    materials.add(testProduct1.getMaterial());
    materials.add(testProduct2.getMaterial());
    String materialsString = String.join("|", materials);
    List<Product> actual = productServiceImpl.getProductsByMaterials(productRepository.findAll(),
        materialsString);
    assertEquals(testProductsList, actual);
  }

  @Test
  public void getProductByMultipleFiltersReturnsListOfProducts() {
    brands.addAll(Arrays.asList(testProduct1.getBrand(), testProduct2.getBrand()));
    categories.addAll(Arrays.asList(testProduct1.getCategory(), testProduct2.getCategory()));
    demographics.addAll(
        Arrays.asList(testProduct1.getDemographic(), testProduct2.getDemographic()));
    primaryColors.addAll(
        Arrays.asList(testProduct1.getPrimaryColorCode(), testProduct2.getPrimaryColorCode()));
    materials.addAll(Arrays.asList(testProduct1.getMaterial(), testProduct2.getMaterial()));

    HashMap<String, String> filters = new HashMap<>();

    filters.put("brand", String.join("|", brands));
    filters.put("category", String.join("|", categories));
    filters.put("priceMin", priceMin);
    filters.put("priceMax", priceMax);
    filters.put("primaryColor", String.join("|", primaryColors));
    filters.put("material", String.join("|", materials));

    List<Product> actual = productServiceImpl.getProductsByFilters(filters);
    assertEquals(testProductsList, actual);
  }

  @Test
  public void getDistinctBrandsReturnsListOfProductBrands() {
    brands.addAll(Arrays.asList(testProduct1.getBrand(), testProduct2.getBrand()));

    List<String> actual = productServiceImpl.getDistinctBrands();

    assertEquals(brands, actual);
  }

  @Test
  public void getDistinctCategoriesReturnsListOfProductCategories() {
    categories.addAll(Arrays.asList(testProduct1.getCategory(), testProduct2.getCategory()));

    List<String> actual = productServiceImpl.getDistinctCategories();

    assertEquals(categories, actual);
  }

  @Test
  public void getDistinctMaterialsReturnsListOfProductMaterials() {
    materials.addAll(Arrays.asList(testProduct1.getMaterial(), testProduct2.getMaterial()));

    List<String> actual = productServiceImpl.getDistinctMaterials();

    assertEquals(materials, actual);
  }

  @Test
  public void getDistinctPrimaryColorsReturnsListOfProductPrimaryColors() {
    primaryColors.addAll(
        Arrays.asList(testProduct1.getPrimaryColorCode(), testProduct2.getPrimaryColorCode()));

    List<String> actual = productServiceImpl.getDistinctPrimaryColors();

    assertEquals(primaryColors, actual);
  }

  @Test
  public void getDistinctSecondaryColorsReturnsListOfProductSecondaryColors() {
    List<String> expected = Arrays.asList(testProduct1.getSecondaryColorCode(),
        testProduct2.getSecondaryColorCode());

    List<String> actual = productServiceImpl.getDistinctSecondaryColors();

    assertEquals(expected, actual);
  }

  @Test
  public void getDistinctTypesReturnsListOfProductTypes() {
    List<String> expected = Arrays.asList(testProduct1.getType(), testProduct2.getType());

    List<String> actual = productServiceImpl.getDistinctTypes();

    assertEquals(expected, actual);
  }

  @Test
  public void getDistinctDemographicsReturnsListOfProductDemographics() {
    List<String> expected = Arrays.asList(testProduct1.getDemographic(),
        testProduct2.getDemographic());

    List<String> actual = productServiceImpl.getDistinctDemographics();

    assertEquals(expected, actual);
  }

  @Test
  public void saveProductThrowsServerError() {
    //This test fails when run with coverage
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(productRepository).save(any());
    assertThrows(ServerError.class, () -> productServiceImpl.saveProduct(testProduct2));
  }

  @Test
  public void GetDistinctBrandsThrowsServerError() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(productRepository).findDistinctBrands();
    assertThrows(ServerError.class, () -> productServiceImpl.getDistinctBrands());
  }

  @Test
  public void GetDistinctCategoriesThrowsServerError() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(productRepository).findDistinctCategories();
    assertThrows(ServerError.class, () -> productServiceImpl.getDistinctCategories());
  }

  @Test
  public void GetDistinctTypesThrowsServerError() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(productRepository).findDistinctTypes();
    assertThrows(ServerError.class, () -> productServiceImpl.getDistinctTypes());
  }

  @Test
  public void GetDistinctMaterialsThrowsServerError() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(productRepository).findDistinctMaterials();
    assertThrows(ServerError.class, () -> productServiceImpl.getDistinctMaterials());
  }

  @Test
  public void GetDistinctDemographicsThrowsServerError() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(productRepository).findDistinctDemographics();
    assertThrows(ServerError.class, () -> productServiceImpl.getDistinctDemographics());
  }

  @Test
  public void GetDistinctPrimaryColorsThrowsServerError() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(productRepository).findDistinctPrimaryColors();
    assertThrows(ServerError.class, () -> productServiceImpl.getDistinctPrimaryColors());
  }

  @Test
  public void GetDistinctSecondaryColorsThrowsServerError() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(productRepository).findDistinctSecondaryColors();
    assertThrows(ServerError.class, () -> productServiceImpl.getDistinctSecondaryColors());
  }

  @Test
  public void ValidateProductPriceReturnsFalseForInvalidIfPriceIsNotADoubleValueGreaterThan0() {
    testProduct1.setPrice(-10.00);
    assertEquals(false, productServiceImpl.validateProductPrice(testProduct1));
  }

  @Test
  public void ValidateProductPriceReturnsTrueForValidIfPriceIsADoubleValueGreaterThan0() {
    testProduct1.setPrice(10.00);
    assertEquals(true, productServiceImpl.validateProductPrice(testProduct1));
  }

  @Test
  public void SaveProductThrowsBadRequestIfPriceIsNotADoubleValueGreaterThan0() {
    testProduct1.setPrice(-10.00);
    assertThrows(BadRequest.class, () -> productServiceImpl.saveProduct(testProduct1));
  }

  @Test
  public void ValidateProductQuantityReturnsFalseForInvalidIfProductQuantityIsNegativeNumber() {
    testProduct1.setQuantity(-1);
    Boolean expected = testProduct1.getQuantity() >= 0;
    assertEquals(expected, productServiceImpl.validateProductQuantity(testProduct1));
  }

  @Test
  public void ValidateProductQuantityReturnsTrueForValidIfProductQuantityIs0() {
    testProduct1.setQuantity(0);
    Boolean expected = testProduct1.getQuantity() >= 0;
    assertEquals(expected, productServiceImpl.validateProductQuantity(testProduct1));
  }

  @Test
  public void GetEmptyOrNullFieldsReturnsEmptyFieldsIfProductHasEmptyFields() {
    testProduct1.setBrand("");
    testProduct1.setCategory("");
    List<String> expected = new ArrayList<>();
    expected.addAll(Arrays.asList("brand", "category"));

    assertEquals(expected,
        productServiceImpl.getFieldsEmptyOrNull(testProduct1).get("emptyFields"));
  }

  @Test
  public void GetEmptyOrNullFieldsReturnsNullFieldsIfProductHasNullFields() {
    //This test fails when run with coverage
    testProduct1.setBrand(null);
    testProduct1.setCategory(null);
    List<String> expected = new ArrayList<>();
    expected.addAll(Arrays.asList("brand", "category"));

    assertEquals(expected, productServiceImpl.getFieldsEmptyOrNull(testProduct1).get("nullFields"));
  }

  @Test
  public void GetEmptyOrNullFieldsReturnsEmptyAndNullFieldsIfProductHasNullAndEmptyFields() {
    //This test fails when run with coverage
    testProduct1.setBrand(null);
    testProduct1.setCategory(null);
    testProduct1.setDemographic("");
    testProduct1.setMaterial("");
    HashMap<String, List<String>> expected = new HashMap<>();
    expected.put("nullFields", Arrays.asList("brand", "category"));
    expected.put("emptyFields", Arrays.asList("material", "demographic"));

    assertEquals(expected, productServiceImpl.getFieldsEmptyOrNull(testProduct1));
  }

  @Test
  public void GetProductErrorsReturnsAllProductErrorsInAString() {
    //This test fails when run with coverage
    testProduct1.setPrice(-1.00);
    testProduct1.setQuantity(-1);
    testProduct1.setBrand("");
    testProduct1.setActive(null);
    assertTrue(productServiceImpl.getProductErrors(testProduct1)
        .containsAll(Arrays.asList(
            StringConstants.PRODUCT_PRICE_INVALID,
            StringConstants.PRODUCT_QUANTITY_INVALID,
            StringConstants.PRODUCT_FIELDS_EMPTY(Arrays.asList("brand")),
            StringConstants.PRODUCT_FIELDS_NULL(Arrays.asList("active")))));
  }

  @Test
  public void GetProductErrorsReturnsNoErrorsForValidProduct() {
    //This test fails when run with coverage
    assertTrue(productServiceImpl.getProductErrors(testProduct1).isEmpty());
  }

  @Test
  public void SaveValidProductReturnsProduct() {
    //This test fails when run with coverage
    assertEquals(testProduct1, productServiceImpl.saveProduct(testProduct1));
  }

  @Test
  public void SaveInvalidProductThrowsBadRequestWithListOfErrors() {
    //This test fails when run with coverage
    testProduct1.setPrice(-1.00);
    testProduct1.setQuantity(-1);
    testProduct1.setBrand("");
    testProduct1.setActive(null);
    try {
      productServiceImpl.saveProduct(testProduct1);
    } catch (BadRequest e) {
      String[] messageArr = e.getMessage().split("\n");
      List<String> messageList = Arrays.asList(messageArr);
      assertTrue(messageList.containsAll(Arrays.asList(
          StringConstants.PRODUCT_PRICE_INVALID,
          StringConstants.PRODUCT_QUANTITY_INVALID,
          StringConstants.PRODUCT_FIELDS_EMPTY(Arrays.asList("brand")),
          StringConstants.PRODUCT_FIELDS_NULL(Arrays.asList("active")))));
    }
  }


}
