package io.catalyte.training.sportsproducts.domains.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.sportsproducts.constants.StringConstants;
import io.catalyte.training.sportsproducts.exceptions.BadRequest;
import io.catalyte.training.sportsproducts.exceptions.ResourceNotFound;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

import static io.catalyte.training.sportsproducts.domains.product.ProductFilterTypes.*;


/**
 * This class provides the implementation for the ProductService interface.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products from the database, optionally making use of an example if it is passed.
     *
     * @param product - an example product to use for querying
     * @return - a list of products matching the example, or all products if no example was passed
     */
    public List<Product> getProducts(Product product) {
        try {
            return productRepository.findAll(Example.of(product));
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * Retrieves the product with the provided id from the database.
     *
     * @param id - the id of the product to retrieve
     * @return - the product
     */
    public Product getProductById(Long id) {
        Product product;

        try {
            product = productRepository.findById(id).orElse(null);
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }

        if (product != null) {
            return product;
        } else {
            logger.info("Get by id failed, it does not exist in the database: " + id);
            throw new ResourceNotFound("Get by id failed, it does not exist in the database: " + id);
        }
    }

    /**
     * @return a list of unique Types in the database
     */
    public List<String> getDistinctTypes() {
        try {
            return productRepository.findDistinctTypes();
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * @return a list of unique Categories in the database
     */
    public List<String> getDistinctCategories() {
        try {
            return productRepository.findDistinctCategories();
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * @return a list of unique Brands in the database
     */
    public List<String> getDistinctBrands() {
        try {
            return productRepository.findDistinctBrands();
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * @return a list of unique Materials in the database
     */
    public List<String> getDistinctMaterials() {
        try {
            return productRepository.findDistinctMaterials();
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * @return a list of unique Demographics in the database
     */
    public List<String> getDistinctDemographics() {
        try {
            return productRepository.findDistinctDemographics();
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * @return a list of unique Primary Colors in the database
     */
    public List<String> getDistinctPrimaryColors() {
        try {
            return productRepository.findDistinctPrimaryColors();
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * @return a list of unique Primary Colors in the database
     */
    public List<String> getDistinctSecondaryColors() {
        try {
            return productRepository.findDistinctSecondaryColors();
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * Adds a product to the database
     *
     * @param products - list of product objects
     * @return list of product objects that are added to database
     */
    public List<Product> addProducts(List<Product> products) {
        try {
            return productRepository.saveAll(products);
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * Adds a product to the database
     *
     * @param product - product object
     * @return list of product objects that are added to database
     */
    public Product saveProduct(Product product) {
        List<String> productErrors = getProductErrors(product);

        if (!productErrors.isEmpty()){
            throw new BadRequest(String.join("\n", productErrors));
        }

        try {
            return productRepository.save(product);
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * Helper method that reads a product and validateds it's properties
     * @param product product to be validated
     * @return a list of errrors
     */
    public List<String> getProductErrors(Product product) {
        List<String> errors = new ArrayList<>();
        Boolean priceIsValid = validateProductPrice(product);
        List<String> emptyFields = getFieldsEmptyOrNull(product).get("emptyFields");
        List<String> nullFields = getFieldsEmptyOrNull(product).get("nullFields");
        Boolean quantityIsValid = validateProductQuantity(product);

        if (!nullFields.isEmpty()) {
            errors.add(StringConstants.PRODUCT_FIELDS_NULL(nullFields));
        }

        if (!emptyFields.isEmpty()) {
            errors.add(StringConstants.PRODUCT_FIELDS_EMPTY(emptyFields));
        }

        if (!priceIsValid) {
            errors.add(StringConstants.PRODUCT_PRICE_INVALID);
        }

        if (!quantityIsValid) {
            errors.add(StringConstants.PRODUCT_QUANTITY_INVALID);
        }

        return errors;
    }

    /**
     * Checks price is a double value greater than zero
     * and does not have more than 2 digits after the decimal
     *
     * Because price is stored as a double, regardless of input the product will always have 1 digit after the decimal
     * even if input as an integer, or with 2 zeros after decimal
     *
     * @param product product to be validated
     * @return boolean if product price is valid
     */
    public Boolean validateProductPrice(Product product) {
        if (product.getPrice() != null) {
        //Split price by the decimal
        String[] priceString = String.valueOf(product.getPrice()).split("\\.");
        Boolean priceMoreThan2Decimals = priceString[1].length() > 2;
        Boolean priceLessThanZero = product.getPrice() > 0;
        return priceLessThanZero || priceMoreThan2Decimals;
        }
        return false;
    }

    /**
     * Validates a products quantity is not a negative number
     * @param product product to be validated
     * @return boolean if product has valid quantity
     */
    public Boolean validateProductQuantity(Product product) {
        if (product.getQuantity() != null) {
            return product.getQuantity() >= 0;
        }
        return false;
    }

    /**
     * Reads a products fields and checks for fields that are empty or null
     * @param product product to be validated
     * @return A Hashmap {"emptyFields": List of empty fields, "nullFields": list of null fields}
     */
    public HashMap<String, List<String>> getFieldsEmptyOrNull(Product product) {
        List<Field> productFields = Arrays.asList(Product.class.getDeclaredFields());
        List<String> productFieldNames = new ArrayList<>();
        List<String> emptyFields = new ArrayList<>();
        List<String> nullFields = new ArrayList<>();
        HashMap<String, List<String>> results = new HashMap<>();
        //Get product field names
        productFields.forEach((field -> productFieldNames.add(field.getName())));
        //Remove id as product will not have an id before it is saved
        productFieldNames.remove("id");
        //Convert product to a HashMap
        ObjectMapper mapper = new ObjectMapper();
        Map productMap = mapper.convertValue(product, HashMap.class);
        //Loop through each fieldName to retrieve each product mapping value of the field
        productFieldNames.forEach((field) -> {
            //Check if the value for the product's field is null or empty and place in the corresponding list
            if (productMap.get(field) == null && field != "reviews") {
                nullFields.add(field);
            } else if (field != "reviews" && productMap.get(field).toString().trim() == "") {
                emptyFields.add(field);
            }
        });
        //place each list in the results
        results.put("emptyFields", emptyFields);
        results.put("nullFields", nullFields);
        return results;
    }

    /**
     * Filters products by multiple attributes provided
     *
     * @param filters the attributes to filter products by
     * @return List of products matching the filters
     */
    public List<Product> getProductsByFilters(HashMap<String, String> filters) {
        // Get list of products
        List<Product> products;

        try {
            products = productRepository.findAll();
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }

        // List of filters found and not yet implemented
        List<String> unImplementedFilters = new ArrayList<>();

        // Boolean set to if priceMin and priceMax is present in the filters
        boolean isPriceMin = false;
        boolean isPriceMax = false;

        ProductFilterTypes filterName = null;

        // For each filter key filter the list of products with their respective method or gather a list of filter keys that were not implemented
        for (String filter : filters.keySet()) {
            try {
                filterName = valueOf(filter);
            } catch (IllegalArgumentException e) {
                unImplementedFilters.add(filter);
            }
            switch (filterName) {
                case brand:
                    getProductsByBrands(products, filters.get(filter));
                    break;
                case category:
                    getProductsByCategories(products, filters.get(filter));
                    break;
                case demographic:
                    getProductsByDemographics(products, filters.get(filter));
                    break;
                case primaryColor:
                    getProductsByPrimaryColors(products, filters.get(filter));
                    break;
                case material:
                    getProductsByMaterials(products, filters.get(filter));
                    break;
                case priceMin:
                    isPriceMin = true;
                    break;
                case priceMax:
                    isPriceMax = true;
                    break;
                default:
                    unImplementedFilters.add(filter);
                    break;
            }
        }

        // Handle price min and price max filters separately since both will need to be present in order to call filter method
        if (isPriceMin && isPriceMax) {
            getProductsByPrice(products, filters.get("priceMin"), filters.get("priceMax"));
        }

        // Log all filters that are not implemented
        if (!unImplementedFilters.isEmpty()) {
            logger.info(StringConstants.UNIMPLEMENTED_FILTERS + unImplementedFilters);
        }

        return products;
    }

    @Override
    public List<Product> getProductsByIds(List<Long> ids) {
        try{
            return productRepository.findByIdIn(ids);
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * Helper Method filters list of products by brands
     *
     * @param products the list of products to filter
     * @param brands   the list of brands to filter by as a string with | between each value
     * @return filtered list of products
     */
    public List<Product> getProductsByBrands(List<Product> products, String brands) {
        // Create new list for brand names to allow matching without case sensitivity
        List<String> brandNames = new ArrayList<>();

        // slit the brands string into an array separated by |. Then add each value in lowercase to the new names array
        Arrays.asList(brands.split("\\|")).forEach(brand -> brandNames.add(brand.toLowerCase()));

        // Remove ongoing products list if product's brand does not match any brand filters
        products.removeIf(product -> !brandNames.contains(product.getBrand().toLowerCase()));

        return products;
    }

    /**
     * Helper Method filters list of products by categories
     *
     * @param products   the list of products to filter
     * @param categories the list of categories to filter by as a string with | between each value
     * @return filtered list of products
     */
    public List<Product> getProductsByCategories(List<Product> products, String categories) {

        List<String> categoryNames = new ArrayList<>();

        // slit the categories string into an array separated by |. Then add each value in lowercase to the new names array
        Arrays.asList(categories.split("\\|")).forEach(category -> categoryNames.add(category.toLowerCase()));

        products.removeIf(product -> !categoryNames.contains(product.getCategory().toLowerCase()));

        return products;
    }

    /**
     * Helper Method filters list of products by demographics
     *
     * @param products     the list of products to filter
     * @param demographics the list of demographics to filter by as a string with | between each value
     * @return filtered list of products
     */
    public List<Product> getProductsByDemographics(List<Product> products, String demographics) {

        List<String> demographicNames = new ArrayList<>();

        // slit the demographics string into an array separated by |. Then add each value in lowercase to the new names array
        Arrays.asList(demographics.split("\\|")).forEach(demographic -> demographicNames.add(demographic.toLowerCase()));

        products.removeIf(product -> !demographicNames.contains(product.getDemographic().toLowerCase()));

        return products;

    }

    /**
     * Helper Method filters list of products between two prices
     *
     * @param products the list of products to filter
     * @param priceMin the minimum price to filter by
     * @param priceMax the maximum price to filter by
     * @return filtered list of products
     */
    public List<Product> getProductsByPrice(List<Product> products, String priceMin, String priceMax) {

        Double min;
        Double max;

        // Try to parse price to ensure it is a double value, not a string of letters
        try {
            min = Double.valueOf(priceMin);
            max = Double.valueOf(priceMax);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage());
            throw new BadRequest(StringConstants.PRODUCT_PRICE_INVALID);
        }


        products.removeIf(product -> product.getPrice() < min || product.getPrice() > max);

        return products;

    }

    /**
     * Helper Method filters list of products by primaryColor
     *
     * @param products      the list of products to filter
     * @param primaryColors the list of primaryColors to filter by as a string with | between each value
     * @return filtered list of products
     */
    public List<Product> getProductsByPrimaryColors(List<Product> products, String primaryColors) {

        List<String> primaryColorsCodes = new ArrayList<>();

        // slit the primaryColors string into an array separated by |. Then add each value in lowercase to the new names array
        Arrays.asList(primaryColors.split("\\|")).forEach(primaryColor -> primaryColorsCodes.add(primaryColor.toLowerCase()));

        products.removeIf(product -> !primaryColorsCodes.contains(product.getPrimaryColorCode().toLowerCase()));

        return products;
    }

    /**
     * Helper Method filters list of products by material
     *
     * @param products  the list of products to filter
     * @param materials the list of materials to filter by as a string with | between each value
     * @return filtered list of products
     */
    public List<Product> getProductsByMaterials(List<Product> products, String materials) {

        List<String> materialNames = new ArrayList<>();

        // slit the materials string into an array separated by |. Then add each value in lowercase to the new names array
        Arrays.asList(materials.split("\\|")).forEach(material -> materialNames.add(material.toLowerCase()));

        products.removeIf(product -> !materialNames.contains(product.getMaterial().toLowerCase()));

        return products;
    }
}