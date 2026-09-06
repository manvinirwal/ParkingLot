package com.scaler.parking_lot.controllers;

import com.scaler.parking_lot.dtos.GenerateBillRequestDto;
import com.scaler.parking_lot.dtos.GenerateBillResponseDto;
import com.scaler.parking_lot.services.BillService;
import com.scaler.parking_lot.dtos.ResponseStatus;
import com.scaler.parking_lot.models.Bill;

public class BillController {
    private final BillService billService;

    

    public BillController(BillService billService) {
        this.billService = billService;
    }



    public GenerateBillResponseDto generateBill(GenerateBillRequestDto requestDto) {

        GenerateBillResponseDto responseDto = new GenerateBillResponseDto();

        try{
            Bill bill = billService.generateBill(requestDto.getTicketId(), requestDto.getExitTime(), requestDto.getGateId(), requestDto.getParkingAttendant());
            responseDto.setBillId(bill.getId());
            responseDto.setAmount(bill.getAmount());
            responseDto.setBillStatus(bill.getBillStatus());
            responseDto.setExitTime(bill.getExitTime());
            responseDto.setEntryTime(bill.getTicket().getEntryTime());
            responseDto.setResponseStatus(ResponseStatus.SUCCESS);   

        }catch(Exception e){
            e.printStackTrace();
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }


       


        return responseDto;
    }

    
}
