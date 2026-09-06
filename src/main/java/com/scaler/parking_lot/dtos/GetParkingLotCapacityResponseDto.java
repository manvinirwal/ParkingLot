package com.scaler.parking_lot.dtos;

import com.scaler.parking_lot.models.ParkingFloor;

import java.util.Map;

public class GetParkingLotCapacityResponseDto {

    private ResponseStatus responseStatus;

    private Map<ParkingFloor, Map<String, Integer>> capacityMap;

    public Map<ParkingFloor, Map<String, Integer>> getCapacityMap() {
        return capacityMap;
    }

    public void setCapacityMap(Map<ParkingFloor, Map<String, Integer>> capacityMap) {
        this.capacityMap = capacityMap;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

   
}
