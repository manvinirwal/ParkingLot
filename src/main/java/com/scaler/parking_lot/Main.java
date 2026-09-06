
package com.scaler.parking_lot;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.scaler.parking_lot.controllers.BillController;
import com.scaler.parking_lot.controllers.CapacityController;
import com.scaler.parking_lot.controllers.PaymentController;
import com.scaler.parking_lot.controllers.TicketController;
import com.scaler.parking_lot.dtos.GenerateBillRequestDto;
import com.scaler.parking_lot.dtos.GenerateBillResponseDto;
import com.scaler.parking_lot.dtos.GenerateTicketRequestDto;
import com.scaler.parking_lot.dtos.GenerateTicketResponseDto;
import com.scaler.parking_lot.dtos.GetParkingLotCapacityRequestDto;
import com.scaler.parking_lot.dtos.GetParkingLotCapacityResponseDto;
import com.scaler.parking_lot.dtos.MakePaymentRequestDto;
import com.scaler.parking_lot.dtos.MakePaymentResponseDto;
import com.scaler.parking_lot.dtos.ResponseStatus;
import com.scaler.parking_lot.models.Bill;
import com.scaler.parking_lot.models.Gate;
import com.scaler.parking_lot.models.ParkingAttendant;
import com.scaler.parking_lot.models.ParkingFloor;
import com.scaler.parking_lot.models.ParkingLot;
import com.scaler.parking_lot.models.ParkingSpot;
import com.scaler.parking_lot.models.Ticket;
import com.scaler.parking_lot.models.enums.FloorStatus;
import com.scaler.parking_lot.models.enums.GateType;
import com.scaler.parking_lot.models.enums.ParkingSpotStatus;
import com.scaler.parking_lot.models.enums.VehicleType;
import com.scaler.parking_lot.respositories.InMemoryBillRepository;
import com.scaler.parking_lot.respositories.InMemoryGateRepository;
import com.scaler.parking_lot.respositories.InMemoryParkingLotRepository;
import com.scaler.parking_lot.respositories.InMemoryPaymentRepository;
import com.scaler.parking_lot.respositories.InMemoryTicketRepository;
import com.scaler.parking_lot.respositories.InMemoryVehicleRepository;
import com.scaler.parking_lot.services.BillService;
import com.scaler.parking_lot.services.BillServiceImpl;
import com.scaler.parking_lot.services.CapacityService;
import com.scaler.parking_lot.services.CapacityServiceImpl;
import com.scaler.parking_lot.services.PaymentService;
import com.scaler.parking_lot.services.PaymentServiceImpl;
import com.scaler.parking_lot.services.TicketService;
import com.scaler.parking_lot.services.TicketServiceImpl;
import com.scaler.parking_lot.strategies.FeeCalculationStrategy;
import com.scaler.parking_lot.strategies.HourlyFeeCalculationStrategy;
import com.scaler.parking_lot.strategies.NearestSpotAssignmentStrategy;
import com.scaler.parking_lot.strategies.SpotAssignmentStrategy;

public class Main {

    public static void main(String[] args) throws Exception {

        // Repositories
        InMemoryParkingLotRepository parkingLotRepository =
                new InMemoryParkingLotRepository();

        InMemoryTicketRepository ticketRepository =
                new InMemoryTicketRepository();

        InMemoryVehicleRepository vehicleRepository =
                new InMemoryVehicleRepository();

        InMemoryGateRepository gateRepository =
                new InMemoryGateRepository();


        // Spot assignment strategy
        SpotAssignmentStrategy spotAssignmentStrategy =
                new NearestSpotAssignmentStrategy();


        // -------------------------
        // Create Parking Spots
        // -------------------------

        ParkingSpot carSpot1 = new ParkingSpot();
        carSpot1.setSpotNumber(1);
        carSpot1.setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);
        carSpot1.setFloorNumber(1);
        carSpot1.setSupportedVehicleTypes(
                List.of(VehicleType.CAR, VehicleType.BIKE)
        );

        ParkingSpot carSpot2 = new ParkingSpot();
        carSpot2.setSpotNumber(2);
        carSpot2.setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);
        carSpot2.setFloorNumber(1);
        carSpot2.setSupportedVehicleTypes(
                List.of(VehicleType.CAR, VehicleType.BIKE)
        );

        ParkingSpot bikeSpot1 = new ParkingSpot();
        bikeSpot1.setSpotNumber(3);
        bikeSpot1.setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);
        bikeSpot1.setFloorNumber(1);
        bikeSpot1.setSupportedVehicleTypes(
                List.of(VehicleType.BIKE)
        );

        ParkingSpot truckSpot1 = new ParkingSpot();
        truckSpot1.setSpotNumber(4);
        truckSpot1.setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);
        truckSpot1.setFloorNumber(1);
        truckSpot1.setSupportedVehicleTypes(
                List.of(VehicleType.TRUCK, VehicleType.CAR, VehicleType.SUV)
        );


        // -------------------------
        // Create Parking Floor
        // -------------------------

        ParkingFloor floor = new ParkingFloor();
        floor.setId(1L);
        floor.setFloorNumber(1);
        floor.setStatus(FloorStatus.OPERATIONAL);

        floor.setSpots(
                new ArrayList<>(
                        List.of(carSpot1, carSpot2, bikeSpot1, truckSpot1)
                )
        );


         // -------------------------
        // Create Parking Floor 2
        // -------------------------

        ParkingSpot floor2CarSpot = new ParkingSpot();
        floor2CarSpot.setSpotNumber(5);
        floor2CarSpot.setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);
        floor2CarSpot.setFloorNumber(2);
        floor2CarSpot.setSupportedVehicleTypes(
                List.of(VehicleType.CAR)
        );

        ParkingSpot floor2BikeCarSpot = new ParkingSpot();
        floor2BikeCarSpot.setSpotNumber(6);
        floor2BikeCarSpot.setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);
        floor2BikeCarSpot.setFloorNumber(2);
        floor2BikeCarSpot.setSupportedVehicleTypes(
                List.of(VehicleType.CAR, VehicleType.BIKE)
        );

        ParkingSpot floor2TruckSpot = new ParkingSpot();
        floor2TruckSpot.setSpotNumber(7);
        floor2TruckSpot.setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);
        floor2TruckSpot.setFloorNumber(2);
        floor2TruckSpot.setSupportedVehicleTypes(
                List.of(VehicleType.TRUCK, VehicleType.SUV)
        );

        ParkingFloor floor2 = new ParkingFloor();
        floor2.setId(2L);
        floor2.setFloorNumber(2);
        floor2.setStatus(FloorStatus.OPERATIONAL);
        floor2.setSpots(
                new ArrayList<>(
                        List.of(floor2CarSpot, floor2BikeCarSpot, floor2TruckSpot)
                )
        );


        // -------------------------
        // Create Parking Floor 3
        // -------------------------

        ParkingSpot floor3CarBikeSpot = new ParkingSpot();
        floor3CarBikeSpot.setSpotNumber(8);
        floor3CarBikeSpot.setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);
        floor3CarBikeSpot.setFloorNumber(3);
        floor3CarBikeSpot.setSupportedVehicleTypes(
                List.of(VehicleType.CAR, VehicleType.BIKE)
        );

        ParkingSpot floor3BikeSpot = new ParkingSpot();
        floor3BikeSpot.setSpotNumber(9);
        floor3BikeSpot.setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);
        floor3BikeSpot.setFloorNumber(3);
        floor3BikeSpot.setSupportedVehicleTypes(
                List.of(VehicleType.BIKE)
        );

        ParkingSpot floor3TruckSpot = new ParkingSpot();
        floor3TruckSpot.setSpotNumber(10);
        floor3TruckSpot.setParkingSpotStatus(ParkingSpotStatus.OCCUPIED);
        floor3TruckSpot.setFloorNumber(3);
        floor3TruckSpot.setSupportedVehicleTypes(
                List.of(VehicleType.TRUCK)
        );

        ParkingFloor floor3 = new ParkingFloor();
        floor3.setFloorNumber(3);
        floor3.setId(3L);
        floor3.setStatus(FloorStatus.OPERATIONAL);
        floor3.setSpots(
                new ArrayList<>(
                        List.of(floor3CarBikeSpot, floor3BikeSpot, floor3TruckSpot)
                )
        );







        // -------------------------
        // Create Gates
        // -------------------------

        Gate entryGateA = new Gate();

        entryGateA.setGateNumber(1);
        entryGateA.setType(GateType.ENTRY);

        gateRepository.save(entryGateA);


         Gate entryGateB = new Gate();

        entryGateB.setGateNumber(2);
        entryGateB.setType(GateType.ENTRY);

        gateRepository.save(entryGateB);

       
        Gate exitGate = new Gate();

        exitGate.setGateNumber(3);
        exitGate.setType(GateType.EXIT);

        gateRepository.save(exitGate);



        // -------------------------
        // Create Parking Attendant
        // -------------------------

        ParkingAttendant attendantA = new ParkingAttendant();
        attendantA.setName("John Doe");

        entryGateA.setParkingAttendant(attendantA);


        ParkingAttendant attendantB = new ParkingAttendant();
        attendantB.setName("Jane Smith");

        entryGateB.setParkingAttendant(attendantB);


        ParkingAttendant exitAttendant = new ParkingAttendant();
        exitAttendant.setName("Mike Johnson");  

        exitGate.setParkingAttendant(exitAttendant);
        


        // -------------------------
        // Create Parking Lot
        // -------------------------

        ParkingLot parkingLot = new ParkingLot(
                new ArrayList<>(List.of(floor,floor2,floor3)),
                new ArrayList<>(List.of(entryGateA, entryGateB,exitGate)),
                Arrays.asList(VehicleType.CAR, VehicleType.BIKE,VehicleType.TRUCK, VehicleType.SUV),
                spotAssignmentStrategy,
                null
        );

        parkingLot = parkingLotRepository.save(parkingLot);


       
       // test getCapacity  api 
        CapacityService capacityService = new CapacityServiceImpl(parkingLotRepository);

        CapacityController capacityController = new CapacityController(capacityService);
        GetParkingLotCapacityRequestDto capacityRequest =new GetParkingLotCapacityRequestDto();

        capacityRequest.setParkingLotId(parkingLot.getId());

        // null means all floors
        capacityRequest.setParkingFloorIds(null);

        // null means all vehicle types
        capacityRequest.setVehicleTypes(null);

         GetParkingLotCapacityResponseDto capacityResponse =capacityController.getParkingLotCapacity(capacityRequest);
                                

        System.out.println("\n========== PARKING CAPACITY ==========");

        for (Map.Entry<ParkingFloor, Map<String, Integer>> entry: capacityResponse.getCapacityMap().entrySet()) {

                System.out.println(
                        "Floor " + entry.getKey().getFloorNumber()
                 );

                for (Map.Entry<String, Integer> vehicleEntry
                                : entry.getValue().entrySet()) {

                         System.out.println(
                                 "   " + vehicleEntry.getKey()
                                 + " -> "
                                + vehicleEntry.getValue()
                        );
                 }
         }       
                
                








        // -------------------------
        // Create Ticket Service
        // -------------------------

        TicketService ticketService =
                new TicketServiceImpl(
                        parkingLotRepository,
                        ticketRepository,
                        vehicleRepository,
                        gateRepository,
                        spotAssignmentStrategy
                );


        // -------------------------
        // Create Controller
        // -------------------------

        TicketController ticketController =
                new TicketController(ticketService);


        // -------------------------
        // Generate Ticket Request
        // -------------------------
        /*. w/o concurrency
                GenerateTicketRequestDto requestDto =
                        new GenerateTicketRequestDto();

                requestDto.setGateId((int) entryGateA.getId());
                requestDto.setRegistrationNumber("HP01A1234");
                requestDto.setVehicleType("truck");
                requestDto.setParkingAttendant(attendantA);
         */
        





      

        // -------------------------
        // Generate Ticket
        // -------------------------

        // GenerateTicketResponseDto responseDto =
        //         ticketController.generateTicket(requestDto);


        // -------------------------
        // Generate Tickets Concurrently
        // -------------------------

        System.out.println("\n========== Vehicle enters through GateA ==========");
        System.out.println("\n========== Vehicle enters through GateB ==========");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        GenerateTicketRequestDto requestA =
                new GenerateTicketRequestDto();

        requestA.setGateId((int) entryGateA.getId());
        requestA.setVehicleType("CAR");
        requestA.setRegistrationNumber("CAR-FROM-GATE-A");
        requestA.setParkingAttendant(attendantA);


        GenerateTicketRequestDto requestB =
                new GenerateTicketRequestDto();

        requestB.setGateId((int) entryGateB.getId());
        requestB.setVehicleType("CAR");
        requestB.setRegistrationNumber("CAR-FROM-GATE-B");
        requestB.setParkingAttendant(attendantB);


        // Submit both requests concurrently
        Future<GenerateTicketResponseDto> futureA =
                executor.submit(() ->
                        ticketController.generateTicket(requestA)
                );

        Future<GenerateTicketResponseDto> futureB =
                executor.submit(() ->
                        ticketController.generateTicket(requestB)
                );


        // Get the results
        GenerateTicketResponseDto responseA = futureA.get();
        GenerateTicketResponseDto responseB = futureB.get();


        // Get tickets
        Ticket ticketA = responseA.getTicket();
        Ticket ticketB = responseB.getTicket();

        System.out.println("\n========== Tickets Generated ==========");

        System.out.println(
                "Vehicle " + requestA.getVehicleType()
                + " from Gate A got spot: "
                + ticketA.getParkingSpot().getSpotNumber()
                + " And Generated Ticket Number: "
                + ticketA.getTicketNumber()
        );

        System.out.println(
                "Vehicle " + requestB.getVehicleType()
                + " from Gate B got spot: "
                + ticketB.getParkingSpot().getSpotNumber()
                + " And Generated Ticket Number: "
                + ticketB.getTicketNumber()
        );

        
       

       

      


/*
      System.out.println("Response Status: "
                + responseDto.getResponseStatus());

        Ticket ticket = responseDto.getTicket();

        if (ticket != null) {
            System.out.println("Ticket generated successfully");
            System.out.println("Vehicle: "
                    + ticket.getVehicle().getRegistrationNumber());
            System.out.println("Vehicle Type: "
                    + ticket.getVehicle().getVehicleType());
            System.out.println("Spot Number: "
                    + ticket.getParkingSpot().getSpotNumber());
            System.out.println("Floor Number: "
                    + ticket.getParkingSpot()
                            .getFloorNumber());
        }
 */
   
     // ================= GENERATE BILL =================

       FeeCalculationStrategy feeCalculationStrategy = new HourlyFeeCalculationStrategy();
       InMemoryBillRepository billRepository = new InMemoryBillRepository();

        BillService billService = new BillServiceImpl(
                ticketRepository,
                billRepository,
                gateRepository,
                feeCalculationStrategy
        );

        BillController billController =
                new BillController(billService);

        GenerateBillRequestDto billRequest =
                new GenerateBillRequestDto();

        billRequest.setTicketId(ticketA.getId());
        billRequest.setExitTime(new Date(System.currentTimeMillis() + 3 * 60 * 60 * 1000)); // 3 hours later
        billRequest.setGateId(exitGate.getId());
        billRequest.setParkingAttendant(
                exitGate.getParkingAttendant()
        );

        GenerateBillResponseDto billResponse =
                billController.generateBill(billRequest);

        System.out.println("\n========== BILL GENERATED ==========");

        if (billResponse.getResponseStatus() == ResponseStatus.SUCCESS) {

                System.out.println("Bill ID      : " + billResponse.getBillId());
                System.out.println("Entry Time   : " + billResponse.getEntryTime());
                System.out.println("Exit Time    : " + billResponse.getExitTime());
                System.out.println("Amount       : ₹" + billResponse.getAmount());
                System.out.println("Bill Status  : " + billResponse.getBillStatus());

        } else {

                System.out.println("Bill generation failed");
        }

      // ==================== MAKE PAYMENT ====================

      InMemoryPaymentRepository paymentRepository = new InMemoryPaymentRepository();

        PaymentService paymentService = new PaymentServiceImpl(
                billRepository,
                paymentRepository
        );

        PaymentController paymentController =
                new PaymentController(paymentService);

        MakePaymentRequestDto paymentRequest = new MakePaymentRequestDto();

        paymentRequest.setBillId(billResponse.getBillId());
        paymentRequest.setAmount(billResponse.getAmount());
        paymentRequest.setPaymentMode("ONLINE");
        paymentRequest.setPaymentGatewayType("RAZORPAY");

        MakePaymentResponseDto paymentResponse =
                paymentController.makePayment(paymentRequest);

        System.out.println("\n========== PAYMENT ==========");

        if (paymentResponse.getResponseStatus() == ResponseStatus.SUCCESS) {

                System.out.println("Payment Status   : "
                        + paymentResponse.getPaymentStatus());

                System.out.println("Reference Number : "
                        + paymentResponse.getReferenceNumber());

                

        } else {
        System.out.println("Payment failed");
        }


        Bill updatedBill =
                billRepository.findById(billResponse.getBillId()).get();

        System.out.println("\n========== BILL AFTER PAYMENT ==========");

        System.out.println("Bill ID     : " + updatedBill.getId());
        System.out.println("Bill paid Amount : ₹" + updatedBill.getPaidAmount());
        System.out.println("Bill Status : " + updatedBill.getBillStatus());


        

       
    }
}