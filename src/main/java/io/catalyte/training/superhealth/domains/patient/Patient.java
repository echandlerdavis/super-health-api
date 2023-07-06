package io.catalyte.training.superhealth.domains.patient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.catalyte.training.superhealth.domains.encounter.Encounter;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.criteria.CriteriaBuilder.In;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Describes a patient object
 */
@Entity
public class Patient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String firstName;

  private String lastName;

  private String ssn;

//  @Column(unique = true)
  private String email;

  private String street;

  private String city;

  private String state;

  private String postal;

  private Integer age;

  private Integer height;

  private Integer weight;

  private String insurance;

  private String gender;

  @OneToMany(mappedBy = "patient")
  @OnDelete(action = OnDeleteAction.NO_ACTION)
  public List<Encounter> encounters;

  public Patient() {
  }

  public Patient(Long id, String firstName, String lastName, String ssn, String email,
      String street,
      String city, String state, String postal, Integer age, Integer height, Integer weight,
      String insurance, String gender) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.ssn = ssn;
    this.email = email;
    this.street = street;
    this.city = city;
    this.state = state;
    this.postal = postal;
    this.age = age;
    this.height = height;
    this.weight = weight;
    this.insurance = insurance;
    this.gender = gender;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getSsn() {
    return ssn;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getPostal() {
    return postal;
  }

  public void setPostal(String postal) {
    this.postal = postal;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public Integer getWeight() {
    return weight;
  }

  public void setWeight(Integer weight) {
    this.weight = weight;
  }

  public String getInsurance() {
    return insurance;
  }

  public void setInsurance(String insurance) {
    this.insurance = insurance;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public List<Encounter> getEncounters() {
    return encounters;
  }

  public void setEncounters(
      List<Encounter> encounters) {
    this.encounters = encounters;
  }

  @Override
  public String toString() {
    return "Patient{" +
        "id=" + id +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", ssn='" + ssn + '\'' +
        ", email='" + email + '\'' +
        ", street='" + street + '\'' +
        ", city='" + city + '\'' +
        ", state='" + state + '\'' +
        ", postal='" + postal + '\'' +
        ", age=" + age +
        ", height=" + height +
        ", weight=" + weight +
        ", insurance='" + insurance + '\'' +
        ", gender='" + gender + '\'' +
        ", encounters=" + encounters +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Patient patient = (Patient) o;
    return firstName.equals(patient.firstName) && lastName.equals(patient.lastName) && ssn.equals(
        patient.ssn) && email.equals(patient.email) && street.equals(patient.street) && city.equals(
        patient.city) && state.equals(patient.state) && postal.equals(patient.postal) && age ==
        patient.age && height == patient.height && weight == patient.weight
        && insurance.equals(patient.insurance) && gender.equals(patient.gender);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName, ssn, email, street, city, state, postal, age, height,
        weight, insurance, gender);
  }

}