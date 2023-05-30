package io.catalyte.training.sportsproducts.domains.user;

import java.util.Date;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User entity in database
 */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String email;
  private String role;
  private String firstName;
  private String lastName;
  @Embedded
  private UserBillingAddress billingAddress;

  private Date lastActive;

  public User() {
    // Empty Constructor
  }

  public User(String email, String firstName, String lastName, UserBillingAddress billingAddress) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.billingAddress = billingAddress;
  }

  public User(Long id, String email, String role, String firstName, String lastName,
      UserBillingAddress billingAddress) {
    this.id = id;
    this.email = email;
    this.role = role;
    this.firstName = firstName;
    this.lastName = lastName;
    this.billingAddress = billingAddress;
  }

  public User(String email, String role, String firstName, String lastName,
      UserBillingAddress billingAddress) {
    this.email = email;
    this.role = role;
    this.firstName = firstName;
    this.lastName = lastName;
    this.billingAddress = billingAddress;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Date getLastActive() {
    return lastActive;
  }

  public void setLastActive(Date lastActive) {
    this.lastActive = lastActive;
  }


  public UserBillingAddress getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(UserBillingAddress billingAddress) {
    this.billingAddress = billingAddress;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", email='" + email + '\'' +
        ", role='" + role + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        '}';
  }
}
