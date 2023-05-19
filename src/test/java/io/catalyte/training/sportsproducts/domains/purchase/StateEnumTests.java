package io.catalyte.training.sportsproducts.domains.purchase;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StateEnumTests {
  private double LOWER_48_SHIPPING;
  private double ELEVATED_SHIPPING;
  private double DELTA;

  @Before
  public void setUp(){
    LOWER_48_SHIPPING = 5;
    ELEVATED_SHIPPING = 10;
    DELTA = .001;
  }

  @Test
  public void formatStateNameSingleNameTest(){
    String testString = "wASHINGTON";
    String expected = "Washington";
    String actual = StateEnum.formatStateName(testString);
    assertEquals(expected, actual);
  }

  @Test
  public void formatStateNameMultipleNameTest(){
    String testString = "nEW mEXICO";
    String expected = "New Mexico";
    String actual = StateEnum.formatStateName(testString);
    assertEquals(expected, actual);
  }

  @Test
  public void getShippingByAbbreviationNormalCostTest() {
    String testState = "ca";
    double actual = StateEnum.getShippingByAbbreviation(testState);
    assertEquals(LOWER_48_SHIPPING, actual, DELTA);
  }
  @Test
  public void getShippingByAbbreviationElevatedCostTest() {
    String testState = "ak";
    double actual = StateEnum.getShippingByAbbreviation(testState);
    assertEquals(ELEVATED_SHIPPING, actual, DELTA);
  }
  @Test
  public void getShippingByNameNormalCostTest(){
    String testState = "new mexico";
    double actual = StateEnum.getShippingByName(testState);
    assertEquals(actual, LOWER_48_SHIPPING, DELTA);
  }
  @Test
  public void getShippingByNameElevatedCostTest(){
    String testState = "hawaii";
    double actual = StateEnum.getShippingByName(testState);
  assertEquals(actual, ELEVATED_SHIPPING, DELTA);
  }

  @Test
  public void isValidStateNameTrueTest(){
    boolean expected = true;
    String testState = "mississippi";
    boolean actual = StateEnum.isValidStateName(testState);
    assertEquals(expected, actual);
  }
  @Test
  public void isValidStateNameFalseTest(){
    boolean expected = false;
    String testState = "puerto rico";
    boolean actual = StateEnum.isValidStateName(testState);
    assertEquals(expected, actual);
  }

  @Test
  public void getDistrictOfColumbiaShippingTest(){
    double expected = 5.0;
    double actual = StateEnum.getShippingByName("District Of Columbia");
    assertEquals(expected, actual, .001);
  }
}
