package com.scaler.parking_lot.respositories;



import java.util.Optional;

import com.scaler.parking_lot.models.Vehicle;

public class InMemoryVehicleRepository extends InMemoryRepository<Vehicle>  {

    

    
    public Optional<Vehicle> getVehicleByRegistrationNumber(String registrationNumber) {

       
        return items.values().stream().filter(v -> v.getRegistrationNumber().equals(registrationNumber)).findFirst();
        
    }
    
}
