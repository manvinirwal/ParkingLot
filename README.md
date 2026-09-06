# Parking Lot — Low Level Design

A simple in-memory Parking Lot system implemented in Java using object-oriented design principles, design patterns, strategy-based extensibility, and concurrency-safe parking spot allocation.

## 1. Requirements

The parking lot system supports:

* Multiple parking floors
* Multiple parking spots on each floor
* Different vehicle types
* Parking spots supporting one or more vehicle types
* Entry and exit gates
* Parking attendants
* Ticket generation when a vehicle enters
* Automatic parking spot assignment
* Parking capacity checking
* Bill generation when a vehicle exits
* Hourly fee calculation
* Cash and online payments
* Multiple online payment gateways
* Concurrent ticket generation from multiple entry gates
* In-memory repositories

---

## 2. Supported Vehicle Types

```text
CAR
BIKE
TRUCK
SUV
```

A parking spot can support multiple vehicle types.

For example:

```text
Spot 4 → CAR, TRUCK, SUV
```

This means the same physical spot can be used by any of these vehicle types, but it can only hold **one vehicle at a time**.

### Why a Spot Supports a List of Vehicle Types, Not Just One

Vehicle sizes roughly order as BIKE < CAR < TRUCK/SUV. A spot physically large enough for a bigger vehicle has room to also hold a smaller one — a spot built for a TRUCK can just as easily fit a CAR or a BIKE, but a BIKE-sized spot can never fit a CAR. This is the reasoning behind `supportedVehicleTypes` being a list rather than one fixed type: exact-type matching still happens, it's just checked against a spot's configured list of supported types, not a single label.

This also matters for the business, not just correctness — if a TRUCK spot could only ever hold a truck, and most large spots sit empty most of the day, that space goes to waste. Letting a larger, otherwise-idle spot serve a smaller vehicle uses the lot more efficiently. Each spot's actual list is still configured explicitly (see the example lot in Section 11) rather than automatically derived from a fixed size hierarchy, so a lot operator retains full control over which types a given physical spot is allowed to serve.

---

## 3. High-Level Flow

### Vehicle Entry

```text
Vehicle
   |
   v
Entry Gate
   |
   v
TicketController
   |
   v
TicketService
   |
   +---- ParkingLotRepository
   |
   +---- VehicleRepository
   |
   +---- SpotAssignmentStrategy
   |
   v
ParkingSpot
   |
   v
TicketRepository
   |
   v
Ticket
```

### Vehicle Exit

```text
Ticket
   |
   v
BillController
   |
   v
BillService
   |
   +---- TicketRepository
   +---- GateRepository
   +---- FeeCalculationStrategy
   |
   v
Bill
   |
   v
PaymentController
   |
   v
PaymentService
   |
   +---- Cash Payment
   |
   +---- PaymentGateway
            |
            +---- Paytm
            |
            +---- Razorpay
```

---

# 4. Main Domain Models

## ParkingLot

Represents the complete parking lot.

Contains:

* Parking floors
* Gates
* Supported vehicle types
* Spot assignment strategy
* Fee calculation strategy

```text
ParkingLot
 ├── ParkingFloor
 ├── Gate
 ├── SpotAssignmentStrategy
 └── FeeCalculationStrategy
```

---

## ParkingFloor

Represents a floor inside the parking lot.

Contains:

* Floor number
* Floor status
* Parking spots

A floor can be operational or non-operational.

Only operational floors are considered while assigning parking spots.

---

## ParkingSpot

Represents a physical parking spot.

Contains:

* Spot number
* Supported vehicle types
* Current status
* Parking floor

```text
ParkingSpotStatus

AVAILABLE
OCCUPIED
```

A spot can support multiple vehicle types.

Example:

```text
Spot 1 → CAR, BIKE
Spot 4 → CAR, TRUCK, SUV
```

---

## Vehicle

Represents a vehicle entering the parking lot.

Contains:

* Registration number
* Vehicle type
* Owner name

---

## Gate

Represents an entry or exit gate.

```text
GateType

ENTRY
EXIT
```

Each gate is associated with a parking attendant.

---

## Ticket

A ticket is generated when a vehicle enters the parking lot.

Contains:

* Vehicle
* Ticket number
* Entry time
* Parking spot
* Entry gate
* Parking attendant

Example:

```text
Ticket
 ├── Vehicle
 ├── ParkingSpot
 ├── EntryTime
 ├── Gate
 └── ParkingAttendant
```

---

## Bill

A bill is generated when a vehicle exits.

Contains:

* Ticket
* Entry time
* Exit time
* Amount
* Exit gate
* Parking attendant
* Bill status
* Payments

Bill statuses:

```text
UNPAID
PARTIALLY_PAID
PAID
```

A bill can have multiple payments.

For example:

```text
Bill = ₹200

Payment 1 = ₹100 CASH
Payment 2 = ₹100 ONLINE

Total = ₹200
Bill Status = PAID
```

The `Bill` itself is responsible for calculating its payment status.

---

## Payment

Represents a payment made against a bill.

Contains:

* Amount
* Payment mode
* Payment status
* Bill
* Payment time
* Reference number

Payment modes:

```text
CASH
ONLINE
```

---

# 5. Services

## TicketService

Responsible for generating parking tickets.

Responsibilities:

1. Find the parking lot using the entry gate.
2. Validate the gate.
3. Validate vehicle type.
4. Find/create the vehicle.
5. Find an available parking spot.
6. Mark the spot as occupied.
7. Generate a ticket.
8. Save the ticket.

---

## CapacityService

Returns available parking capacity.

Capacity can be requested for:

* Entire parking lot
* Specific floors
* Specific vehicle types

Example response:

```text
Floor 1
    CAR   -> 3
    BIKE  -> 3
    SUV   -> 1
    TRUCK -> 1

Floor 2
    CAR   -> 2
    BIKE  -> 1
    SUV   -> 1
    TRUCK -> 1
```

### Important

Because one spot can support multiple vehicle types, capacity counts can overlap.

For example:

```text
Spot 1 → CAR, BIKE
```

This contributes:

```text
CAR  -> 1
BIKE -> 1
```

But physically it is still only **one parking spot**.

---

## BillService

Responsible for generating a bill.

Responsibilities:

1. Find the ticket.
2. Validate the exit gate.
3. Calculate the parking fee.
4. Create the bill.
5. Set the initial status to `UNPAID`.
6. Save the bill.

---

## PaymentService

Responsible for processing payments.

Responsibilities:

1. Find the bill.
2. Validate payment mode.
3. Validate the payment gateway for online payments.
4. Process the payment.
5. Save the payment.
6. Add the payment to the bill.

The bill then updates its own status.

```text
PaymentService
      |
      v
bill.addPayment(payment)
      |
      v
Bill.updateBillStatus()
```

This keeps the bill's business rule inside the `Bill` model.

---

# 6. Design Patterns

## Strategy Pattern — Spot Assignment

Different algorithms can be used to select a parking spot.

```text
SpotAssignmentStrategy
          |
          +---- NearestSpotAssignmentStrategy
```

The service does not need to know how the spot is selected.

It simply calls:

```java
spotAssignmentStrategy.assignSpot(lot, vehicleType);
```

### Current Strategy

`NearestSpotAssignmentStrategy`

The current implementation selects:

1. An operational floor
2. The floor with the minimum number of available compatible spots
3. The lowest numbered compatible spot on that floor

This keeps the assignment algorithm separate from ticket generation.

---

## Strategy Pattern — Fee Calculation

Fee calculation is also separated using a strategy.

```text
FeeCalculationStrategy
          |
          +---- HourlyFeeCalculationStrategy
```

This allows another fee calculation strategy to be added without changing `BillService`.

---

## Adapter Pattern — Payment Gateway

Different payment providers expose different APIs.

The system provides a common interface:

```java
PaymentGateway
```

Concrete implementations:

```text
PaymentGateway
      |
      +---- PaytmPaymentProcessor
      |
      +---- RazorpayPaymentProcessor
```

`PaymentService` works with the common interface instead of directly depending on a specific payment provider.

---

## Factory Pattern — Payment Gateway Selection

A factory selects the appropriate gateway.

```text
PaymentGatewayFactory
        |
        +---- PAYTM
        |
        +---- RAZORPAY
```

For example:

```java
PaymentGateway gateway =
        PaymentGatewayFactory.getPaymentGateway("RAZORPAY");
```

This prevents `PaymentService` from containing gateway-specific object creation logic.

---

# 7. Repository Layer

The application uses in-memory repositories.

Examples:

```text
InMemoryParkingLotRepository
InMemoryParkingSpotRepository
InMemoryVehicleRepository
InMemoryGateRepository
InMemoryTicketRepository
InMemoryBillRepository
InMemoryPaymentRepository
```

Repositories are responsible for storing and retrieving domain objects.

The system does not use a real database.

---

# 8. Thread Safety

Ticket generation can happen concurrently because multiple entry gates may receive vehicles at the same time.

For example:

```text
Gate A ───── Thread A
                |
                v
             Spot 8


Gate B ───── Thread B
                |
                v
             Spot 5
```

The critical shared resource is the **physical parking spot**.

### The Critical Section

The danger sits specifically in the gap between reading a spot's status and writing a new one:

```java
if (spot.getParkingSpotStatus() == ParkingSpotStatus.AVAILABLE) {
    // <-- another thread could read AVAILABLE here too
    spot.setParkingSpotStatus(ParkingSpotStatus.OCCUPIED);
}
```

That gap is the **critical section** — the piece of code that must never be entered by two threads at the same time for the same spot. Locking the entire ticket-generation method would also be correct, but it would force every gate to wait for the whole operation (vehicle lookup, ticket building, everything), even the parts that never touch spot data. Keeping the lock scoped to just this section lets unrelated work from different gates run in parallel, and only serializes the part that actually needs it.

## Fine-Grained Locking

The system uses the specific `ParkingSpot` object as the lock:

```java
synchronized (spot) {
    if (spot.getParkingSpotStatus()
            == ParkingSpotStatus.AVAILABLE) {

        spot.setParkingSpotStatus(
                ParkingSpotStatus.OCCUPIED);

        break;
    }
}
```

### Why not lock by vehicle type?

A parking spot can support multiple vehicle types.

For example:

```text
Spot 4 → CAR, TRUCK, SUV
```

If we lock by vehicle type:

```text
CAR   → Lock A
TRUCK → Lock B
```

then a CAR thread and a TRUCK thread could simultaneously select the same physical spot.

Therefore, the lock must be associated with the **physical spot**, not the vehicle type.

---

## Retry After Collision

Two threads can initially select the same spot:

```text
Thread A → Spot 8
Thread B → Spot 8
```

The first thread obtains the lock:

```text
Thread A
   |
   v
lock Spot 8
   |
   v
AVAILABLE
   |
   v
OCCUPIED
```

The second thread then obtains the lock and sees:

```text
Spot 8 → OCCUPIED
```

It retries the assignment:

```text
Thread B
   |
   v
Spot 8 → OCCUPIED
   |
   v
retry
   |
   v
Spot 5
```

Therefore, both threads eventually receive different physical spots.

---

# 9. Thread-Safe In-Memory Repository

Since multiple ticket-generation threads can access repositories simultaneously, the base repository uses:

```java
ConcurrentHashMap
```

for storage and:

```java
AtomicLong
```

for ID generation.

Conceptually:

```text
ConcurrentHashMap
        |
        v
Thread-safe storage


AtomicLong
        |
        v
Thread-safe ID generation
```

Example:

```java
private final Map<Long, T> items =
        new ConcurrentHashMap<>();

private final AtomicLong nextId =
        new AtomicLong(1);
```

IDs are generated using:

```java
nextId.getAndIncrement();
```

This prevents two concurrent threads from receiving the same repository ID.

---

# 10. Exception Handling

The system uses domain-specific exceptions rather than generic errors.

Examples:

```text
InvalidGateException
InvalidParkingLotException
ParkingSpotNotAvailableException
GenerateBillRequestValidationException
InvalidBillException
PaymentFailedException
```

Controllers convert service exceptions into response statuses.

Example:

```text
Service
   |
   +---- Success → SUCCESS
   |
   +---- Exception → FAILURE
```

---

# 11. Example Parking Lot

The current example contains three floors.

### Floor 1

```text
Spot 1 → CAR, BIKE
Spot 2 → CAR, BIKE
Spot 3 → BIKE
Spot 4 → TRUCK, CAR, SUV
```

### Floor 2

```text
Spot 5 → CAR
Spot 6 → CAR, BIKE
Spot 7 → TRUCK, SUV
```

### Floor 3

```text
Spot 8 → CAR, BIKE
Spot 9 → BIKE
Spot 10 → TRUCK
```

Spot 10 can initially be occupied to demonstrate capacity calculation.

---

# 12. Example Concurrent Entry

Two vehicles enter simultaneously through different gates:

```text
Gate A → CAR
Gate B → CAR
```

Both threads may initially select:

```text
Spot 8
```

The locking mechanism ensures only one thread can claim it.

Example:

```text
Thread 1 → Spot 8 → OCCUPIED
Thread 2 → Spot 8 → OCCUPIED → retry
Thread 2 → Spot 5 → OCCUPIED
```

Final result:

```text
Gate A → Spot 8
Gate B → Spot 5
```

The reverse is also possible:

```text
Gate A → Spot 5
Gate B → Spot 8
```

because thread scheduling is nondeterministic.

The system does not require a particular gate to receive a particular spot.

---

# 13. Example Bill and Payment Flow

Suppose:

```text
Entry Time = 16:24
Exit Time  = 19:24
```

Parking duration:

```text
3 hours
```

Using the hourly fee strategy:

```text
Amount = ₹60
```

Bill is initially:

```text
Bill ID     : 1
Amount      : ₹60
Bill Status : UNPAID
```

Then an online payment is made through Razorpay:

```text
Payment Mode   : ONLINE
Gateway        : RAZORPAY
Amount         : ₹60
Status         : SUCCESS
```

The payment is added to the bill:

```java
bill.addPayment(payment);
```

The bill recalculates its status:

```text
Total successful payments = ₹60
Bill amount               = ₹60

Bill Status = PAID
```

---

# 14. Important Design Decisions

### Why does `Bill` update its own status?

Because the bill owns the rule:

```text
Total successful payments >= Bill amount
                    ↓
                  PAID
```

Therefore, payment processing does not need to directly manipulate `BillStatus`.

---

### Why does one Bill have multiple Payments?

To support partial payments.

Example:

```text
Bill = ₹500

Payment 1 = ₹200
Payment 2 = ₹300

Total = ₹500
Status = PAID
```

---

### Why is payment separate from bill generation?

Generating a bill and paying a bill are two different operations.

```text
Generate Bill
     ↓
UNPAID
     ↓
Make Payment
     ↓
PAID
```

This also allows the system to support multiple payments against the same bill.

---

### Why does one Gate class handle both entry and exit, instead of separate EntryGate/ExitGate classes?

An EntryGate and an ExitGate would share almost every field — id, name, attendant, status — with exactly one real difference: which kind each is. A single `Gate` class with a `GateType` (`ENTRY`/`EXIT`) captures that one difference without duplicating everything else.

---

### Why do Ticket and Bill each store their own ParkingAttendant, instead of just reading it from Gate?

`Gate.parkingAttendant` reflects whoever is *currently* stationed there — attendants change shifts. If a `Ticket` only referenced its `Gate` and looked up the attendant from there, the record of who actually issued that specific ticket would be lost the moment the shift changes. Storing the attendant directly on `Ticket` and `Bill` captures who was actually present at that exact moment — a snapshot in time, not a live pointer.

---

### Why is there one generic `InMemoryRepository<T>` instead of a separate repository class per model?

`InMemoryParkingLotRepository`, `InMemoryTicketRepository`, and the rest would otherwise repeat identical `save()`/`findById()`/`findAll()` logic for every single model. A single `InMemoryRepository<T extends BaseModel>` implements that logic once; each concrete repository becomes a one-line subclass:

```java
public class GateRepository extends InMemoryRepository<Gate> {}
```

The `T extends BaseModel` bound is what makes this legal — it guarantees every stored type has an `id` (and whatever else `BaseModel` declares) for the repository to call, without needing to know the concrete type.

---

### Why do controllers use DTOs instead of passing domain models directly?

Two reasons. First, a domain model can change shape for reasons that have nothing to do with a given caller — a new required field on `Ticket` can break every caller holding the raw model, even ones that never needed that field. A DTO exposing only the fields a specific use case actually needs absorbs that change; callers depending on the DTO see nothing different. Second, a raw model exposes everything it holds, whether the caller should see it or not — returning a raw `Ticket` to, say, an external billing integration would also hand over the full `ParkingAttendant` object and anything else `Ticket` happens to carry. A response DTO is a deliberate, field-by-field decision about what actually needs to leave the system boundary.

---

# 15. SOLID Principles Used

## Single Responsibility

Different components have focused responsibilities:

```text
TicketService       → ticket generation
CapacityService     → capacity
BillService         → bill generation
PaymentService      → payment
Repository          → persistence
Strategy             → algorithms
Factory              → object creation
```

## Open/Closed Principle

New strategies can be added without modifying the services.

For example:

```text
SpotAssignmentStrategy
    ├── NearestSpotAssignmentStrategy
    └── AnotherSpotAssignmentStrategy
```

Similarly:

```text
FeeCalculationStrategy
    ├── HourlyFeeCalculationStrategy
    └── WeekendFeeCalculationStrategy
```

## Dependency Inversion

Services depend on abstractions such as:

```text
SpotAssignmentStrategy
FeeCalculationStrategy
PaymentGateway
```

rather than directly depending on concrete implementations.

---

# 16. Project Structure

A simplified project structure:

```text
src/main/java/com/scaler/parking_lot

├── controllers
│   ├── TicketController
│   ├── CapacityController
│   ├── BillController
│   └── PaymentController
│
├── services
│   ├── TicketService
│   ├── TicketServiceImpl
│   ├── CapacityService
│   ├── CapacityServiceImpl
│   ├── BillService
│   ├── BillServiceImpl
│   ├── PaymentService
│   └── PaymentServiceImpl
│
├── models
│   ├── ParkingLot
│   ├── ParkingFloor
│   ├── ParkingSpot
│   ├── Vehicle
│   ├── Gate
│   ├── Ticket
│   ├── Bill
│   ├── Payment
│   └── ParkingAttendant
│
├── repositories
│   ├── InMemoryRepository
│   ├── InMemoryParkingLotRepository
│   ├── InMemoryVehicleRepository
│   ├── InMemoryGateRepository
│   ├── InMemoryTicketRepository
│   ├── InMemoryBillRepository
│   └── InMemoryPaymentRepository
│
├── strategies
│   ├── SpotAssignmentStrategy
│   ├── NearestSpotAssignmentStrategy
│   ├── FeeCalculationStrategy
│   └── HourlyFeeCalculationStrategy
│
├── adapters
│   ├── PaymentGateway
│   ├── PaytmPaymentProcessor
│   └── RazorpayPaymentProcessor
│
├── factories
│   └── PaymentGatewayFactory
│
├── dto
│   ├── GenerateTicketRequestDto
│   ├── GenerateTicketResponseDto
│   ├── GetParkingLotCapacityRequestDto
│   ├── GetParkingLotCapacityResponseDto
│   ├── GenerateBillRequestDto
│   ├── GenerateBillResponseDto
│   ├── MakePaymentRequestDto
│   └── MakePaymentResponseDto
│
└── exceptions
    ├── InvalidGateException
    ├── InvalidParkingLotException
    ├── ParkingSpotNotAvailableException
    ├── InvalidBillException
    └── PaymentFailedException
```

---

# 17. Design Philosophy

The implementation intentionally avoids unnecessary abstractions.

The main principles are:

* Keep domain models simple.
* Keep business logic in services/models where appropriate.
* Use Strategy where an algorithm can vary.
* Use Adapter for external payment gateways.
* Use Factory for selecting payment gateways.
* Use repositories for in-memory persistence.
* Protect the actual shared resource (`ParkingSpot`) during concurrent allocation.
* Use `ConcurrentHashMap` and `AtomicLong` for thread-safe in-memory storage.
* Avoid introducing additional classes unless a requirement actually needs them.

The goal is to satisfy the parking lot requirements while keeping the design easy to understand, extend, and explain in an LLD interview.
