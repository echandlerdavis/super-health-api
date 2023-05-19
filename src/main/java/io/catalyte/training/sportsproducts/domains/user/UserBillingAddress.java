package io.catalyte.training.sportsproducts.domains.user;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Describes the object for the billing address of the purchase
 */
@Embeddable
public class UserBillingAddress {

    @Nullable
    @Column(nullable = true)
    private String billingStreet;
    @Nullable
    @Column(nullable = true)
    private String billingStreet2;
    @Nullable
    @Column(nullable = true)
    private String billingCity;
    @Nullable
    @Column(nullable = true)
    private String billingState;
    @Nullable
    @Column(nullable = true)
    private int billingZip;
    @Nullable
    @Column(nullable = true)
    private String phone;

    public UserBillingAddress() {
    }

    public UserBillingAddress(String billingStreet, String billingStreet2, String billingCity, String billingState, int billingZip, String phone) {
        this.billingStreet = billingStreet;
        this.billingStreet2 = billingStreet2;
        this.billingCity = billingCity;
        this.billingState = billingState;
        this.billingZip = billingZip;
        this.phone = phone;
    }

    public UserBillingAddress(String billingStreet, String billingStreet2, String billingCity, String billingState, int billingZip) {
        this.billingStreet = billingStreet;
        this.billingStreet2 = billingStreet2;
        this.billingCity = billingCity;
        this.billingState = billingState;
        this.billingZip = billingZip;
    }

    public String getBillingStreet() {
        return billingStreet;
    }

    public void setBillingStreet(String billingStreet) {
        this.billingStreet = billingStreet;
    }

    public String getBillingStreet2() {
        return billingStreet2;
    }

    public void setBillingStreet2(String billingStreet2) {
        this.billingStreet2 = billingStreet2;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingState() {
        return billingState;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public int getBillingZip() {
        return billingZip;
    }

    public void setBillingZip(int billingZip) {
        this.billingZip = billingZip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserBillingAddress that = (UserBillingAddress) o;

        if (getBillingZip() != that.getBillingZip()) {
            return false;
        }
        if (getBillingStreet() != null ? !getBillingStreet().equals(that.getBillingStreet())
                : that.getBillingStreet() != null) {
            return false;
        }
        if (getBillingStreet2() != null ? !getBillingStreet2().equals(that.getBillingStreet2())
                : that.getBillingStreet2() != null) {
            return false;
        }
        if (getBillingCity() != null ? !getBillingCity().equals(that.getBillingCity())
                : that.getBillingCity() != null) {
            return false;
        }
        if (getBillingState() != null ? !getBillingState().equals(that.getBillingState())
                : that.getBillingState() != null) {
            return false;
        }

        return getPhone() != null ? getPhone().equals(that.getPhone()) : that.getPhone() == null;
    }

    @Override
    public int hashCode() {
        int result = getBillingStreet() != null ? getBillingStreet().hashCode() : 0;
        result = 31 * result + (getBillingStreet2() != null ? getBillingStreet2().hashCode() : 0);
        result = 31 * result + (getBillingCity() != null ? getBillingCity().hashCode() : 0);
        result = 31 * result + (getBillingState() != null ? getBillingState().hashCode() : 0);
        result = 31 * result + getBillingZip();
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserBillingAddress{" +
                "billingStreet='" + billingStreet + '\'' +
                ", billingStreet2='" + billingStreet2 + '\'' +
                ", billingCity='" + billingCity + '\'' +
                ", billingState='" + billingState + '\'' +
                ", billingZip=" + billingZip +
                ", phone='" + phone + '\'' +
                '}';
    }
}
