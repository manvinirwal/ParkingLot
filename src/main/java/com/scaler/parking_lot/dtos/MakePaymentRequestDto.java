package com.scaler.parking_lot.dtos;

public class MakePaymentRequestDto {
    private long billId;
    private double amount;
    private String paymentMode; // e.g., "CASH", "ONLINE"
    private String paymentGatewayType; // e.g., "RAZORPAY", "PAYTM" (only relevant for online payments)
    // For online payments, this can be the transaction ID or reference number provided by the payment gateway.

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }
    

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

   

    public String getPaymentGatewayType() {
        return paymentGatewayType;
    }

    public void setPaymentGatewayType(String paymentGatewayType) {
        this.paymentGatewayType = paymentGatewayType;
    }
    
}
