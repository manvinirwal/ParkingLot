package com.scaler.parking_lot.services;

import java.util.Date;

import com.scaler.parking_lot.adapters.PaymentGateway;
import com.scaler.parking_lot.exceptions.InvalidBillException;
import com.scaler.parking_lot.exceptions.PaymentFailedException;
import com.scaler.parking_lot.factories.PaymentGatewayFactory;
import com.scaler.parking_lot.models.Bill;
import com.scaler.parking_lot.models.Payment;
import com.scaler.parking_lot.models.enums.BillStatus;
import com.scaler.parking_lot.models.enums.PaymentMode;
import com.scaler.parking_lot.models.enums.PaymentStatus;
import com.scaler.parking_lot.respositories.InMemoryBillRepository;
import com.scaler.parking_lot.respositories.InMemoryPaymentRepository;

public class PaymentServiceImpl implements PaymentService {

    private final InMemoryBillRepository billRepository;
    private final InMemoryPaymentRepository paymentRepository;

    public PaymentServiceImpl(InMemoryBillRepository billRepository, InMemoryPaymentRepository paymentRepository) {
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
    }


    @Override
    public Payment makePayment(long billId, double amount, String paymentMode, String paymentGatewayType)
            throws InvalidBillException, PaymentFailedException {
        // Validate the bill
        // Fetch the bill from the repository
        // If the bill is invalid, throw InvalidBillException 

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new InvalidBillException("Invalid bill ID"));  
        

        if (bill.getBillStatus() == BillStatus.PAID) {
            throw new PaymentFailedException("Bill is already paid");
        }
         if (amount <= 0) {
            throw new PaymentFailedException("Invalid payment amount");
        }
                
        // Process the payment using the appropriate payment gateway
        // If the payment fails, throw PaymentFailedException
        // If the payment is successful, create a Payment object and save it to the repository
        // add payment object in bill and return the Payment object


        PaymentMode paymentModeEnum;
        try {
            paymentModeEnum = PaymentMode.valueOf(paymentMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PaymentFailedException("Invalid payment mode: " + paymentMode);
        }   

        if(paymentModeEnum == PaymentMode.ONLINE && (paymentGatewayType == null || paymentGatewayType.isEmpty())) {
            throw new PaymentFailedException("Payment gateway type is required for online payments");
        }
        

        Payment payment;

        if(paymentModeEnum == PaymentMode.ONLINE) {
            // Process online payment using the specified payment gateway
            try{
                PaymentGateway paymentGateway = PaymentGatewayFactory.getPaymentGateway(paymentGatewayType);
                payment = paymentGateway.processPayment(billId, amount);
                payment.setPaymentMode(paymentModeEnum);
                payment.setBill(bill);
                paymentRepository.save(payment);
                bill.addPayment(payment);
                billRepository.save(bill);
               
            } catch (Exception e) {
                throw new PaymentFailedException("Payment failed: " + e.getMessage());
            }
            

        } else {
            // Process cash payment and assume it's always successful
            payment = new Payment();
            payment.setPaymentMode(paymentModeEnum);
            payment.setBill(bill);
            payment.setAmount(amount);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTime(new Date());
            paymentRepository.save(payment);
            bill.addPayment(payment);
            billRepository.save(bill);
        }



        return payment;
        
    }
    
    
}
