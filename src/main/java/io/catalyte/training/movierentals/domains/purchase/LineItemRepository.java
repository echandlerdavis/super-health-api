package io.catalyte.training.movierentals.domains.purchase;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem, Long> {

  Set<LineItem> findByPurchase(Purchase purchase);

}
