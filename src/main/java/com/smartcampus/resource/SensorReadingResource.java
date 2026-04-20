package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings
    @GET
    public Response getAllReadings() {
        // Check sensor exists
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found with ID: " 
                            + sensorId + "\"}")
                    .build();
        }

        // Get readings for this sensor
        List<SensorReading> readings = store.getReadings()
                .getOrDefault(sensorId, new ArrayList<>());

        return Response.ok(readings).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings
    @POST
    public Response addReading(SensorReading reading) {
        // Check sensor exists
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found with ID: " 
                            + sensorId + "\"}")
                    .build();
        }

        // Check sensor is not in MAINTENANCE status
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is currently under MAINTENANCE " +
                "and cannot accept new readings."
            );
        }

        // Check sensor is not OFFLINE
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is OFFLINE " +
                "and cannot accept new readings."
            );
        }

        // Generate unique ID if not provided
        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        // Set timestamp if not provided
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Save reading to the list
        store.getReadings()
             .computeIfAbsent(sensorId, k -> new ArrayList<>())
             .add(reading);

        // Update the parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }

    // GET /api/v1/sensors/{sensorId}/readings/{readingId}
    @GET
    @Path("/{readingId}")
    public Response getReadingById(@PathParam("readingId") String readingId) {
        // Check sensor exists
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found with ID: "
                            + sensorId + "\"}")
                    .build();
        }

        // Get readings list
        List<SensorReading> readings = store.getReadings()
                .getOrDefault(sensorId, new ArrayList<>());

        // Find reading by ID
        for (SensorReading reading : readings) {
            if (reading.getId() != null && 
                reading.getId().trim().equals(readingId.trim())) {
                return Response.ok(reading).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Reading not found with ID: "
                        + readingId + "\"}")
                .build();
    }
}