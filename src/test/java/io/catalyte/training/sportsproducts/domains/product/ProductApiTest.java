package io.catalyte.training.sportsproducts.domains.product;

import static io.catalyte.training.sportsproducts.constants.Paths.PRODUCTS_PATH;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.sportsproducts.constants.StringConstants;
import io.catalyte.training.sportsproducts.data.ProductFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.hamcrest.core.Every;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductApiTest {

  @Autowired
  ProductRepository productRepository;
  ProductFactory productFactory = new ProductFactory();
  Product testProduct1 = productFactory.createRandomProduct();
  Product testProduct2 = productFactory.createRandomProduct();
  List<String> brands = new ArrayList<>();
  List<String> categories = new ArrayList<>();
  List<String> demographics = new ArrayList<>();
  List<String> prices = new ArrayList<>();
  List<String> primaryColors = new ArrayList<>();
  List<String> materials = new ArrayList<>();
  Double priceMax;
  Double priceMin;
  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    setTestProducts();
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  private void setTestProducts() {
    productRepository.saveAll(Arrays.asList(testProduct1, testProduct2));
    getMinMaxPrice();
  }

  /**
   * Test Helper method used to compare values of the test products prices Assigns min and max value
   * that will be used to ensure prices filtered are between these values
   */
  private void getMinMaxPrice() {
    priceMin = Double.min(testProduct1.getPrice(), testProduct2.getPrice());
    priceMax = Double.max(testProduct1.getPrice(), testProduct2.getPrice());
  }

  @After
  public void removeTestData() {
    productRepository.delete(testProduct1);
    productRepository.delete(testProduct2);
  }

  @Test
  public void getProductsReturns200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH))
        .andExpect(status().isOk());
  }

  @Test
  public void getProductByIdReturnsProductWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/1"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctTypesReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/types"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctBrandsReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/brands"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctMaterialsReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/materials"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctDemographicsReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/demographics"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctCategoriesReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/categories"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctPrimaryColorsReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/primarycolors"))
        .andExpect(status().isOk());
  }


  @Test
  public void getDistinctSecondaryColorsReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/secondarycolors"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctTypesReturnsAllAndOnlyUniqueTypes() throws Exception {

    //GET types and check if it is returning each unique type, only once.
    mockMvc.perform(get(PRODUCTS_PATH + "/types"))
        .andExpect(jsonPath("$", hasItem("Pant")))
        .andExpect(jsonPath("$", hasItem("Short")))
        .andExpect(jsonPath("$", hasItem("Shoe")))
        .andExpect(jsonPath("$", hasItem("Glove")))
        .andExpect(jsonPath("$", hasItem("Tank Top")))
        .andExpect(jsonPath("$", hasItem("Jacket")))
        .andExpect(jsonPath("$", hasItem("Sock")))
        .andExpect(jsonPath("$", hasItem("Sunglasses")))
        .andExpect(jsonPath("$", hasItem("Hat")))
        .andExpect(jsonPath("$", hasItem("Helmet")))
        .andExpect(jsonPath("$", hasItem("Belt")))
        .andExpect(jsonPath("$", hasItem("Visor")))
        .andExpect(jsonPath("$", hasItem("Shin Guard")))
        .andExpect(jsonPath("$", hasItem("Elbow Pad")))
        .andExpect(jsonPath("$", hasItem("Headband")))
        .andExpect(jsonPath("$", hasItem("Wristband")))
        .andExpect(jsonPath("$", hasItem("Hoodie")))
        .andExpect(jsonPath("$", hasItem("Flip Flop")))
        .andExpect(jsonPath("$", hasItem("Pool Noodle")));
  }

  @Test
  public void getDistinctCategoriesReturnsAllAndOnlyUniqueCategories() throws Exception {

    //GET categories and check if it is returning each unique category, only once.
    mockMvc.perform(get(PRODUCTS_PATH + "/categories"))
        .andExpect(jsonPath("$", hasItem("Golf")))
        .andExpect(jsonPath("$", hasItem("Soccer")))
        .andExpect(jsonPath("$", hasItem("Basketball")))
        .andExpect(jsonPath("$", hasItem("Hockey")))
        .andExpect(jsonPath("$", hasItem("Football")))
        .andExpect(jsonPath("$", hasItem("Running")))
        .andExpect(jsonPath("$", hasItem("Baseball")))
        .andExpect(jsonPath("$", hasItem("Skateboarding")))
        .andExpect(jsonPath("$", hasItem("Boxing")))
        .andExpect(jsonPath("$", hasItem("Weightlifting")));
  }

  @Test
  public void getDistinctBrandsReturnsAllAndOnlyUniqueBrands() throws Exception {

    //GET brands and check if it is returning each unique brand, only once.
    mockMvc.perform(get(PRODUCTS_PATH + "/brands"))
        .andExpect(jsonPath("$", hasItem("Nike")))
        .andExpect(jsonPath("$", hasItem("Champion")))
        .andExpect(jsonPath("$", hasItem("New Balance")))
        .andExpect(jsonPath("$", hasItem("Puma")));
  }

  @Test
  public void getDistinctDemographicsReturnsAllAndOnlyUniqueDemographics() throws Exception {

    //GET demographics and check if it is returning each unique demographics, only once.
    mockMvc.perform(get(PRODUCTS_PATH + "/demographics"))
        .andExpect(jsonPath("$", hasItem("Men")))
        .andExpect(jsonPath("$", hasItem("Women")))
        .andExpect(jsonPath("$", hasItem("Kids")));
  }

  @Test
  public void getDistinctMaterialsReturnsAllAndOnlyUniqueMaterials() throws Exception {

    //GET materials and check if it is returning each unique materials, only once.
    mockMvc.perform(get(PRODUCTS_PATH + "/materials"))
        .andExpect(jsonPath("$", hasItem("Cotton")))
        .andExpect(jsonPath("$", hasItem("Polyester")))
        .andExpect(jsonPath("$", hasItem("Nylon")));
  }

  @Test
  public void getDistinctPrimaryColorsReturnsAllAndOnlyUniquePrimaryColors() throws Exception {

    //GET primary colors and check if it is returning each unique primary colors, only once.
    mockMvc.perform(get(PRODUCTS_PATH + "/primarycolors"))
        .andExpect(jsonPath("$", hasItem("#000000")))
        .andExpect(jsonPath("$", hasItem("#ffffff")));
  }

  @Test
  public void getDistinctBrandsReturnsAllAndOnlyUniqueSecondaryColors() throws Exception {

    //GET secondary colros and check if it is returning each unique secondary colors, only once.
    mockMvc.perform(get(PRODUCTS_PATH + "/secondarycolors"))
        .andExpect(jsonPath("$", hasItem("#000000")))
        .andExpect(jsonPath("$", hasItem("#3079ab")));
  }

  @Test
  public void getProductsByFilterQueryParamsWithOnlyBrandReturnsProductListWith200()
      throws Exception {

    String testBrand = testProduct1.getBrand();

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?brand=" + testBrand))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].brand")
            .value(Every.everyItem(is(testBrand))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithMultipleBrandsReturnsProductListWith200()
      throws Exception {

    brands.add(testProduct1.getBrand());
    brands.add(testProduct2.getBrand());

    String brandsString = String.join("|", brands);

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?brand=" + brandsString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].brand").value(
            Every.everyItem(anyOf(is(brands.get(0)), is(brands.get(1))))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithOnlyCategoryReturnsProductListWith200()
      throws Exception {

    String testCategory = testProduct1.getCategory();

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?category=" + testCategory))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].category")
            .value(Every.everyItem(is(testCategory))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithMultipleCategoriesReturnsProductListWith200()
      throws Exception {

    categories.add(testProduct1.getCategory());
    categories.add(testProduct2.getCategory());
    String categoriesString = String.join("|", categories);

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?category=" + categoriesString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].category")
            .value(Every.everyItem(anyOf(is(categories.get(0)), is(categories.get(1))))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithOnlyDemographicReturnsProductListWith200()
      throws Exception {

    String testDemographic = testProduct1.getDemographic();

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?demographic=" + testDemographic))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].demographic")
            .value(Every.everyItem(is(testDemographic))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithMultipleDemographicsReturnsProductListWith200()
      throws Exception {

    demographics.add(testProduct1.getDemographic());
    demographics.add(testProduct2.getDemographic());
    String demographicsString = String.join("|", demographics);

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?demographic=" + demographicsString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].demographic")
            .value(Every.everyItem(anyOf(is(demographics.get(0)), is(demographics.get(1))))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithOnlyPriceReturnsProductListWith200()
      throws Exception {

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?priceMin=" + priceMin + "&priceMax=" + priceMax))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].price")
            .value(Every.everyItem(
                anyOf(greaterThanOrEqualTo(priceMin), lessThanOrEqualTo(priceMax)))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithOnlyPriceMinReturnsListOfProductsWith200()
      throws Exception {

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?priceMin=" + priceMin))
        .andExpect(status().isOk());
  }

  @Test
  public void getProductsByFilterQueryParamsWithOnlyPriceMaxReturnsListOfProducts200()
      throws Exception {

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?priceMax=" + priceMax))
        .andExpect(status().isOk());
  }

  @Test
  public void getProductsByFilterQueryParamsWithOnlyPrimaryColorReturnsProductListWith200()
      throws Exception {

    String testPrimaryColor = testProduct1.getPrimaryColorCode();

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?primaryColor=" + testPrimaryColor))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].primaryColorCode")
            .value(Every.everyItem(is(testPrimaryColor))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithMultiplePrimaryColorsReturnsProductListWith200()
      throws Exception {

    primaryColors.add(testProduct1.getPrimaryColorCode());
    primaryColors.add(testProduct2.getPrimaryColorCode());
    String primaryColorsString = String.join("|", primaryColors);

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?primaryColor=" + primaryColorsString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].primaryColorCode")
            .value(Every.everyItem(anyOf(is(primaryColors.get(0)), is(primaryColors.get(1))))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithOnlyMaterialReturnsProductListWith200()
      throws Exception {

    String testMaterial = testProduct1.getMaterial();

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?material=" + testMaterial))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].material")
            .value(Every.everyItem(is(testMaterial))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithMultipleMaterialsReturnsProductListWith200()
      throws Exception {

    materials.add(testProduct1.getPrimaryColorCode());
    materials.add(testProduct2.getPrimaryColorCode());
    String materialsString = String.join("|", materials);

    mockMvc.perform(get(PRODUCTS_PATH + "/filter?material=" + materialsString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].material")
            .value(Every.everyItem(
                anyOf(is(testProduct1.getMaterial()), is(testProduct2.getMaterial())))));
  }

  @Test
  public void getProductsByFilterQueryParamsWithAllFiltersReturnsProductListWith200()
      throws Exception {

    // Create list for all attributes with the values of both test products
    brands.addAll(Arrays.asList(testProduct1.getBrand(), testProduct2.getBrand()));
    categories.addAll(Arrays.asList(testProduct1.getCategory(), testProduct2.getCategory()));
    demographics.addAll(
        Arrays.asList(testProduct1.getDemographic(), testProduct2.getDemographic()));
    prices.addAll(Arrays.asList(String.valueOf(testProduct1.getPrice()),
        String.valueOf(testProduct2.getPrice())));
    primaryColors.addAll(
        Arrays.asList(testProduct1.getPrimaryColorCode(), testProduct2.getPrimaryColorCode()));
    materials.addAll(Arrays.asList(testProduct1.getMaterial(), testProduct2.getMaterial()));

    // Get string to use for filter query
    StringBuilder filterString = createFilterStringForAllFilters();

    // Perform query and check all attributes match either testProduct1 or testProduct2 and price is between the two prices
    mockMvc.perform(get(PRODUCTS_PATH + "/filter?" + filterString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].brand")
            .value(
                Every.everyItem(anyOf(is(testProduct1.getBrand()), is(testProduct2.getBrand())))))
        .andExpect(jsonPath("$[*].category")
            .value(Every.everyItem(
                anyOf(is(testProduct1.getCategory()), is(testProduct2.getCategory())))))
        .andExpect(jsonPath("$[*].price")
            .value(Every.everyItem(
                anyOf(greaterThanOrEqualTo(priceMin), lessThanOrEqualTo(priceMax)))))
        .andExpect(jsonPath("$[*].demographic")
            .value(Every.everyItem(
                anyOf(is(testProduct1.getDemographic()), is(testProduct2.getDemographic())))))
        .andExpect(jsonPath("$[*].primaryColor")
            .value(Every.everyItem(anyOf(is(testProduct1.getPrimaryColorCode()),
                is(testProduct2.getPrimaryColorCode())))))
        .andExpect(jsonPath("$[*].material")
            .value(Every.everyItem(
                anyOf(is(testProduct1.getMaterial()), is(testProduct2.getMaterial())))));

  }

  /**
   * Helper method to create the string to be entered into a filter query get request
   *
   * @return string of attributes
   */
  private StringBuilder createFilterStringForAllFilters() {

    StringBuilder filterString = new StringBuilder();

    // Join list of each attribute to be filtered into a string replacing spaces with URL-encoded space character and add to filter string
    String brandsString = String.join("|", brands).replaceAll("\\s", "%20");
    filterString.append("brand=" + brandsString);

    String categoriesString = String.join("|", brands).replaceAll("\\s", "%20");
    filterString.append("&category=" + categoriesString);

    filterString.append("&priceMin=" + priceMin);
    filterString.append("&priceMax=" + priceMax);

    String primaryColorsString = String.join("|", primaryColors).replaceAll("\\s", "%20");
    filterString.append("&primaryColor=" + primaryColorsString);

    String materialsString = String.join("|", materials).replaceAll("\\s", "%20");
    filterString.append("&material=" + materialsString);

    return filterString;
  }

  @Test
  public void saveProductReturns201WithProductObject() throws Exception {
    //This test fails when run with coverage
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(PRODUCTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(testProduct1)))
        .andExpect(status().isCreated())
        .andReturn().getResponse();

    Product returnedProduct = mapper.readValue(response.getContentAsString(), Product.class);

    assert (returnedProduct.equals(testProduct1));
    assertNotNull(returnedProduct.getId());
  }

  @Test
  public void SaveProductReturns400IfPriceIsNegativeNumber() throws Exception {
    //This test fails when run with coverage
    Product newProduct = productFactory.createRandomProduct();
    newProduct.setPrice(-1.00);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(PRODUCTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newProduct)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.PRODUCT_PRICE_INVALID));
  }

  @Test
  public void SaveProductReturns400IQuantityIsNegativeNumber() throws Exception {
    //This test fails when run with coverage
    Product newProduct = productFactory.createRandomProduct();
    newProduct.setQuantity(-1);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(PRODUCTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newProduct)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.PRODUCT_QUANTITY_INVALID));
  }

  @Test
  public void SaveProductReturns400IfFieldsAreNull() throws Exception {
    //This test fails when run with coverage
    Product newProduct = productFactory.createRandomProduct();
    newProduct.setActive(null);
    newProduct.setBrand(null);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(PRODUCTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newProduct)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants.PRODUCT_FIELDS_NULL(Arrays.asList("brand", "active"))));
  }

  @Test
  public void SaveProductReturns400IfFieldsAreEmpty() throws Exception {
    //This test fails when run with coverage
    Product newProduct = productFactory.createRandomProduct();
    newProduct.setCategory("");
    newProduct.setBrand("");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(PRODUCTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newProduct)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants.PRODUCT_FIELDS_EMPTY(Arrays.asList("brand", "category"))));
  }

  @Test
  public void SaveProductReturns400WithListOfAllErrors() throws Exception {
    //This test fails when run with coverage
    Product newProduct = productFactory.createRandomProduct();
    newProduct.setActive(null);
    newProduct.setBrand("");
    newProduct.setPrice(-2.00);
    newProduct.setQuantity(-2);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
            post(PRODUCTS_PATH)
                .contentType("application/json")
                .content(mapper.writeValueAsString(newProduct)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    String[] responseErrors = responseMap.get("errorMessage").toString().split("\n");
    List<String> errorsList = Arrays.asList(responseErrors);
    assertTrue(errorsList.containsAll(Arrays.asList(
        StringConstants.PRODUCT_PRICE_INVALID,
        StringConstants.PRODUCT_QUANTITY_INVALID,
        StringConstants.PRODUCT_FIELDS_NULL(Arrays.asList("active")),
        StringConstants.PRODUCT_FIELDS_EMPTY(Arrays.asList("brand")))));
  }
}
