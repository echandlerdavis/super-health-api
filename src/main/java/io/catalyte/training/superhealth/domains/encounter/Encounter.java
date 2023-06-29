package io.catalyte.training.superhealth.domains.encounter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.catalyte.training.superhealth.domains.patient.Patient;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * This class is a representation of a patient encounter.
 */
@Entity
public class Encounter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "patientId", referencedColumnName = "id")
  @JsonIgnore
  private Patient patient;

//  @Column(insertable = false, updatable = false)
//  private Long patientId;

  private String notes;

  private String visitCode;

  private String provider;

  private String billingCode;

  private String icd10;

  private double totalCost;

  private double copay;

  private String chiefComplaint;

  private int pulse;

  private int systolic;

  private int diastolic;

  private LocalDate date;

  public Encounter() {
  }

  public Encounter(Long id, Patient patient, String notes, String visitCode, String provider,
      String billingCode, String icd10, double totalCost, double copay, String chiefComplaint,
      int pulse, int systolic, int diastolic, LocalDate date) {
    this.id = id;
    this.patient = patient;
    this.notes = notes;
    this.visitCode = visitCode;
    this.provider = provider;
    this.billingCode = billingCode;
    this.icd10 = icd10;
    this.totalCost = totalCost;
    this.copay = copay;
    this.chiefComplaint = chiefComplaint;
    this.pulse = pulse;
    this.systolic = systolic;
    this.diastolic = diastolic;
    this.date = date;
  }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getVisitCode() {
    return visitCode;
  }

  public void setVisitCode(String visitCode) {
    this.visitCode = visitCode;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getBillingCode() {
    return billingCode;
  }

  public void setBillingCode(String billingCode) {
    this.billingCode = billingCode;
  }

  public String getIcd10() {
    return icd10;
  }

  public void setIcd10(String icd10) {
    this.icd10 = icd10;
  }

  public double getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(double totalCost) {
    this.totalCost = totalCost;
  }

  public double getCopay() {
    return copay;
  }

  public void setCopay(double copay) {
    this.copay = copay;
  }

  public String getChiefComplaint() {
    return chiefComplaint;
  }

  public void setChiefComplaint(String chiefComplaint) {
    this.chiefComplaint = chiefComplaint;
  }

  public int getPulse() {
    return pulse;
  }

  public void setPulse(int pulse) {
    this.pulse = pulse;
  }

  public int getSystolic() {
    return systolic;
  }

  public void setSystolic(int systolic) {
    this.systolic = systolic;
  }

  public int getDiastolic() {
    return diastolic;
  }

  public void setDiastolic(int diastolic) {
    this.diastolic = diastolic;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Encounter encounter = (Encounter) o;
    return Double.compare(encounter.totalCost, totalCost) == 0
        && Double.compare(encounter.copay, copay) == 0 && pulse == encounter.pulse
        && systolic == encounter.systolic && diastolic == encounter.diastolic && patient.equals(
        encounter.patient) && notes.equals(encounter.notes) && visitCode.equals(encounter.visitCode)
        && provider.equals(encounter.provider) && billingCode.equals(encounter.billingCode)
        && icd10.equals(encounter.icd10) && chiefComplaint.equals(encounter.chiefComplaint)
        && date.equals(encounter.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(patient, notes, visitCode, provider, billingCode, icd10, totalCost, copay,
        chiefComplaint, pulse, systolic, diastolic, date);
  }

}
