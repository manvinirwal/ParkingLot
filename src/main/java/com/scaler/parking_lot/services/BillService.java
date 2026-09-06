package com.scaler.parking_lot.services;

import java.util.Date;

import com.scaler.parking_lot.models.Bill;
import com.scaler.parking_lot.models.ParkingAttendant;
import com.scaler.parking_lot.exceptions.GenerateBillRequestValidationException;
import com.scaler.parking_lot.exceptions.InvalidGateException;

public interface BillService {
     public Bill generateBill(long ticketId, Date exitTime, long gateId, ParkingAttendant parkingAttendant) throws InvalidGateException, GenerateBillRequestValidationException  ;
       
    
}
