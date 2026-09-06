package com.scaler.parking_lot.services;

import com.scaler.parking_lot.exceptions.InvalidBillException;
import com.scaler.parking_lot.exceptions.PaymentFailedException;
import com.scaler.parking_lot.models.Payment;

public interface PaymentService {

    public Payment makePayment(long billId, double amount, String paymentMode, String paymentGatewayType) throws InvalidBillException, PaymentFailedException;
    
    
} 
