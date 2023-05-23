package io.catalyte.training.sportsproducts.domains.purchase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum StateEnum {
  AL("Alabama", shippingCosts.DEFAULT.cost),
  AK("Alaska", shippingCosts.HIGHER.cost),
  AZ("Arizona", shippingCosts.DEFAULT.cost),
  AR("Arkansas", shippingCosts.DEFAULT.cost),
  CA("California", shippingCosts.DEFAULT.cost),
  CO("Colorado", shippingCosts.DEFAULT.cost),
  CT("Connecticut", shippingCosts.DEFAULT.cost),
  DE("Delaware", shippingCosts.DEFAULT.cost),
  DC("District Of Columbia", shippingCosts.DEFAULT.cost),
  FL("Florida", shippingCosts.DEFAULT.cost),
  GA("Georgia", shippingCosts.DEFAULT.cost),
  HI("Hawaii", shippingCosts.HIGHER.cost),
  ID("Idaho", shippingCosts.DEFAULT.cost),
  IL("Illinois", shippingCosts.DEFAULT.cost),
  IN("Indiana", shippingCosts.DEFAULT.cost),
  IA("Iowa", shippingCosts.DEFAULT.cost),
  KS("Kansas", shippingCosts.DEFAULT.cost),
  KY("Kentucky", shippingCosts.DEFAULT.cost),
  LA("Louisiana", shippingCosts.DEFAULT.cost),
  ME("Maine", shippingCosts.DEFAULT.cost),
  MD("Maryland", shippingCosts.DEFAULT.cost),
  MA("Massachusetts", shippingCosts.DEFAULT.cost),
  MI("Michigan", shippingCosts.DEFAULT.cost),
  MN("Minnesota", shippingCosts.DEFAULT.cost),
  MS("Mississippi", shippingCosts.DEFAULT.cost),
  MO("Missouri", shippingCosts.DEFAULT.cost),
  MT("Montana", shippingCosts.DEFAULT.cost),
  NE("Nebraska", shippingCosts.DEFAULT.cost),
  NV("Nevada", shippingCosts.DEFAULT.cost),
  NH("New Hampshire", shippingCosts.DEFAULT.cost),
  NJ("New Jersey", shippingCosts.DEFAULT.cost),
  NM("New Mexico", shippingCosts.DEFAULT.cost),
  NY("New York", shippingCosts.DEFAULT.cost),
  NC("North Carolina", shippingCosts.DEFAULT.cost),
  ND("North Dakota", shippingCosts.DEFAULT.cost),
  OH("Ohio", shippingCosts.DEFAULT.cost),
  OK("Oklahoma", shippingCosts.DEFAULT.cost),
  OR("Oregon", shippingCosts.DEFAULT.cost),
  PA("Pennsylvania", shippingCosts.DEFAULT.cost),
  RI("Rhode Island", shippingCosts.DEFAULT.cost),
  SC("South Carolina", shippingCosts.DEFAULT.cost),
  SD("South Dakota", shippingCosts.DEFAULT.cost),
  TN("Tennessee", shippingCosts.DEFAULT.cost),
  TX("Texas", shippingCosts.DEFAULT.cost),
  UT("Utah", shippingCosts.DEFAULT.cost),
  VT("Vermont", shippingCosts.DEFAULT.cost),
  VA("Virginia", shippingCosts.DEFAULT.cost),
  WA("Washington", shippingCosts.DEFAULT.cost),
  WV("West Virginia", shippingCosts.DEFAULT.cost),
  WI("Wisconsin", shippingCosts.DEFAULT.cost),
  WY("Wyoming", shippingCosts.DEFAULT.cost);

  private static final Map<String, StateEnum> BY_FULLNAME = new HashMap<>();
  private static final Map<String, StateEnum> BY_ABBREVIATION = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(state -> {
      BY_FULLNAME.put(state.fullName, state);
      BY_ABBREVIATION.put(state.name(), state);
    });
  }

  /**
   * The fullname of the enum
   */
  public final String fullName;
  /**
   * The shipping cost of shipping to the state
   */
  public final double shippingCost;

  StateEnum(String fullName, double shippingCost) {
    this.fullName = fullName;
    this.shippingCost = shippingCost;
  }

  /**
   * Returns the shipping cost for the state represented by abbreviation
   *
   * @param abbreviation Two letter String
   * @return double
   */

  public static double getShippingByAbbreviation(String abbreviation) {
    return BY_ABBREVIATION.get(abbreviation.toUpperCase()).shippingCost;
  }

  /**
   * Returns the shipping cost for state stateName
   *
   * @param stateName String
   * @return double
   */
  public static double getShippingByName(String stateName) {
    String testName = formatStateName(stateName);
    StateEnum state = BY_FULLNAME.get(testName);
    if (state == null) {
      throw new IllegalArgumentException(
          String.format("%s is not an implemented state.", testName));
    }
    return state.shippingCost;
  }

  /**
   * Returns a boolean for if the given stateName is implemented
   *
   * @param stateName String
   * @return boolean
   */
  public static boolean isValidStateName(String stateName) {
    StateEnum state = BY_FULLNAME.get(formatStateName(stateName));
    return state != null;
  }

  /**
   * Formats the given string to match the StateEnum.fullName values
   *
   * @param string String
   * @return String with the first letter of each word capitalized
   */
  static String formatStateName(String string) {
    String[] strArray = string.split(" ");
    String cleanedString = "";
    for (String str : strArray) {
      cleanedString =
          cleanedString + " " + str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    return cleanedString.trim();
  }

  private enum shippingCosts {

    DEFAULT(5),
    HIGHER(10);
    public final double cost;

    shippingCosts(double value) {
      this.cost = value;
    }
  }

}
