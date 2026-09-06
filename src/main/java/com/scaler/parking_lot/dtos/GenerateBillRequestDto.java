package com.scaler.parking_lot.dtos;

import java.util.Date;


import com.scaler.parking_lot.models.ParkingAttendant;


public class GenerateBillRequestDto {
    private long ticketId;
    private Date exitTime;
    private ParkingAttendant parkingAttendant;
    private long gateId;

    
   
    public Date getExitTime() {
        return exitTime;
    }
    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }
    public ParkingAttendant getParkingAttendant() {
        return parkingAttendant;
    }
    public void setParkingAttendant(ParkingAttendant parkingAttendant) {
        this.parkingAttendant = parkingAttendant;
    }
    public long getTicketId() {
        return ticketId;
    }
    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }
    public long getGateId() {
        return gateId;
    }
    public void setGateId(long gateId) {
        this.gateId = gateId;
    }
    
   


    
    

    

    
}
