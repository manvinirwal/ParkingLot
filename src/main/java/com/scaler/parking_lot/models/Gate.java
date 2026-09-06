package com.scaler.parking_lot.models;

import com.scaler.parking_lot.models.enums.GateType;

public class Gate extends BaseModel{

    private int gateNumber;
    private GateType type;
    private ParkingAttendant parkingAttendant;



    public GateType getType() {
        return type;
    }

    public void setType(GateType type) {
        this.type = type;
    }

    public ParkingAttendant getParkingAttendant() {
        return parkingAttendant;
    }

    public void setParkingAttendant(ParkingAttendant parkingAttendant) {
        this.parkingAttendant = parkingAttendant;
    }

    public int getGateNumber() {
        return gateNumber;
    }

    public void setGateNumber(int gateNumber) {
        this.gateNumber = gateNumber;
    }
}
