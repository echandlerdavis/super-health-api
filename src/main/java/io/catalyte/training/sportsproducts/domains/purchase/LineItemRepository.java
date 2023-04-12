package io.catalyte.training.sportsproducts.domains.purchase;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem, Long> {

  Set<LineItem> findByPurchase(Purchase purchase);

}
