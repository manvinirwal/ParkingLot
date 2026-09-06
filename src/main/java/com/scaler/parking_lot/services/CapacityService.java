package com.scaler.parking_lot.services;

import java.util.List;
import java.util.Map;

import com.scaler.parking_lot.exceptions.GetParkingLotCapacityRequestValidationException;
import com.scaler.parking_lot.exceptions.InvalidParkingLotException;
import com.scaler.parking_lot.models.ParkingFloor;

public interface CapacityService {
    public Map<ParkingFloor, Map<String, Integer>> getParkingLotCapacity(long parkingLotId, List<Long> parkingFloors,
            List<String> list) throws InvalidParkingLotException, GetParkingLotCapacityRequestValidationException;
    
}
