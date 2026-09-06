package com.scaler.parking_lot.models;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.scaler.parking_lot.models.enums.BillStatus;
import com.scaler.parking_lot.models.enums.PaymentStatus;  


public class Bill extends BaseModel {
    private Date exitTime;
    private double amount;
    private double paidAmount;
    private Ticket ticket;
    private Gate generatedAt;
    private ParkingAttendant  generatedBy;
    private BillStatus billStatus;
    private List<Payment> payments = new ArrayList<>();



    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public Ticket getTicket() {
        return ticket;
    }
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
   
    public ParkingAttendant getGeneratedBy() {
        return generatedBy;
    }
    public void setGeneratedBy(ParkingAttendant generatedBy) {
        this.generatedBy = generatedBy;
    }
    public BillStatus getBillStatus() {
        return billStatus;
    }
    public void setBillStatus(BillStatus billStatus) {
        this.billStatus = billStatus;
    }
    public List<Payment> getPayments() {
        return payments;
    }
    
    public Gate getGeneratedAt() {
        return generatedAt;
    }
    public void setGeneratedAt(Gate generatedAt) {
        this.generatedAt = generatedAt;
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        updateBillStatus();
    }
    

    private void updateBillStatus() {
        double totalPaid = 0;

        for (Payment payment : payments) {
            if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
                totalPaid += payment.getAmount();
            }
        }

        paidAmount = totalPaid;

        if (totalPaid >= amount) {
            billStatus = BillStatus.PAID;
        } else {
            if (totalPaid == 0) {
                billStatus = BillStatus.UNPAID;
            } else {
                billStatus = BillStatus.PARTIALLY_PAID;
            }
        }
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }





   
}