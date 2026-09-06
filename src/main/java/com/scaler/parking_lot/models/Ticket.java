package com.scaler.parking_lot.models;

import java.util.Date;

public class Ticket extends BaseModel{
    private Vehicle vehicle;
    private String ticketNumber;
    

    private Date entryTime;

    private ParkingSpot parkingSpot;
    private Gate genratedAt;
    private ParkingAttendant generatedBy;

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }
    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public Gate getGenratedAt() {
        return genratedAt;
    }

    public void setGenratedAt(Gate genratedAt) {
        this.genratedAt = genratedAt;
    }

    public ParkingAttendant getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(ParkingAttendant generatedBy) {
        this.generatedBy = generatedBy;
    }

   
}
