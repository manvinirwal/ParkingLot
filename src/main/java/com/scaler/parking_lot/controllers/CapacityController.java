package com.scaler.parking_lot.controllers;

import java.util.Map;

import com.scaler.parking_lot.dtos.GetParkingLotCapacityRequestDto;
import com.scaler.parking_lot.dtos.GetParkingLotCapacityResponseDto;
import com.scaler.parking_lot.dtos.ResponseStatus;
import com.scaler.parking_lot.models.ParkingFloor;
import com.scaler.parking_lot.services.CapacityService;

public class CapacityController {

    private final CapacityService capacityService;


    public CapacityController(CapacityService capacityService) {
        this.capacityService = capacityService;
    }

    public GetParkingLotCapacityResponseDto getParkingLotCapacity(GetParkingLotCapacityRequestDto getParkingLotCapacityRequestDto) {
        GetParkingLotCapacityResponseDto responseDto = new GetParkingLotCapacityResponseDto();
        

        try{
            Map<ParkingFloor, Map<String, Integer>> capacity
                         = capacityService.getParkingLotCapacity(getParkingLotCapacityRequestDto.getParkingLotId(),
                         getParkingLotCapacityRequestDto.getParkingFloorIds(),  
                         getParkingLotCapacityRequestDto.getVehicleTypes());

           responseDto.setCapacityMap(capacity);
           responseDto.setResponseStatus(ResponseStatus.SUCCESS);

        }catch(Exception e){
            e.printStackTrace();
            responseDto.setResponseStatus(ResponseStatus.FAILURE);

        }
        return responseDto;
    }
    
}
