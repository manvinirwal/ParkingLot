package com.scaler.parking_lot.models;
import java.util.Date;

import com.scaler.parking_lot.models.enums.PaymentMode;
import com.scaler.parking_lot.models.enums.PaymentStatus;  

public class Payment extends BaseModel {
    private PaymentMode paymentMode;
    private Bill bill;
    private double amount;
    private Date time;
    private PaymentStatus paymentStatus;
    private String referenceNumber;// For online payments, this can be the transaction ID or reference number provided by the payment gateway.
    // For cash payments, this can be a receipt number or any other identifier associated with the cash transaction.
   
   
    public PaymentMode getPaymentMode() {
        return paymentMode;
    }
    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }
    public Bill getBill() {
        return bill;
    }
    public void setBill(Bill bill) {
        this.bill = bill;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public String getReferenceNumber() {
        return referenceNumber;
    }
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    



    
}
