package com.scaler.parking_lot.strategies;

import java.util.Comparator;
import java.util.Optional;

import com.scaler.parking_lot.models.ParkingFloor;
import com.scaler.parking_lot.models.ParkingLot;
import com.scaler.parking_lot.models.ParkingSpot;
import com.scaler.parking_lot.models.enums.FloorStatus;
import com.scaler.parking_lot.models.enums.ParkingSpotStatus;
import com.scaler.parking_lot.models.enums.VehicleType;

public class NearestSpotAssignmentStrategy implements SpotAssignmentStrategy {

    // This strategy will assign the nearest available spot to the vehicle based on the floor number and spot number.
    //Following are the rules for assigning a spot to a vehicle.
    // * When a vehicle arrives, the system should assign it to the floor with the least number
    //   of available spots for that vehicle type.
    // * If there are multiple floors with same number of available spots, the system should
    //   assign the vehicle to the floor with the lowest floor number.
    // * If a floor is operational, then only it should be considered, otherwise the system should ignore that floor.
    // * Once a floor has been selected, the system should assign the vehicle to the nearest available spot of that vehicle type on that floor.
    // * If there are no available spots on any floor, the system should not issue a ticket.

    @Override
    public Optional<ParkingSpot> assignSpot(
            ParkingLot parkingLot,
            VehicleType vehicleType) {

        long countSlots = Long.MAX_VALUE;
        ParkingFloor selectedFloor = null;

        for (ParkingFloor floor : parkingLot.getParkingFloors()) {

            if (floor.getStatus() != FloorStatus.OPERATIONAL) {
                continue;
            }

            long count = floor.getSpots().stream()
                    .filter(spot ->
                            spot.getSupportedVehicleTypes().contains(vehicleType)
                            && spot.getParkingSpotStatus() == ParkingSpotStatus.AVAILABLE)
                    .count();

            if (count == 0) {
                continue;
            }
            //break tie if eqyals
            if (count < countSlots ||
                    (count == countSlots &&
                      floor.getFloorNumber() < selectedFloor.getFloorNumber())) {

                countSlots = count;
                selectedFloor = floor;
            }
        }

        if (selectedFloor == null) {
            return Optional.empty();
        }

        return selectedFloor.getSpots().stream()
                .filter(spot ->
                        spot.getParkingSpotStatus() == ParkingSpotStatus.AVAILABLE
                        && spot.getSupportedVehicleTypes().contains(vehicleType))
                .min(Comparator.comparingInt(ParkingSpot::getSpotNumber));
    }
}