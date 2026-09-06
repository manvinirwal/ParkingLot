package com.scaler.parking_lot.respositories;


import java.util.Optional;

import com.scaler.parking_lot.models.ParkingLot;

public class InMemoryParkingLotRepository extends InMemoryRepository<ParkingLot> {
      

    public Optional<ParkingLot> getParkingLotByGateId(long gateId) {
        
       return  items.values().stream()
          .filter(lot -> lot.getGates().stream().anyMatch( gate -> gate.getId() == gateId)).findFirst();

        
    }

    
}
