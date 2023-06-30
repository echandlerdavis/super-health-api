package io.catalyte.training.superhealth.domains.encounter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.catalyte.training.superhealth.domains.patient.Patient;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder.In;

public class EncounterDTO {

    private Long patientId;

    private String notes;

    private String visitCode;

    private String provider;

    private String billingCode;

    private String icd10;

    private Double totalCost;

    private Double copay;

    private String chiefComplaint;

    private Integer pulse;

    private Integer systolic;

    private Integer diastolic;

    private LocalDate date;

    public EncounterDTO() {
    }

    public EncounterDTO(Long patientId, String notes, String visitCode, String provider,
        String billingCode, String icd10, Double totalCost, Double copay, String chiefComplaint,
        Integer pulse, Integer systolic, Integer diastolic, LocalDate date) {
      this.patientId = patientId;
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
    public Long getPatientId() {
      return patientId;
    }

    public void setPatientId(Long patientId) {
      this.patientId = patientId;
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

    public Double getTotalCost() {
      return totalCost;
    }

    public void setTotalCost(Double totalCost) {
      this.totalCost = totalCost;
    }

    public double getCopay() {
      return copay;
    }

    public void setCopay(Double copay) {
      this.copay = copay;
    }

    public String getChiefComplaint() {
      return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
      this.chiefComplaint = chiefComplaint;
    }

    public Integer getPulse() {
      return pulse;
    }

    public void setPulse(Integer pulse) {
      this.pulse = pulse;
    }

    public Integer getSystolic() {
      return systolic;
    }

    public void setSystolic(Integer systolic) {
      this.systolic = systolic;
    }

    public Integer getDiastolic() {
      return diastolic;
    }

    public void setDiastolic(Integer diastolic) {
      this.diastolic = diastolic;
    }

    public LocalDate getDate() {
      return date;
    }

    public void setDate(LocalDate date) {
      this.date = date;
    }

  }
