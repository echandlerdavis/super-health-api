package io.catalyte.training.movierentals.domains.promotions;

/**
 * This enum represents the type of a promotional code, which can be either a flat dollar amount or
 * a percentage off.
 */
public enum PromotionalCodeType {

  /**
   * Represents a flat dollar amount discount.
   */
  FLAT,

  /**
   * Represents a percentage off discount.
   */
  PERCENT
}
