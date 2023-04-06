package io.catalyte.training.sportsproducts.domains.purchase;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import io.catalyte.training.sportsproducts.domains.product.ProductServiceImpl;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;


@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(ProductServiceImpl.class)
public class PurchaseServiceImplUnitTest {
  @InjectMocks
  private PurchaseServiceImpl purchaseServiceImpl;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private PurchaseRepository purchaseRepository;

  String testEmail;
  Purchase testPurchase;
  BillingAddress testBillingAddress;
  CreditCard testCreditCard;
  DeliveryAddress testDeliveryAddress;
  List<Purchase> testPurchases;

  @Before
  public void setUp(){
    MockitoAnnotations.initMocks(this);

    //assembly some test objects
    testEmail = "test@validEmail.com";
    testBillingAddress = new BillingAddress();
    testBillingAddress.setEmail(testEmail);
    testCreditCard = new CreditCard();
    testDeliveryAddress = new DeliveryAddress();
    testPurchase = new Purchase();


    testPurchase.setBillingAddress(testBillingAddress);
    testPurchase.setCreditCard(testCreditCard);
    testPurchase.setDeliveryAddress(testDeliveryAddress);

    testPurchases = new ArrayList<>();
    testPurchases.add(testPurchase);

    //set mock functionality
    when(purchaseRepository.findByBillingAddressEmail(anyString())).thenReturn(testPurchases);
  }

  @Test
  public void findByBillingAddressEmailCallsPurchaseService(){
    List<Purchase> actual = purchaseServiceImpl.findByBillingAddressEmail(testEmail);
    assertEquals(testPurchases, actual);
  }

  @Test(expected = ServerError.class)
  public void findByBillingAddressEmailCatchesDataAccessException(){
    doThrow(new DataAccessException("Test exception"){}).when(purchaseRepository).findByBillingAddressEmail(testEmail);

    List<Purchase> actual = purchaseServiceImpl.findByBillingAddressEmail(testEmail);
  }

}
