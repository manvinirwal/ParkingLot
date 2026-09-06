package com.scaler.parking_lot.models;

import java.util.List;

import com.scaler.parking_lot.models.enums.VehicleType;
import com.scaler.parking_lot.strategies.FeeCalculationStrategy;
import com.scaler.parking_lot.strategies.SpotAssignmentStrategy;

public class ParkingLot extends BaseModel{

    private List<ParkingFloor> parkingFloors;
    private List<Gate> gates;
    private List<VehicleType> vehicleTypes;
    private SpotAssignmentStrategy spotAssignmentStrategy;
    private FeeCalculationStrategy feesCalculationStrategy;

    
    
    public ParkingLot(List<ParkingFloor> parkingFloors, List<Gate> gates, List<VehicleType> vehicleTypes,
            SpotAssignmentStrategy spotAssignmentStrategy, FeeCalculationStrategy feesCalculationStrategy) {
        this.parkingFloors = parkingFloors;
        this.gates = gates;
        this.vehicleTypes = vehicleTypes;
        this.spotAssignmentStrategy = spotAssignmentStrategy;
        this.feesCalculationStrategy = feesCalculationStrategy;
    }

    public List<ParkingFloor> getParkingFloors() {
        return parkingFloors;
    }

    public void setParkingFloors(List<ParkingFloor> parkingFloors) {
        this.parkingFloors = parkingFloors;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public void setGates(List<Gate> gates) {
        this.gates = gates;
    }

    public List<VehicleType> getVehicleTypes() {
        return vehicleTypes;
    }

    public void setVehicleTypes(List<VehicleType> vehicleTypes) {
        this.vehicleTypes = vehicleTypes;
    }

    public SpotAssignmentStrategy getSpotAssignmentStrategy() {
        return spotAssignmentStrategy;
    }

    public void setSpotAssignmentStrategy(SpotAssignmentStrategy spotAssignmentStrategy) {
        this.spotAssignmentStrategy = spotAssignmentStrategy;
    }

    public FeeCalculationStrategy getFeesCalculationStrategy() {
        return feesCalculationStrategy;
    }

    public void setFeesCalculationStrategy(FeeCalculationStrategy feesCalculationStrategy) {
        this.feesCalculationStrategy = feesCalculationStrategy;
    }
    

   
}
