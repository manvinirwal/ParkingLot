package com.scaler.parking_lot.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scaler.parking_lot.exceptions.GetParkingLotCapacityRequestValidationException;

import com.scaler.parking_lot.exceptions.InvalidParkingLotException;
import com.scaler.parking_lot.models.ParkingFloor;
import com.scaler.parking_lot.models.ParkingLot;
import com.scaler.parking_lot.models.ParkingSpot;
import com.scaler.parking_lot.models.enums.ParkingSpotStatus;
import com.scaler.parking_lot.models.enums.VehicleType;
import com.scaler.parking_lot.respositories.InMemoryParkingLotRepository;


public class CapacityServiceImpl implements CapacityService {
    private final InMemoryParkingLotRepository parkingLotRepository;

    

    public CapacityServiceImpl(InMemoryParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }



    @Override
    public Map<ParkingFloor, Map<String, Integer>> getParkingLotCapacity(long parkingLotId, List<Long> parkingFloors,
            List<String> list) throws InvalidParkingLotException, GetParkingLotCapacityRequestValidationException {

            ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                                                .orElseThrow(() ->  new InvalidParkingLotException("Invalid parking lot id"));

            if(parkingFloors == null){
                    parkingFloors = parkingLot.getParkingFloors()
                                            .stream().map(ParkingFloor::getId).toList();
            }

            List<VehicleType> vehicleTypes;
            if(list == null){
                vehicleTypes = List.of(VehicleType.values());
            }else{
                vehicleTypes = new ArrayList<>();
                for(String s : list){
                   

                    try{
                        vehicleTypes.add(VehicleType.valueOf(s.toUpperCase()));

                    }catch (Exception e){
                        throw new GetParkingLotCapacityRequestValidationException("Invalid vehicle type");
                    }
                }
            }


            Map<ParkingFloor, Map<String, Integer>> capacity = new HashMap<>();
            

            for(long floorId : parkingFloors){

                ParkingFloor parkingFloor =parkingLot.getParkingFloors().stream()
                            .filter(floor -> floor.getId() == floorId).findFirst()
                            .orElseThrow(() -> new GetParkingLotCapacityRequestValidationException("Invalid floor"));

                List<ParkingSpot> spots = parkingFloor.getSpots();

                Map<String, Integer> floorCapacity = new HashMap<>();

                for(ParkingSpot spot : spots ){
                    if (spot.getParkingSpotStatus() != ParkingSpotStatus.AVAILABLE) {
                        continue;
                    }
                    List<VehicleType> spotVehicleTypes = spot.getSupportedVehicleTypes();

                    for (VehicleType spotVehicleType : spotVehicleTypes) {
                        if (!vehicleTypes.contains(spotVehicleType)) {
                            continue;
                        }

                        String vehicleTypeName = spotVehicleType.name();

                        floorCapacity.merge(vehicleTypeName, 1, Integer::sum);
                    }

                }

                capacity.put(parkingFloor, floorCapacity);
                

            }




            return capacity;

        
    }
    
}
