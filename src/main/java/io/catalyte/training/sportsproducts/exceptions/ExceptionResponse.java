package io.catalyte.training.sportsproducts.exceptions;

import io.catalyte.training.sportsproducts.domains.product.Product;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Describes an object to hold error information that the server will return to clients.
 */
public class ExceptionResponse {

  private Date timestamp;
  private String error;
  private String errorMessage;
  private List payload;

  public ExceptionResponse() {
  }

  public ExceptionResponse(String error, Date timestamp, String errorMessage) {
    this.error = error;
    this.timestamp = timestamp;
    this.errorMessage = errorMessage;
  }

  public ExceptionResponse(String error, Date timestamp, String errorMessage, List payload) {
    this.error = error;
    this.timestamp = timestamp;
    this.errorMessage = errorMessage;
    this.payload = payload;
  }

  public ExceptionResponse(String error, Date timestamp, String errorMessage,
      Map<String, List<Product>> payload) {
    this(error, timestamp, error);
    List<String> consolidatedMap = new ArrayList<>();
    //consolidate map to list
    for (String errorType : payload.keySet()) {
      consolidatedMap.add(errorType);
      List<Product> associatedProducts = payload.get(errorType);
      for (Product p : associatedProducts) {
        consolidatedMap.add(p.toString());
      }
    }
    //assign payload to new list
    this.payload = consolidatedMap;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public List getPayload() {
    return payload;
  }

  public void setPayload(List payload) {
    this.payload = payload;
  }
}
