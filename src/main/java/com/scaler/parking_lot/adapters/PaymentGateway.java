package com.scaler.parking_lot.adapters;

import com.scaler.parking_lot.models.Payment;

public interface PaymentGateway {

    Payment processPayment(long orderId, double amount);
    
}
