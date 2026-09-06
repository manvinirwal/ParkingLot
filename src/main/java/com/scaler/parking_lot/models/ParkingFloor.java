package com.scaler.parking_lot.models;

import java.util.List;

import com.scaler.parking_lot.models.enums.FloorStatus;
import com.scaler.parking_lot.models.enums.ParkingSpotStatus;
import com.scaler.parking_lot.models.enums.VehicleType;

public class ParkingFloor extends BaseModel{

    private List<ParkingSpot> spots;
    private int floorNumber;
    private FloorStatus status;

    public List<ParkingSpot> getSpots() {
        return spots;
    }

    public void setSpots(List<ParkingSpot> spots) {
        this.spots = spots;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public FloorStatus getStatus() {
        return status;
    }

    public void setStatus(FloorStatus status) {
        this.status = status;
    }

    public boolean hasAvailableSpot(VehicleType vehicleType) {
        return spots.stream()
                .anyMatch(spot ->
                        spot.getSupportedVehicleTypes().contains(vehicleType)
                        && spot.getParkingSpotStatus() == ParkingSpotStatus.AVAILABLE);
    }
}
