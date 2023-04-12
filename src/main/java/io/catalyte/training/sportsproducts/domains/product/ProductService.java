package io.catalyte.training.sportsproducts.domains.product;
import java.util.List;

/**
 * This interface provides an abstraction layer for the Products Service
 */
public interface ProductService {

  List<Product> getProducts(Product product);

  Product getProductById(Long id);

  List<String> getDistinctTypes();

  List<String> getDistinctCategories();

  List<Product> addProducts(List<Product> products);
}
