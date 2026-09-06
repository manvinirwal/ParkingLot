package com.scaler.parking_lot.factories;

import com.scaler.parking_lot.adapters.PaymentGateway;
import com.scaler.parking_lot.adapters.PaytmProcessor;
import com.scaler.parking_lot.adapters.RazrorpayProcessor;

public class PaymentGatewayFactory {
    public static PaymentGateway getPaymentGateway(String gatewayType) throws IllegalArgumentException {
        if (gatewayType.equalsIgnoreCase("razorpay")) {
            return new RazrorpayProcessor();
        }
        else if(gatewayType.equalsIgnoreCase("paytm")) {
             return new PaytmProcessor();
        }
        
        // Add more gateways as needed
        throw new IllegalArgumentException("Unsupported payment gateway: " + gatewayType);
    }
    
}
