package com.scaler.parking_lot.adapters;

import com.scaler.parking_lot.libraries.paytm.PaytmApi;
import com.scaler.parking_lot.libraries.paytm.PaytmPaymentResponse;
import com.scaler.parking_lot.models.Payment;
import com.scaler.parking_lot.models.enums.PaymentStatus;

public class PaytmProcessor implements PaymentGateway {
    private static final PaytmApi paytmApi = new PaytmApi();
    @Override
    public Payment processPayment(long orderId, double amount) {
        // Implement Paytm payment processing logic here
        //call the Paytm API to initiate the payment
        // and return a Payment object with the payment details.
        PaytmPaymentResponse paytmPaymentResponse = paytmApi.makePayment(orderId, amount);

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.valueOf(paytmPaymentResponse.getPaymentStatus()));
        payment.setReferenceNumber(paytmPaymentResponse.getTxnId());
        payment.setAmount(paytmPaymentResponse.getTxnAmount());
        payment.setTime(paytmPaymentResponse.getTxnDate());
        
        return payment;

    }
    
}
