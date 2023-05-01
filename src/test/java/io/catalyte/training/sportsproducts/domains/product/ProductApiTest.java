package io.catalyte.training.sportsproducts.domains.product;

import io.catalyte.training.sportsproducts.data.ProductFactory;
import org.hamcrest.Matchers;
import org.hamcrest.core.Every;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.catalyte.training.sportsproducts.constants.Paths.PRODUCTS_PATH;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductApiTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

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
     * Test Helper method used to compare values of the test products prices
     * Assigns min and max value that will be used to ensure prices filtered are between these values
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
    public void getDistinctCategoriesReturnsWith200() throws Exception {
        mockMvc.perform(get(PRODUCTS_PATH + "/categories"))
                .andExpect(status().isOk());
    }

    @Test
    public void getDistinctTypesReturnsAllAndOnlyUniqueTypes() throws Exception {

        //GET categories and check if it is returning each unique type, only once.
        mockMvc.perform(get(PRODUCTS_PATH + "/types"))
                .andExpect(ResultMatcher.matchAll(jsonPath("$", Matchers.containsInAnyOrder(
                        "Pant",
                        "Short",
                        "Shoe",
                        "Glove",
                        "Jacket",
                        "Tank Top",
                        "Sock",
                        "Sunglasses",
                        "Hat",
                        "Helmet",
                        "Belt",
                        "Visor",
                        "Shin Guard",
                        "Elbow Pad",
                        "Headband",
                        "Wristband",
                        "Hoodie",
                        "Flip Flop",
                        "Pool Noodle"))));
    }

    @Test
    public void getDistinctCategoriesReturnsAllAndOnlyUniqueCategories() throws Exception {


        //GET categories and check if it is returning each unique category, only once.
        mockMvc.perform(get(PRODUCTS_PATH + "/categories")).andExpect(ResultMatcher.matchAll(jsonPath("$", Matchers.containsInAnyOrder("Golf",
                "Soccer",
                "Basketball",
                "Hockey",
                "Football",
                "Running",
                "Baseball",
                "Skateboarding",
                "Boxing",
                "Weightlifting"))));
    }


    @Test
    public void getProductsByFilterQueryParamsWithOnlyBrandReturnsProductListWith200() throws Exception {

        String testBrand = testProduct1.getBrand();

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?brand=" + testBrand))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].brand")
                        .value(Every.everyItem(is(testBrand))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithMultipleBrandsReturnsProductListWith200() throws Exception {

        brands.add(testProduct1.getBrand());
        brands.add(testProduct2.getBrand());

        String brandsString = String.join("|", brands);

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?brand=" + brandsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].brand").value(Every.everyItem(anyOf(is(brands.get(0)), is(brands.get(1))))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithOnlyCategoryReturnsProductListWith200() throws Exception {

        String testCategory = testProduct1.getCategory();

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?category=" + testCategory))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].category")
                        .value(Every.everyItem(is(testCategory))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithMultipleCategoriesReturnsProductListWith200() throws Exception {

        categories.add(testProduct1.getCategory());
        categories.add(testProduct2.getCategory());
        String categoriesString = String.join("|", categories);

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?category=" + categoriesString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].category")
                        .value(Every.everyItem(anyOf(is(categories.get(0)), is(categories.get(1))))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithOnlyDemographicReturnsProductListWith200() throws Exception {

        String testDemographic = testProduct1.getDemographic();

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?demographic=" + testDemographic))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].demographic")
                        .value(Every.everyItem(is(testDemographic))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithMultipleDemographicsReturnsProductListWith200() throws Exception {

        demographics.add(testProduct1.getDemographic());
        demographics.add(testProduct2.getDemographic());
        String demographicsString = String.join("|", demographics);

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?demographic=" + demographicsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].demographic")
                        .value(Every.everyItem(anyOf(is(demographics.get(0)), is(demographics.get(1))))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithOnlyPriceReturnsProductListWith200() throws Exception {

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?priceMin=" + priceMin + "&priceMax=" + priceMax))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].price")
                        .value(Every.everyItem(anyOf(greaterThanOrEqualTo(priceMin), lessThanOrEqualTo(priceMax)))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithOnlyPriceMinReturnsListOfProductsWith200() throws Exception {

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?priceMin=" + priceMin))
                .andExpect(status().isOk());
    }

    @Test
    public void getProductsByFilterQueryParamsWithOnlyPriceMaxReturnsListOfProducts200() throws Exception {

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?priceMax=" + priceMax))
                .andExpect(status().isOk());
    }

    @Test
    public void getProductsByFilterQueryParamsWithOnlyPrimaryColorReturnsProductListWith200() throws Exception {

        String testPrimaryColor = testProduct1.getPrimaryColorCode();

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?primaryColor=" + testPrimaryColor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].primaryColorCode")
                        .value(Every.everyItem(is(testPrimaryColor))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithMultiplePrimaryColorsReturnsProductListWith200() throws Exception {

        primaryColors.add(testProduct1.getPrimaryColorCode());
        primaryColors.add(testProduct2.getPrimaryColorCode());
        String primaryColorsString = String.join("|", primaryColors);

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?primaryColor=" + primaryColorsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].primaryColorCode")
                        .value(Every.everyItem(anyOf(is(primaryColors.get(0)), is(primaryColors.get(1))))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithOnlyMaterialReturnsProductListWith200() throws Exception {

        String testMaterial = testProduct1.getMaterial();

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?material=" + testMaterial))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].material")
                        .value(Every.everyItem(is(testMaterial))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithMultipleMaterialsReturnsProductListWith200() throws Exception {

        materials.add(testProduct1.getPrimaryColorCode());
        materials.add(testProduct2.getPrimaryColorCode());
        String materialsString = String.join("|", materials);

        mockMvc.perform(get(PRODUCTS_PATH + "/filter?material=" + materialsString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].material")
                        .value(Every.everyItem(anyOf(is(testProduct1.getMaterial()), is(testProduct2.getMaterial())))));
    }

    @Test
    public void getProductsByFilterQueryParamsWithAllFiltersReturnsProductListWith200() throws Exception {

        // Create list for all attributes with the values of both test products
        brands.addAll(Arrays.asList(testProduct1.getBrand(), testProduct2.getBrand()));
        categories.addAll(Arrays.asList(testProduct1.getCategory(), testProduct2.getCategory()));
        demographics.addAll(Arrays.asList(testProduct1.getDemographic(), testProduct2.getDemographic()));
        prices.addAll(Arrays.asList(String.valueOf(testProduct1.getPrice()), String.valueOf(testProduct2.getPrice())));
        primaryColors.addAll(Arrays.asList(testProduct1.getPrimaryColorCode(), testProduct2.getPrimaryColorCode()));
        materials.addAll(Arrays.asList(testProduct1.getMaterial(), testProduct2.getMaterial()));

        // Get string to use for filter query
        StringBuilder filterString = createFilterStringForAllFilters();

        // Perform query and check all attributes match either testProduct1 or testProduct2 and price is between the two prices
        mockMvc.perform(get(PRODUCTS_PATH + "/filter?" + filterString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].brand")
                        .value(Every.everyItem(anyOf(is(testProduct1.getBrand()), is(testProduct2.getBrand())))))
                .andExpect(jsonPath("$[*].category")
                        .value(Every.everyItem(anyOf(is(testProduct1.getCategory()), is(testProduct2.getCategory())))))
                .andExpect(jsonPath("$[*].price")
                        .value(Every.everyItem(anyOf(greaterThanOrEqualTo(priceMin), lessThanOrEqualTo(priceMax)))))
                .andExpect(jsonPath("$[*].demographic")
                        .value(Every.everyItem(anyOf(is(testProduct1.getDemographic()), is(testProduct2.getDemographic())))))
                .andExpect(jsonPath("$[*].primaryColor")
                        .value(Every.everyItem(anyOf(is(testProduct1.getPrimaryColorCode()), is(testProduct2.getPrimaryColorCode())))))
                .andExpect(jsonPath("$[*].material")
                        .value(Every.everyItem(anyOf(is(testProduct1.getMaterial()), is(testProduct2.getMaterial())))));

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

}
