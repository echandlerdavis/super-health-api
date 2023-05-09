package io.catalyte.training.sportsproducts.domains.product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT DISTINCT p.type FROM product p", nativeQuery = true)
    List<String> findDistinctTypes();

    @Query(value = "SELECT DISTINCT p.category FROM product p", nativeQuery = true)
    List<String> findDistinctCategories();

    @Query(value = "SELECT DISTINCT p.brand FROM product p", nativeQuery = true)
    List<String> findDistinctBrands();

    @Query(value = "SELECT DISTINCT p.material FROM product p", nativeQuery = true)
    List<String> findDistinctMaterials();

    @Query(value = "SELECT DISTINCT p.demographic FROM product p", nativeQuery = true)
    List<String> findDistinctDemographics();

    @Query(value = "SELECT DISTINCT p.primary_color_code FROM product p", nativeQuery = true)
    List<String> findDistinctPrimaryColors();

    @Query(value = "SELECT DISTINCT p.secondary_color_code FROM product p", nativeQuery = true)
    List<String> findDistinctSecondaryColors();
}
