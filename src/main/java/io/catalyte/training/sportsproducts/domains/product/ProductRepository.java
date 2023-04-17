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


}
