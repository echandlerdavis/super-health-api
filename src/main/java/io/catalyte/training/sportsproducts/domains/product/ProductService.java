package io.catalyte.training.sportsproducts.domains.product;

import java.util.HashMap;
import java.util.List;

/**
 * This interface provides an abstraction layer for the Products Service
 */
public interface ProductService {

  List<Product> getProducts(Product product);

  Product getProductById(Long id);

  List<String> getDistinctTypes();

  List<String> getDistinctCategories();

  List<String> getDistinctBrands();

  List<String> getDistinctMaterials();

  List<String> getDistinctDemographics();

  List<String> getDistinctPrimaryColors();

  List<String> getDistinctSecondaryColors();

  List<Product> addProducts(List<Product> products);

  Product saveProduct(Product product);

  List<Product> getProductsByFilters(HashMap<String, String> filters);
  List<Product> getProductsByIds(List<Long> ids);
}
