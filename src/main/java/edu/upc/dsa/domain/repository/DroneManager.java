package edu.upc.dsa.domain.repository;

import edu.upc.dsa.domain.models.Drone;
import edu.upc.dsa.domain.models.FlightPlan;
import edu.upc.dsa.domain.models.Pilot;
import edu.upc.dsa.domain.models.Position;
import edu.upc.dsa.exceptions.*;

import java.util.List;

public interface DroneManager {
    //operacions
    public Drone addDrone(String id, String name, String manufacturer, String model);
    public Pilot addPilot(String id, String name, String surname);

    public List<Drone> getDronesByFlightHours();
    public List<Pilot> getPilotsByFlightHours();

    public Drone storeDrone(String droneId) throws DroneNotFoundException, DroneInMaintenanceException;
    public Drone repairDrone() throws EmptyStorageException;

    public FlightPlan addFlightPlan(String droneId, String pilotId, String date, double hours, List<Position> positions)
            throws DroneNotFoundException, PilotNotFoundException, DroneInMaintenanceException, FlightPlanConflictException;
    public List<FlightPlan> getFlightPlansByPilot(String pilotId) throws PilotNotFoundException;
    public List<FlightPlan> getFlightPlansByDrone(String droneId) throws DroneNotFoundException;

    public Drone getDrone(String id);
    public Pilot getPilot(String id);


    public int numDrones();
    public int numPilots();
    public int numDronesInStorage();

    public void clear();
}