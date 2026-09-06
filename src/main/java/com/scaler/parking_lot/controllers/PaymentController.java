package com.scaler.parking_lot.controllers;

import com.scaler.parking_lot.dtos.MakePaymentRequestDto;
import com.scaler.parking_lot.dtos.MakePaymentResponseDto;
import com.scaler.parking_lot.dtos.ResponseStatus;
import com.scaler.parking_lot.exceptions.InvalidBillException;
import com.scaler.parking_lot.exceptions.PaymentFailedException;
import com.scaler.parking_lot.models.Payment;
import com.scaler.parking_lot.services.PaymentService;

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public MakePaymentResponseDto makePayment(MakePaymentRequestDto requestDto) {

        MakePaymentResponseDto responseDto = new MakePaymentResponseDto();

        try{
            Payment payment = paymentService.makePayment(requestDto.getBillId(), requestDto.getAmount(), requestDto.getPaymentMode(), requestDto.getPaymentGatewayType());
            responseDto.setReferenceNumber(payment.getReferenceNumber());
            responseDto.setPaymentStatus(payment.getPaymentStatus().toString());

            responseDto.setResponseStatus(ResponseStatus.SUCCESS);
        } catch (InvalidBillException | PaymentFailedException e) {
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }




        
        return responseDto;
    }
    
    
}
