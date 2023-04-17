package io.catalyte.training.sportsproducts.domains.purchase;

import java.util.List;

public interface PurchaseService {

  Purchase savePurchase(Purchase purchaseToSave);

  List<Purchase> findAllPurchases();

  List<Purchase> findByBillingAddressEmail(String email);

}
