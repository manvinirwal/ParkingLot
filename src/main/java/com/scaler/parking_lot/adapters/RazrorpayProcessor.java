package com.scaler.parking_lot.adapters;

import com.scaler.parking_lot.libraries.razorpay.RazorpayApi;
import com.scaler.parking_lot.libraries.razorpay.RazorpayPaymentResponse;
import com.scaler.parking_lot.models.Payment;
import com.scaler.parking_lot.models.enums.PaymentStatus;

public class RazrorpayProcessor implements PaymentGateway {

    private static final RazorpayApi razorpayApi = new RazorpayApi();

    
    @Override
    public Payment processPayment(long orderId, double amount) {
        // Implement Razorpay payment processing logic here
        //  call the Razorpay API to initiate the payment
        // and return a Payment object with the payment details.

        RazorpayPaymentResponse razorpayPaymentResponse  = razorpayApi.processPayment(orderId, amount);

        Payment payment = new Payment();

        payment.setPaymentStatus(PaymentStatus.valueOf(razorpayPaymentResponse.getPaymentStatus()));
        payment.setReferenceNumber(razorpayPaymentResponse.getTransactionId()); 
        payment.setAmount(razorpayPaymentResponse.getTransactionAmount());
        payment.setTime(razorpayPaymentResponse.getTransactionDate());


        return payment;


        
    }

    
}
