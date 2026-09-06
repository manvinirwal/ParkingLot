package com.scaler.parking_lot.strategies;

import java.util.Date;

import com.scaler.parking_lot.models.Ticket;

public class HourlyFeeCalculationStrategy implements FeeCalculationStrategy {
    private static final double HOURLY_RATE = 20.0; // Example hourly rate

    @Override
    public double calculateFee(Ticket ticket, Date exitTime) {
        // Implement the logic to calculate the fee based on hourly rates
        // For example, you can calculate the duration of parking and multiply it by an hourly rate
        long durationInMillis = exitTime.getTime() - ticket.getEntryTime().getTime();
        long durationInHours = (long) Math.ceil(durationInMillis / 3600000L); // Round up to the next hour
        durationInHours = Math.max(durationInHours, 1); // Ensure at least 1 hour is charged
        return  (durationInHours * HOURLY_RATE);
    }

   
    
}
