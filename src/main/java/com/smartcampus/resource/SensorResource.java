package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    // GET /api/v1/sensors - Get all sensors (with optional type filter)
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(store.getSensors().values());

        // Filter by type if query parameter is provided
        if (type != null && !type.trim().isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor sensor : sensorList) {
                if (sensor.getType().equalsIgnoreCase(type)) {
                    filtered.add(sensor);
                }
            }
            return Response.ok(filtered).build();
        }

        return Response.ok(sensorList).build();
    }

    // POST /api/v1/sensors - Register a new sensor
    @POST
    public Response createSensor(Sensor sensor) {
        // Validate sensor ID
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Sensor ID is required\"}")
                    .build();
        }

        // Check duplicate sensor ID
        if (store.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Sensor with this ID already exists\"}")
                    .build();
        }

        // Validate that the roomId exists
        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID is required\"}")
                    .build();
        }

        if (!store.getRooms().containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Room with ID '" + sensor.getRoomId() + 
                "' does not exist. Please create the room first."
            );
        }

        // Set default status if not provided
        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        // Save sensor
        store.getSensors().put(sensor.getId(), sensor);

        // Add sensor ID to the room's sensorIds list
        store.getRooms().get(sensor.getRoomId())
             .getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    // GET /api/v1/sensors/{sensorId} - Get a specific sensor
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensors().get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found with ID: " + sensorId + "\"}")
                    .build();
        }

        return Response.ok(sensor).build();
    }

    // DELETE /api/v1/sensors/{sensorId} - Delete a sensor
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensors().get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found with ID: " + sensorId + "\"}")
                    .build();
        }

        // Remove sensor ID from the room's sensorIds list
        String roomId = sensor.getRoomId();
        if (roomId != null && store.getRooms().containsKey(roomId)) {
            store.getRooms().get(roomId).getSensorIds().remove(sensorId);
        }

        store.getSensors().remove(sensorId);

        return Response.ok()
                .entity("{\"message\":\"Sensor " + sensorId + " deleted successfully\"}")
                .build();
    }
    // Sub-resource locator for readings (Part 4)
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(
            @PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }

    
}