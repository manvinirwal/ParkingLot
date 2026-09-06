package com.scaler.parking_lot.services;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import com.scaler.parking_lot.exceptions.InvalidGateException;
import com.scaler.parking_lot.exceptions.InvalidParkingLotException;
import com.scaler.parking_lot.exceptions.ParkingSpotNotAvailableException;

import com.scaler.parking_lot.models.Gate;
import com.scaler.parking_lot.models.ParkingAttendant;
import com.scaler.parking_lot.models.ParkingLot;
import com.scaler.parking_lot.models.ParkingSpot;

import com.scaler.parking_lot.models.Ticket;
import com.scaler.parking_lot.models.Vehicle;
import com.scaler.parking_lot.models.enums.GateType;
import com.scaler.parking_lot.models.enums.ParkingSpotStatus;
import com.scaler.parking_lot.models.enums.VehicleType;
import com.scaler.parking_lot.respositories.InMemoryGateRepository;
import com.scaler.parking_lot.respositories.InMemoryParkingLotRepository;
import com.scaler.parking_lot.respositories.InMemoryTicketRepository;
import com.scaler.parking_lot.respositories.InMemoryVehicleRepository;
import com.scaler.parking_lot.strategies.SpotAssignmentStrategy;

public class TicketServiceImpl implements TicketService{
    private final InMemoryParkingLotRepository parkingLotRepository;
    private final InMemoryTicketRepository ticketRepository;
    private final InMemoryVehicleRepository vehicleRepository;
    private final InMemoryGateRepository gateRepository;
    private final SpotAssignmentStrategy spotAssignmentStrategy;

    //private final Object spotLock = new Object();
    // // This lock is used to synchronize access to the spot assignment process, ensuring that only one thread can assign a spot at a time.
    //but we will use fine-grained locking based on the specific ParkingSpot object, so this global lock is not used in the current implementation. I have kept it here for reference and understanding of the locking mechanism.

    /*
    private final Object spotLock = new Object(); is used as a global dedicated lock, instead of just writing synchronized on the whole method. 
    Why bother with a separate object just to lock on? Because only the spot-finding section should be locked, not the whole method; 
    synchronized(spotLock) { ... } marks exactly which lines are protected.
    spotLock is one single object, shared across the whole TicketService. 
    If Gate A and Gate B (throuh diff threads)both call issueTicket() on the SAME TicketService instance, does it matter that they're different Gate objects, for this lock to actually work? 
    No, the lock doesn't care which gate is calling. As long as both requests go through the same TicketService instance, they'll both hit the same spotLock, 
    and only one can be inside the critical section at a time, no matter which gate they came from. 
    That's actually the whole point. This lock protects the shared spot data, regardless of how many different gates are trying to reach it.
     */


    

    public TicketServiceImpl(InMemoryParkingLotRepository parkingLotRepository, InMemoryTicketRepository ticketRepository,
            InMemoryVehicleRepository vehicleRepository, InMemoryGateRepository gateRepository, SpotAssignmentStrategy spotAssignmentStrategy) {
        this.parkingLotRepository = parkingLotRepository;
        this.ticketRepository = ticketRepository;
        this.vehicleRepository = vehicleRepository;
        this.gateRepository = gateRepository;
        this.spotAssignmentStrategy = spotAssignmentStrategy;
    }





    @Override
    public Ticket generateTicket(int gateId, String registrationNumber, String vehicleType,ParkingAttendant parkingAttendant)
            throws InvalidGateException, InvalidParkingLotException, ParkingSpotNotAvailableException {
        
                ParkingLot lot = parkingLotRepository.getParkingLotByGateId(gateId)
                                        .orElseThrow( () -> new InvalidParkingLotException("parkingLot not found"));

                Gate gate = gateRepository.findById(gateId).get();

                if(gate.getType().equals(GateType.EXIT)){
                    throw new InvalidGateException("this gate is of EXIT type");
                }

                VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());

                Optional<Vehicle> v = vehicleRepository.getVehicleByRegistrationNumber(registrationNumber);
                Vehicle vehicle;
                if(v.isEmpty()){
                    vehicle = new Vehicle();
                    vehicle.setRegistrationNumber(registrationNumber);
                    vehicle.setVehicleType(type);
    
                    vehicle = vehicleRepository.save(vehicle);

                }
                else  vehicle = v.get();

             
               ParkingSpot spot;


               /*
               this is coarse-grained locking, which has global lock .
               With the current global lock:

                    Gate A → Spot 1 ─┐
                                    │ global lock
                    Gate B → Spot 2 ─┘

               Even though they're trying to use different spots, Gate B has to wait for Gate A.
               This can lead to performance bottlenecks if multiple threads are trying to generate tickets simultaneously.
               
               
               synchronized(spotLock) {
                        Optional<ParkingSpot> s = spotAssignmentStrategy.assignSpot(lot, type);


                        if(s.isEmpty()){
                            throw new ParkingSpotNotAvailableException("No parking spot available at the moment");
                            
                        }

                        spot = s.get();
                        spot.setParkingSpotStatus(ParkingSpotStatus.OCCUPIED);
                }

                With per-spot locking:

                    Gate A → Spot 1 → synchronized(Spot 1)
                    Gate B → Spot 2 → synchronized(Spot 2)

                    They don't block each other.
                */


                /*

                AI Suggestes lock per vehicle type, which is a more fine-grained locking approach.
                With this approach, only threads trying to generate tickets for the same vehicle type will block each other, while threads for different vehicle types can proceed concurrently.
                For example:    

                    Gate A → Spot 1 (CAR) ─┐
                                            │ lock for CAR
                    Gate B → Spot 2 (CAR) ─┘

                    Gate C → Spot 3 (BIKE) ─┐
                                            │ lock for BIKE
                    Gate D → Spot 4 (BIKE) ─┘

                    but in our case ,one spot suppoorts multiple vehicle types, so we cannot use this approach.
                    a TRUCK spot's supportedVehicleTypes list includes TRUCK, CAR, and BIKE. 
                    Imagine Gate A is issuing a CAR ticket, and Gate B, at the exact same moment, is issuing a TRUCK ticket. 
                    With this new fix, do these two gates use the same lock, or different locks? 
                    Different locks, Gate A locks on VehicleType.CAR, Gate B locks on VehicleType.TRUCK. 
                    They're not blocked from running at the same time.Could both gates legally end up looking at, and choosing, the exact same physical TRUCK-sized spot, since that one spot supports both CAR and TRUCK? Yes. Since they hold different locks, nothing stops them from running the "find spot" step at the same time, and both finding that same TRUCK spot as EMPTY, since neither one is blocking the other.
                    This is exactly the bug. AI's fix is faster, but it silently reintroduces the original race condition, between two DIFFERENT gates, using DIFFERENT vehicle types, specifically for any spot that supports more than one vehicle type
                    

                    below code snippet is from AI's suggestion, which is not correct for our case, so we will not use it.

                    private final ConcurrentHashMap<VehicleType, Object> locksByVehicleType =new ConcurrentHashMap<>();

                   

                    private Object getLockFor(VehicleType type) {
                        return locksByVehicleType.computeIfAbsent(type, t -> new Object());
                    }

                    public Ticket issueTicket(IssueTicketRequestDTO request) {
                        // ... gate check, vehicle build, same as before ...

                        ParkingSpot assignedSpot;
                        synchronized (getLockFor(request.getVehicleType())) {
                            assignedSpot = allotmentStrategy.findSpot(
                                    parkingSpotRepository.findAll(), request.getVehicleType());

                            if (assignedSpot == null) {
                                throw new ParkingFullException("No available spot for this vehicle type");
                            }

                            assignedSpot.setParkingSpotStatus(ParkingSpotStatus.OCCUPIED);
                            parkingSpotRepository.save(assignedSpot);
                        }

                        // ... build and save ticket, same as before ...
                    }

                 */
                
               

                
                /*
                In our case,we will use fine-grained locking based on the specific ParkingSpot object.  
                with this approach, only threads trying to generate tickets for the same specific parking spot will block each other, while threads for different parking spots can proceed concurrently.
                For example:    

                    Gate A → Spot 1 ─┐
                                    │ lock for Spot 1
                    Gate B → Spot 1 ─┘

                    Gate C → Spot 2 ─┐
                                    │ lock for Spot 2
                    Gate D → Spot 2 ─┘


                 */
                



                while(true){
                    Optional<ParkingSpot> s = spotAssignmentStrategy.assignSpot(lot, type);

                     System.out.println(
                            Thread.currentThread().getName()
                            + " selected: "
                            + (s.isPresent() ? s.get().getSpotNumber() : "NONE")
                            + " for " + type
                    );

                    if(s.isEmpty()){
                        throw new ParkingSpotNotAvailableException("No parking spot available at the moment");
                        
                    }

                    spot = s.get();
                    // Now we have a specific spot, we can lock on it to ensure that no other thread can occupy it at the same time.
                    synchronized (spot) {
                        // re-check if the spot is still available, because it might have been taken by another thread while we were waiting for the lock.
                        if(spot.getParkingSpotStatus() == ParkingSpotStatus.AVAILABLE){
                            spot.setParkingSpotStatus(ParkingSpotStatus.OCCUPIED);
                            break;
                        }
                    }
                    // If we reach here, it means the spot was taken by another thread, so we loop again to find a new available spot.
                }


                Ticket ticket = new Ticket();
                ticket.setEntryTime(new Date());
                ticket.setGenratedAt(gate);
                ticket.setTicketNumber(UUID.randomUUID().toString());
                ticket.setGeneratedBy(parkingAttendant);
                ticket.setVehicle(vehicle);
                ticket.setParkingSpot(spot);

                ticket = ticketRepository.save(ticket);

                return ticket;

    }
    
}
