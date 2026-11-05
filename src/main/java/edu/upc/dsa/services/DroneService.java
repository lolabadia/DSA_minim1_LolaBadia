package edu.upc.dsa.services;

import edu.upc.dsa.domain.models.*;
import edu.upc.dsa.domain.repository.DroneManager;
import edu.upc.dsa.domain.repository.DroneManagerImpl;
import edu.upc.dsa.exceptions.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.log4j.Logger;


import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Api(value = "/drones", description = "Endpoint to Drone Service")

@Path("/drones")

public class DroneService {
    final static Logger logger = Logger.getLogger(DroneService.class);

    private DroneManager dm;

    public DroneService() {
        this.dm = DroneManagerImpl.getInstance();
        if (dm.numDrones() == 0) {
            this.dm.addDrone("D1", "Falcon", "DJI", "Mavic 3");
            this.dm.addDrone("D2", "Eagle", "Parrot", "Anafi");
            this.dm.addPilot("P1", "Anna", "Puig");
            this.dm.addPilot("P2", "Marc", "Soler");
            try {
                List<Position> pos = new ArrayList<>();
                pos.add(new Position(41.27, 1.98));
                pos.add(new Position(41.28, 1.99));
                this.dm.addFlightPlan("D1", "P1", "2026-11-05T10:00", 3, pos);
            } catch (Exception e) {
                logger.error("DroneService: error en la precàrrega");
            }
        }
    }

    @POST
    @ApiOperation(value = "afegir un dron", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = DroneInfo.class),
            @ApiResponse(code = 400, message = "Missing fields")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDrone(DroneInfo d) {
        if (d.getId() == null || d.getName() == null) return Response.status(400).build();
        Drone res = this.dm.addDrone(d.getId(), d.getName(), d.getManufacturer(), d.getModel());
        return Response.status(201).entity(new DroneInfo(res)).build();
    }

    @POST
    @ApiOperation(value = "afegir un pilot", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = PilotInfo.class),
            @ApiResponse(code = 400, message = "Missing fields")
    })
    @Path("/pilots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPilot(PilotInfo p) {
        if (p.getId() == null || p.getName() == null) return Response.status(400).build();
        Pilot res = this.dm.addPilot(p.getId(), p.getName(), p.getSurname());
        return Response.status(201).entity(new PilotInfo(res)).build();
    }


    @GET
    @ApiOperation(value = "drons ordenats per hores de vol (desc)", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = DroneInfo.class, responseContainer = "List")
    })
    @Path("/ranking")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDronesByFlightHours() {
        List<DroneInfo> res = new ArrayList<>();
        for (Drone d : this.dm.getDronesByFlightHours()) res.add(new DroneInfo(d));
        GenericEntity<List<DroneInfo>> entity = new GenericEntity<List<DroneInfo>>(res) {};
        return Response.status(200).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "pilots ordenats per hores de vol (desc)", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = PilotInfo.class, responseContainer = "List")
    })
    @Path("/pilots/ranking")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPilotsByFlightHours() {
        List<PilotInfo> res = new ArrayList<>();
        for (Pilot p : this.dm.getPilotsByFlightHours()) res.add(new PilotInfo(p));
        GenericEntity<List<PilotInfo>> entity = new GenericEntity<List<PilotInfo>>(res) {};
        return Response.status(200).entity(entity).build();
    }

    @PUT
    @ApiOperation(value = "guardar un dron al magatzem", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = DroneInfo.class),
            @ApiResponse(code = 404, message = "Drone not found"),
            @ApiResponse(code = 409, message = "Drone already in storage")
    })

    @Path("/storage/{droneId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response storeDrone(@PathParam("droneId") String droneId) {
        try {
            Drone d = this.dm.storeDrone(droneId);
            return Response.status(200).entity(new DroneInfo(d)).build();
        } catch (DroneNotFoundException e) {
            return Response.status(404).build();
        } catch (DroneInMaintenanceException e) {
            return Response.status(409).build();
        }
    }

    @PUT
    @ApiOperation(value = "reparar el dron més proper a la porta", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = DroneInfo.class),
            @ApiResponse(code = 404, message = "Storage is empty")
    })
    @Path("/storage/repair")
    @Produces(MediaType.APPLICATION_JSON)
    public Response repairDrone() {
        try {
            Drone d = this.dm.repairDrone();
            return Response.status(200).entity(new DroneInfo(d)).build();
        } catch (EmptyStorageException e) {
            return Response.status(404).build();
        }
    }

    @POST
    @ApiOperation(value = "crear un pla de vol", notes = "date format: 2026-11-05T10:00")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = FlightPlan.class),
            @ApiResponse(code = 400, message = "Missing fields or bad date"),
            @ApiResponse(code = 404, message = "Drone or pilot not found"),
            @ApiResponse(code = 409, message = "Drone in maintenance or schedule conflict")
    })

    @Path("/flightplans")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFlightPlan(FlightPlan fp) {
        if (fp.getDroneId() == null || fp.getPilotId() == null || fp.getDate() == null)
            return Response.status(400).build();
        try {
            FlightPlan res = this.dm.addFlightPlan(fp.getDroneId(), fp.getPilotId(), fp.getDate(), fp.getHours(), fp.getPositions());
            return Response.status(201).entity(res).build();
        } catch (DroneNotFoundException | PilotNotFoundException e) {
            return Response.status(404).build();
        } catch (DroneInMaintenanceException | FlightPlanConflictException e) {
            return Response.status(409).build();
        } catch (RuntimeException e) {
            logger.error("addFlightPlan: data incorrecta " + fp.getDate());
            return Response.status(400).build();
        }
    }

    @GET
    @ApiOperation(value = "plans de vol d'un pilot", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = FlightPlan.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Pilot not found")
    })
    @Path("/pilots/{pilotId}/flightplans")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlightPlansByPilot(@PathParam("pilotId") String pilotId) {
        try {
            List<FlightPlan> res = this.dm.getFlightPlansByPilot(pilotId);
            GenericEntity<List<FlightPlan>> entity = new GenericEntity<List<FlightPlan>>(res) {};
            return Response.status(200).entity(entity).build();
        } catch (PilotNotFoundException e) {
            return Response.status(404).build();
        }
    }

    @GET
    @ApiOperation(value = "plans de vol d'un dron", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = FlightPlan.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Drone not found")
    })


    @Path("/{droneId}/flightplans")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlightPlansByDrone(@PathParam("droneId") String droneId) {
        try {
            List<FlightPlan> res = this.dm.getFlightPlansByDrone(droneId);
            GenericEntity<List<FlightPlan>> entity = new GenericEntity<List<FlightPlan>>(res) {};
            return Response.status(200).entity(entity).build();
        } catch (DroneNotFoundException e) {
            return Response.status(404).build();
        }
    }
}