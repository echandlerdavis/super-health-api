package io.catalyte.training.sportsproducts.domains.promotions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository interface for managing {@link PromotionalCode} entities.
 */
@Repository
public interface PromotionalCodeRepository extends JpaRepository<PromotionalCode, Long> {

  PromotionalCode findByTitle(String code);
}
