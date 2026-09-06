package com.scaler.parking_lot.strategies;

import java.util.Date;

import com.scaler.parking_lot.models.Ticket;

public interface FeeCalculationStrategy {

    double calculateFee(Ticket ticket, Date exitTime);
    
}
