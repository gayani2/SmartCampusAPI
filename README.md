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
