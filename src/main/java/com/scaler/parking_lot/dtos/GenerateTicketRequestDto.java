package com.scaler.parking_lot.dtos;

import com.scaler.parking_lot.models.ParkingAttendant;

public class GenerateTicketRequestDto {
    private int gateId;
    private String registrationNumber;
    private String vehicleType;
    private ParkingAttendant parkingAttendant;

    

    public int getGateId() {
        return gateId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setGateId(int gateId) {
        this.gateId = gateId;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public ParkingAttendant getParkingAttendant() {
        return parkingAttendant;
    }

    public void setParkingAttendant(ParkingAttendant parkingAttendant) {
        this.parkingAttendant = parkingAttendant;
    }
}
