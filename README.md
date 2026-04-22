# SmartCampus API - 5COSC022W Coursework

## Overview
A RESTful API built with JAX-RS (Jersey) for managing Rooms and Sensors 
across a Smart Campus. The API supports full CRUD operations, nested 
sub-resources for sensor readings, and robust error handling.

**Base URL:** `http://localhost:8080/SmartCampus/api/v1`

## Technology Stack
- Java 17
- JAX-RS with Jersey 2.41
- Apache Tomcat 9.0
- Maven
- Jackson (JSON)
- In-memory storage (ConcurrentHashMap)

## How to Build and Run

### Prerequisites
- JDK 17
- Apache Maven
- Apache Tomcat 9.0
- Apache NetBeans 24 (or any IDE)

### Steps
1. Clone the repository:
   git clone https://github.com/YOURUSERNAME/SmartCampusAPI.git

2. Open in NetBeans:
   File → Open Project → Select the cloned folder

3. Build the project:
   Right-click project → Clean and Build

4. Run the project:
   Right-click project → Run
   (Make sure Apache Tomcat 9.0 is set as the server)

5. API is available at:
   http://localhost:8080/SmartCampus/api/v1

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1 | Discovery - API metadata |
| GET | /api/v1/rooms | Get all rooms |
| POST | /api/v1/rooms | Create a new room |
| GET | /api/v1/rooms/{id} | Get a specific room |
| DELETE | /api/v1/rooms/{id} | Delete a room |
| GET | /api/v1/sensors | Get all sensors |
| POST | /api/v1/sensors | Register a new sensor |
| GET | /api/v1/sensors/{id}/readings | Get readings for a sensor |
| POST | /api/v1/sensors/{id}/readings | Add a reading for a sensor |

## Sample curl Commands

### 1. Discovery Endpoint
curl -X GET http://localhost:8080/SmartCampus/api/v1

### 2. Create a Room
curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":50}"

### 3. Get All Rooms
curl -X GET http://localhost:8080/SmartCampus/api/v1/rooms

### 4. Create a Sensor
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":22.5,\"roomId\":\"LIB-301\"}"

### 5. Get All Sensors by Type
curl -X GET "http://localhost:8080/SmartCampus/api/v1/sensors?type=Temperature"

## Report - Question Answers

### Part 1 - Q1: JAX-RS Resource Lifecycle
By default, JAX-RS creates a new instance of a resource class for every 
incoming HTTP request (per-request scope). This means instance variables 
reset on each call and cannot store shared data. To maintain state across 
requests, a singleton DataStore class using ConcurrentHashMap is used, 
ensuring thread safety when multiple requests arrive simultaneously.

### Part 1 - Q2: HATEOAS
HATEOAS (Hypermedia as the Engine of Application State) embeds navigation 
links directly in API responses so clients can discover available actions 
dynamically rather than relying on hardcoded URLs or external documentation. 
This reduces tight coupling between client and server, allows the API to 
evolve its URL structure without breaking clients, and makes the API 
self-describing which reduces developer onboarding time.

## Part 2 - Room Management

### Files Added
| File | Package | Purpose |
|------|---------|---------|
| `RoomResource.java` | `com.smartcampus.resource` | Handles all /rooms endpoints |
| `RoomNotEmptyException.java` | `com.smartcampus.exception` | Custom exception for non-empty rooms |
| `RoomNotEmptyExceptionMapper.java` | `com.smartcampus.exception` | Maps exception to 409 response |

### Endpoints Implemented
| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| GET | /api/v1/rooms | Get all rooms | 200 OK |
| POST | /api/v1/rooms | Create a new room | 201 Created |
| GET | /api/v1/rooms/{roomId} | Get specific room | 200 OK |
| DELETE | /api/v1/rooms/{roomId} | Delete room | 200 OK |

### Business Logic
- A room **cannot be deleted** if it has sensors assigned
- Attempting to delete returns **409 Conflict**
- Duplicate room ID returns **409 Conflict**
- Non-existent room returns **404 Not Found**

### Question Answers

#### Q: Returning IDs vs full room objects?
Returning only IDs uses less bandwidth but forces the client to make 
additional requests for each room detail, increasing API calls. Returning 
full objects gives everything in one request but increases payload size. 
For large datasets a summary object with key fields is the best approach.

#### Q: Is DELETE idempotent?
The first DELETE returns 200 OK. Subsequent DELETE calls for the same 
room return 404 Not Found since the room no longer exists. The server 
state remains the same after repeated calls which satisfies the practical 
definition of idempotency.

## Part 3 - Sensor Operations and Filtering

### Files Added

| File | Package | Purpose |
|------|---------|---------|
| `SensorResource.java` | `com.smartcampus.resource` | Handles all /sensors endpoints |
| `LinkedResourceNotFoundException.java` | `com.smartcampus.exception` | Exception when roomId does not exist |
| `LinkedResourceNotFoundExceptionMapper.java` | `com.smartcampus.exception` | Maps exception to 422 response |

### Endpoints Implemented

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| GET | /api/v1/sensors | Get all sensors | 200 OK |
| GET | /api/v1/sensors?type=X | Filter sensors by type | 200 OK |
| POST | /api/v1/sensors | Register a new sensor | 201 Created |
| GET | /api/v1/sensors/{sensorId} | Get specific sensor | 200 OK |
| DELETE | /api/v1/sensors/{sensorId} | Delete a sensor | 200 OK |

### Business Logic
- Sensor cannot be created if roomId does not exist in the system
- Creating a sensor automatically adds its ID to the room sensorIds list
- Deleting a sensor automatically removes its ID from the room sensorIds list
- Type filtering is done via optional query parameter ?type=X

### Question Answers

#### Q1: What happens if client sends wrong Content-Type?
If a client sends data as text/plain or application/xml instead of
application/json, JAX-RS checks the @Consumes(MediaType.APPLICATION_JSON)
annotation and immediately rejects the request with a 415 Unsupported
Media Type response before the method even executes. The request never
reaches the resource method logic, protecting the API from malformed
or unexpected input formats.

#### Q2: @QueryParam vs Path Parameter for Filtering
Query parameters like ?type=CO2 are superior for filtering because they
are optional by nature, the base endpoint /sensors still works without
them. Path parameters like /sensors/type/CO2 make the filter mandatory
and part of the resource identity, implying it represents a different
resource rather than a filtered view of the same collection. Query
parameters also support multiple filters easily such as
?type=CO2&status=ACTIVE without changing the URL structure, making
them far more flexible and RESTful for search and filter operations.

## Part 4 - Sub-Resources and Sensor Readings

### Files Added

| File | Package | Purpose |
|------|---------|---------|
| `SensorReadingResource.java` | `com.smartcampus.resource` | Handles all readings endpoints |
| `SensorUnavailableException.java` | `com.smartcampus.exception` | Exception for unavailable sensors |
| `SensorUnavailableExceptionMapper.java` | `com.smartcampus.exception` | Maps to 403 Forbidden |

### Endpoints Implemented

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| GET | /api/v1/sensors/{sensorId}/readings | Get all readings | 200 OK |
| POST | /api/v1/sensors/{sensorId}/readings | Add new reading | 201 Created |
| GET | /api/v1/sensors/{sensorId}/readings/{readingId} | Get specific reading | 200 OK |

### Business Logic
- Cannot add reading to a MAINTENANCE sensor returns 403
- Cannot add reading to an OFFLINE sensor returns 403
- Adding a reading automatically updates sensor currentValue
- Reading ID is auto generated using UUID if not provided
- Timestamp is auto generated if not provided

### Question Answers

#### Q1: Benefits of Sub-Resource Locator Pattern
The Sub-Resource Locator pattern delegates handling of nested paths to
separate dedicated classes. This improves separation of concerns since
each class manages only its own resource logic. It reduces complexity
in large APIs by avoiding a single massive controller class with hundreds
of methods. It also improves maintainability since changes to reading
logic only affect SensorReadingResource and do not impact SensorResource.
Each class can be tested independently making the codebase more modular
and easier to scale as new nested resources are added.

#### Q2: Side Effect of POST Reading
A successful POST to readings triggers an update to the currentValue
field on the parent Sensor object. This ensures data consistency across
the API so that when a client fetches the sensor details they always
see the most recent measurement recorded by the hardware without needing
to query the readings list separately.
