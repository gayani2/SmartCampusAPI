# SmartCampus API — 5COSC022W Coursework
**University of Westminster | Client-Server Architectures**

---

## Student Information
| Field | Detail |
|-------|--------|
| **Module** | 5COSC022W - Client-Server Architectures |
| **Title** | Smart Campus Sensor & Room Management API |
| **Technology** | JAX-RS (Jersey 2.41) + Apache Tomcat 9.0 |
| **Base URL** | `http://localhost:8080/api/v1` |

---

## Table of Contents
1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [How to Build and Run](#how-to-build-and-run)
5. [API Endpoints](#api-endpoints)
6. [Sample curl Commands](#sample-curl-commands)
7. [Error Responses](#error-responses)
8. [Business Logic Rules](#business-logic-rules)
9. [Part 1 — Setup & Discovery](#part-1--setup--discovery)
10. [Part 2 — Room Management](#part-2--room-management)
11. [Part 3 — Sensor Operations](#part-3--sensor-operations)
12. [Part 4 — Sub-Resources](#part-4--sub-resources)
13. [Part 5 — Error Handling & Logging](#part-5--error-handling--logging)
14. [Report — Question Answers](#report--question-answers)

---

## Overview
A fully RESTful API built with JAX-RS (Jersey) for managing Rooms and
Sensors across a Smart Campus initiative at the University of Westminster.
The system provides a seamless interface for campus facilities managers
and automated building systems to interact with campus data.

The API supports:
- Full CRUD operations for Rooms and Sensors
- Nested sub-resources for Sensor Reading history
- Dependency validation and business logic constraints
- Comprehensive custom error handling with no stack trace exposure
- Request and response logging via JAX-RS filters
- In-memory storage using thread-safe ConcurrentHashMap

---

## Technology Stack

| Technology | Purpose | Version |
|------------|---------|---------|
| Java | Programming Language | 17 |
| JAX-RS (Jersey) | REST API Framework | 2.41 |
| Apache Tomcat | Web Server | 9.0 |
| Maven | Build & Dependency Tool | 3.x |
| Jackson | JSON Serialisation | Bundled with Jersey |
| ConcurrentHashMap | Thread-Safe In-Memory Storage | Built-in Java |

---

## Project Structure

```
SmartCampus/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/smartcampus/
        │       ├── application/
        │       │   └── SmartCampusApplication.java
        │       ├── exception/
        │       │   ├── GlobalExceptionMapper.java
        │       │   ├── LinkedResourceNotFoundException.java
        │       │   ├── LinkedResourceNotFoundExceptionMapper.java
        │       │   ├── RoomNotEmptyException.java
        │       │   ├── RoomNotEmptyExceptionMapper.java
        │       │   ├── SensorUnavailableException.java
        │       │   └── SensorUnavailableExceptionMapper.java
        │       ├── filter/
        │       │   └── ApiLoggingFilter.java
        │       ├── model/
        │       │   ├── Room.java
        │       │   ├── Sensor.java
        │       │   └── SensorReading.java
        │       ├── resource/
        │       │   ├── DiscoveryResource.java
        │       │   ├── RoomResource.java
        │       │   ├── SensorReadingResource.java
        │       │   └── SensorResource.java
        │       └── storage/
        │           └── DataStore.java
        └── webapp/
            └── WEB-INF/
                └── web.xml
```

---

## How to Build and Run

### Prerequisites
- JDK 17
- Apache NetBeans 24
- Apache Tomcat 9.0
- Maven (bundled with NetBeans)
- Postman (for testing)

### Step 1 — Clone the Repository
```bash
git clone https://github.com/gayani2/SmartCampusAPI.git
```

### Step 2 — Open in NetBeans
```
File → Open Project → Select SmartCampus folder → Open Project
```

### Step 3 — Build
```
Right-click SmartCampus → Clean and Build
Wait for: BUILD SUCCESS in output panel
```

### Step 4 — Run
```
Right-click SmartCampus → Run
Wait for: Deployment done in output panel
Server starts on: http://localhost:8080
```

### Step 5 — Verify
Open browser and visit:
```
http://localhost:8080/SmartCampus/api/v1
```
You should see the discovery JSON response confirming the server is running.

---

## API Endpoints

### Discovery
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/v1` | API metadata and resource links | 200 OK |

### Room Management
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/v1/rooms` | Get all rooms | 200 OK |
| POST | `/api/v1/rooms` | Create a new room | 201 Created |
| GET | `/api/v1/rooms/{roomId}` | Get a specific room | 200 OK |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room | 200 OK |

### Sensor Operations
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/v1/sensors` | Get all sensors | 200 OK |
| GET | `/api/v1/sensors?type=X` | Filter sensors by type | 200 OK |
| POST | `/api/v1/sensors` | Register a new sensor | 201 Created |
| GET | `/api/v1/sensors/{sensorId}` | Get a specific sensor | 200 OK |
| DELETE | `/api/v1/sensors/{sensorId}` | Delete a sensor | 200 OK |

### Sensor Readings
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/v1/sensors/{sensorId}/readings` | Get all readings | 200 OK |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add a new reading | 201 Created |
| GET | `/api/v1/sensors/{sensorId}/readings/{readingId}` | Get specific reading | 200 OK |

---

## Sample curl Commands

### 1. Discovery Endpoint
```bash
curl -X GET http://localhost:8080/api/v1
```

### 2. Create a Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":50}"
```

### 3. Get All Rooms
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

### 4. Get Specific Room
```bash
curl -X GET http://localhost:8080/api/v1/rooms/LIB-301
```

### 5. Delete a Room
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

### 6. Create a Sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":22.5,\"roomId\":\"LIB-301\"}"
```

### 7. Get All Sensors
```bash
curl -X GET http://localhost:8080/api/v1/sensors
```

### 8. Filter Sensors by Type
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"
```

### 9. Add a Sensor Reading
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":24.5}"
```

### 10. Get All Readings for a Sensor
```bash
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings
```

---

## Error Responses

| Status Code | Error | When It Happens |
|-------------|-------|-----------------|
| 400 | Bad Request | Missing required fields in request body |
| 403 | Forbidden | Reading posted to MAINTENANCE or OFFLINE sensor |
| 404 | Not Found | Resource does not exist |
| 409 | Conflict | Deleting room with sensors or duplicate ID |
| 415 | Unsupported Media Type | Wrong Content-Type header sent |
| 422 | Unprocessable Entity | Sensor references a non-existent roomId |
| 500 | Internal Server Error | Unexpected server error — no stack trace exposed |

---

## Business Logic Rules

- A room **cannot be deleted** if it has sensors assigned → 409 Conflict
- A sensor **cannot be created** if the roomId does not exist → 422 Unprocessable Entity
- A reading **cannot be added** to a MAINTENANCE or OFFLINE sensor → 403 Forbidden
- Creating a sensor **automatically links** its ID to the room sensorIds list
- Deleting a sensor **automatically removes** its ID from the room sensorIds list
- Adding a reading **automatically updates** the parent sensor currentValue field
- Reading ID is **auto-generated** using UUID if not provided
- Reading timestamp is **auto-generated** using epoch milliseconds if not provided

---

## Part 1 — Setup & Discovery

### Files
| File | Package | Purpose |
|------|---------|---------|
| `SmartCampusApplication.java` | `com.smartcampus.application` | Registers API at /api/v1 |
| `DataStore.java` | `com.smartcampus.storage` | Singleton in-memory data store |
| `DiscoveryResource.java` | `com.smartcampus.resource` | GET /api/v1 discovery endpoint |
| `Room.java` | `com.smartcampus.model` | Room POJO model |
| `Sensor.java` | `com.smartcampus.model` | Sensor POJO model |
| `SensorReading.java` | `com.smartcampus.model` | SensorReading POJO model |

### Endpoints
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/v1` | Returns API metadata, version, contact, resource links | 200 OK |

---

## Part 2 — Room Management

### Files
| File | Package | Purpose |
|------|---------|---------|
| `RoomResource.java` | `com.smartcampus.resource` | All /rooms endpoints |
| `RoomNotEmptyException.java` | `com.smartcampus.exception` | Thrown when deleting room with sensors |
| `RoomNotEmptyExceptionMapper.java` | `com.smartcampus.exception` | Maps exception to 409 Conflict |

### Endpoints
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/v1/rooms` | Returns all rooms | 200 OK |
| POST | `/api/v1/rooms` | Creates a new room | 201 Created |
| GET | `/api/v1/rooms/{roomId}` | Returns specific room | 200 OK |
| DELETE | `/api/v1/rooms/{roomId}` | Deletes room if no sensors | 200 OK |

### Business Logic
- Room cannot be deleted if it has sensors → 409 Conflict
- Duplicate room ID → 409 Conflict
- Room not found → 404 Not Found
- POST returns Location header with new room URL

---

## Part 3 — Sensor Operations

### Files
| File | Package | Purpose |
|------|---------|---------|
| `SensorResource.java` | `com.smartcampus.resource` | All /sensors endpoints |
| `LinkedResourceNotFoundException.java` | `com.smartcampus.exception` | Thrown when roomId does not exist |
| `LinkedResourceNotFoundExceptionMapper.java` | `com.smartcampus.exception` | Maps to 422 Unprocessable Entity |

### Endpoints
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/v1/sensors` | Returns all sensors | 200 OK |
| GET | `/api/v1/sensors?type=X` | Filters sensors by type | 200 OK |
| POST | `/api/v1/sensors` | Registers new sensor | 201 Created |
| GET | `/api/v1/sensors/{sensorId}` | Returns specific sensor | 200 OK |
| DELETE | `/api/v1/sensors/{sensorId}` | Deletes sensor | 200 OK |

### Business Logic
- Sensor cannot be created if roomId does not exist → 422
- Creating sensor adds its ID to room sensorIds automatically
- Deleting sensor removes its ID from room sensorIds automatically
- Type filtering is case-insensitive via @QueryParam

---

## Part 4 — Sub-Resources

### Files
| File | Package | Purpose |
|------|---------|---------|
| `SensorReadingResource.java` | `com.smartcampus.resource` | All /readings endpoints |
| `SensorUnavailableException.java` | `com.smartcampus.exception` | Thrown for unavailable sensors |
| `SensorUnavailableExceptionMapper.java` | `com.smartcampus.exception` | Maps to 403 Forbidden |

### Endpoints
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/v1/sensors/{sensorId}/readings` | Get all readings for sensor | 200 OK |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add new reading | 201 Created |
| GET | `/api/v1/sensors/{sensorId}/readings/{readingId}` | Get specific reading | 200 OK |

### Business Logic
- Cannot post reading to MAINTENANCE sensor → 403 Forbidden
- Cannot post reading to OFFLINE sensor → 403 Forbidden
- Posting reading automatically updates parent sensor currentValue
- Reading UUID auto-generated if not provided
- Timestamp auto-generated if not provided

---

## Part 5 — Error Handling & Logging

### Files
| File | Package | Purpose |
|------|---------|---------|
| `GlobalExceptionMapper.java` | `com.smartcampus.exception` | Catches all Throwable → 500 |
| `ApiLoggingFilter.java` | `com.smartcampus.filter` | Logs all requests and responses |

### Exception Mapper Summary
| Exception Class | HTTP Status | Scenario |
|-----------------|-------------|---------|
| `RoomNotEmptyException` | 409 Conflict | Delete room with sensors |
| `LinkedResourceNotFoundException` | 422 Unprocessable Entity | Sensor with invalid roomId |
| `SensorUnavailableException` | 403 Forbidden | Reading on MAINTENANCE sensor |
| `Throwable` (Global) | 500 Internal Server Error | Any unexpected runtime error |

### Logging Output Example
```
INFO: Incoming Request: POST http://localhost:8080/api/v1/rooms
INFO: Outgoing Response: Status 201 for POST http://localhost:8080/api/v1/rooms
INFO: Incoming Request: GET http://localhost:8080/api/v1/sensors
INFO: Outgoing Response: Status 200 for GET http://localhost:8080/api/v1/sensors
```

---

## Report — Question Answers

---

### Part 1 — Q1: JAX-RS Resource Lifecycle

By default, JAX-RS instantiates a brand new instance of each resource
class for every single incoming HTTP request. This is known as per-request
scope. The consequence of this lifecycle is that any instance variables
declared inside a resource class are initialised fresh with each request
and completely discarded once the response is sent. This means resource
classes cannot be used to store or maintain shared application data across
multiple requests.

To manage in-memory data structures safely across requests, a Singleton
design pattern is applied through the DataStore class. The DataStore holds
a single static instance that is created once when the application starts
and shared across all resource class instances. Since multiple HTTP
requests can arrive simultaneously from different clients, multiple threads
will access this shared DataStore concurrently. To prevent race conditions
and data corruption, ConcurrentHashMap is used instead of a standard
HashMap. ConcurrentHashMap uses internal locking mechanisms that allow
multiple threads to read and write safely at the same time without
corrupting data or causing data loss.

---

### Part 1 — Q2: HATEOAS

HATEOAS stands for Hypermedia as the Engine of Application State and is
considered one of the most advanced principles of RESTful design. The core
idea is that API responses should not just return data but should also
include hyperlinks that guide the client toward related actions and
resources they can navigate to next.

Without HATEOAS, a client developer must rely entirely on static external
documentation to know what URLs exist and what actions are available. If
the API changes its URL structure the client breaks. With HATEOAS the
client receives links directly in the response — for example a room
response would include a link to its sensors and a link to delete itself.
The client can follow these links dynamically without needing hardcoded
URLs. This reduces coupling between the client and server, allows the API
to evolve its structure without breaking existing clients, and makes the
API self-describing. It significantly reduces onboarding time for
developers consuming the API for the first time since the API itself
reveals what is possible at each step.

---

### Part 2 — Q1: Returning IDs vs Full Room Objects

When designing a GET endpoint that returns a collection of rooms there are
two approaches. The first is to return only the IDs of each room. The
second is to return the full room objects including all fields.

Returning only IDs minimises network bandwidth because the payload is very
small regardless of how many rooms exist. However it forces the client to
make a separate GET request for each individual room to retrieve its
details, which significantly increases the total number of API round trips
and can slow down the client application considerably.

Returning full room objects gives the client everything in a single
request, eliminating the need for follow-up calls. However for large
datasets with hundreds or thousands of rooms this produces a very large
payload that consumes significant bandwidth and takes longer to parse on
the client side.

The best practice is a middle ground approach where the collection
endpoint returns a summary object containing only the most essential
fields such as id, name, and capacity, while reserving the complete
detail including sensorIds for the individual GET by ID endpoint. This
balances bandwidth efficiency with client usability.

---

### Part 2 — Q2: Is DELETE Idempotent

In this implementation the DELETE operation satisfies the practical
definition of idempotency. The first time DELETE is called for a room
that exists, the room is removed from the data store and a 200 OK response
is returned. If the exact same DELETE request is sent a second time the
room no longer exists so the service returns a 404 Not Found response.

While the HTTP response code differs between the first and subsequent
calls, the server state remains identical after each call. The room does
not exist after the first call and it still does not exist after the tenth
call. The outcome of the operation is the same regardless of repetition
which satisfies the REST definition of idempotency. This is the standard
and accepted behaviour for DELETE in RESTful APIs. The server is never
put into a different or inconsistent state by repeated DELETE requests
for the same resource.

---

### Part 3 — Q1: @Consumes and Wrong Content-Type

The @Consumes(MediaType.APPLICATION_JSON) annotation declares that a
resource method only accepts requests where the Content-Type header is
set to application/json. When a client sends a request with a different
Content-Type such as text/plain or application/xml, JAX-RS intercepts the
request before it even reaches the resource method and immediately returns
an HTTP 415 Unsupported Media Type response.

This behaviour is handled entirely by the JAX-RS runtime without any code
inside the method needing to check the format. The method is never invoked.
This is important because it protects the API from receiving data in
formats it cannot deserialise into Java objects. If text/plain was accepted
and passed to a method expecting a Room Java object, Jackson would throw a
parsing exception. The @Consumes annotation acts as an automatic gatekeeper
that enforces input format contracts at the framework level before any
business logic is executed.

---

### Part 3 — Q2: @QueryParam vs Path Parameter for Filtering

Using a query parameter such as GET /sensors?type=CO2 is considered
superior to embedding the filter in the URL path such as
GET /sensors/type/CO2 for several important reasons.

Query parameters are optional by nature. The base endpoint GET /sensors
continues to work without any filter and returns all sensors. A path-based
filter makes the filter mandatory and part of the resource identity,
implying that /sensors/type/CO2 is a fundamentally different resource
from /sensors rather than a filtered view of the same collection.

Query parameters also scale naturally to support multiple simultaneous
filters. For example GET /sensors?type=CO2&status=ACTIVE is clean and
readable without changing the URL structure. Achieving the same with path
parameters would require awkward nested paths like
/sensors/type/CO2/status/ACTIVE which is confusing and difficult to extend.

From a REST design perspective, path parameters should identify a specific
resource while query parameters should modify or refine how a collection
is presented. Filtering is a refinement operation and therefore belongs
in the query string.

---

### Part 4 — Q1: Sub-Resource Locator Pattern Benefits

The Sub-Resource Locator pattern involves a resource method that instead
of returning a Response object returns an instance of another resource
class. Jersey continues routing the remaining URL path into that returned
class. This allows large APIs to be split into smaller focused classes
each responsible for one level of the resource hierarchy.

Without this pattern all endpoints including deeply nested ones like
/sensors/{id}/readings/{rid} would need to be defined inside a single
SensorResource class. As the API grows this class becomes a massive
unmanageable file with hundreds of methods making it difficult to read,
maintain, and test.

With the Sub-Resource Locator pattern SensorResource only handles /sensors
level concerns and delegates anything under /sensors/{id}/readings to
SensorReadingResource. Each class has a single clear responsibility.
Changes to reading logic only affect SensorReadingResource and have no
impact on SensorResource. Each class can also be unit tested independently.
This follows the Single Responsibility Principle and makes the codebase
significantly easier to scale as new nested resources are added in the
future.

---

### Part 5 — Q1: Why 422 is More Accurate Than 404

HTTP 404 Not Found means the requested URL endpoint does not exist on the
server. It signals that the path the client navigated to cannot be found.
HTTP 422 Unprocessable Entity means the server found the endpoint and
successfully parsed the request body but the semantic content of that body
contains invalid or unresolvable data.

When a client posts a new sensor with a roomId that does not exist, the
endpoint POST /api/v1/sensors exists perfectly fine and Jersey successfully
deserialises the JSON body into a Sensor object. The problem is not the
URL and not the format. The problem is that the value of the roomId field
inside the valid JSON payload is referencing a room that does not exist in
the system. This is a semantic data validation failure not a missing
resource error.

Returning 404 would mislead the client into thinking the /sensors endpoint
does not exist. Returning 422 accurately communicates that the endpoint
was found, the data was understood, but a specific field value failed
business validation. This precision helps client developers immediately
understand the nature of the error and correct the right thing.

---

### Part 5 — Q2: Security Risks of Exposing Stack Traces

Exposing raw Java stack traces to external API consumers presents serious
cybersecurity risks. A stack trace contains a detailed map of the internal
structure of the application including full package names and class names
which reveal the architecture and technology choices of the system. Line
numbers pinpoint exactly where in the source code an error occurred making
it trivial for an attacker to reason about the code structure.

Stack traces also frequently expose the names and versions of third party
libraries being used. An attacker can cross-reference these versions
against public vulnerability databases such as CVE to find known exploits
that have not been patched. File system paths sometimes appear in stack
traces revealing the directory structure of the server. Database connection
details and query fragments can also appear in certain exceptions.

The GlobalExceptionMapper addresses this by intercepting all unhandled
Throwable errors and returning only a generic safe message to the client.
The full exception details are logged securely on the server side only
where they are accessible to developers but invisible to external
consumers. This follows the principle of least privilege by giving clients
only the information they need and nothing more.

---

### Part 5 — Q3: Why Use Filters for Logging

JAX-RS filters are the correct tool for cross-cutting concerns like logging
because they enforce separation of concerns and eliminate code duplication.
A cross-cutting concern is a behaviour that applies uniformly across the
entire application regardless of which specific resource or method is
being called.

If logging was implemented by manually inserting Logger.info() statements
inside every individual resource method, the same boilerplate code would
be repeated across dozens of methods. This violates the DRY principle
which stands for Do Not Repeat Yourself. If the logging format needs to
change in the future every single method must be found and updated
individually which is error-prone and time-consuming.

With a ContainerRequestFilter and ContainerResponseFilter the logging
logic is written exactly once in ApiLoggingFilter and automatically applies
to every single request and response in the entire API without touching
any resource class. Resource classes remain clean and focused purely on
their business logic. The filter runs transparently in the background.
This also makes it easy to add other cross-cutting behaviours in the
future such as authentication, CORS headers, or rate limiting simply by
adding new filter classes without modifying existing resource code.

---

