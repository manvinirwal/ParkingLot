package com.scaler.parking_lot.services;

import java.util.Date;

import com.scaler.parking_lot.exceptions.GenerateBillRequestValidationException;
import com.scaler.parking_lot.exceptions.InvalidGateException;
import com.scaler.parking_lot.models.Bill;
import com.scaler.parking_lot.models.Gate;
import com.scaler.parking_lot.models.ParkingAttendant;
import com.scaler.parking_lot.models.Ticket;
import com.scaler.parking_lot.models.enums.BillStatus;
import com.scaler.parking_lot.models.enums.GateType;
import com.scaler.parking_lot.respositories.InMemoryBillRepository;
import com.scaler.parking_lot.respositories.InMemoryGateRepository;
import com.scaler.parking_lot.respositories.InMemoryTicketRepository;
import com.scaler.parking_lot.strategies.FeeCalculationStrategy;

public class BillServiceImpl  implements BillService {

    private final InMemoryTicketRepository ticketRepository;
    private final InMemoryBillRepository billRepository;
    private final InMemoryGateRepository gateRepository;
    private final FeeCalculationStrategy feeCalculationStrategy;


    



    public BillServiceImpl(InMemoryTicketRepository ticketRepository, InMemoryBillRepository billRepository,
            InMemoryGateRepository gateRepository, FeeCalculationStrategy feeCalculationStrategy) {
        this.ticketRepository = ticketRepository;
        this.billRepository = billRepository;
        this.gateRepository = gateRepository;
        this.feeCalculationStrategy = feeCalculationStrategy;
    }






    @Override
    public Bill generateBill(long ticketId, Date exitTime, long gateId, ParkingAttendant parkingAttendant) throws InvalidGateException, GenerateBillRequestValidationException
         {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new GenerateBillRequestValidationException("Invalid ticket ID"));

        Gate gate = gateRepository.findById(gateId)
                .orElseThrow(() -> new GenerateBillRequestValidationException("Invalid gate ID"));

        if(gate.getType() != GateType.EXIT) {
            throw new InvalidGateException("Gate is not an exit gate");
        }

        double amount = feeCalculationStrategy.calculateFee(ticket, exitTime);



        Bill bill = new Bill();

        bill.setExitTime(exitTime);
        bill.setAmount(amount);
        bill.setTicket(ticket);
        bill.setGeneratedAt(gate);
        bill.setGeneratedBy(parkingAttendant);  
        bill.setBillStatus(BillStatus.UNPAID);

        bill = billRepository.save(bill);


        return bill;


        
    }

    


   
    
    
}
