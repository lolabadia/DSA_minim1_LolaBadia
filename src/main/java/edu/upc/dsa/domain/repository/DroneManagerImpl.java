package edu.upc.dsa.domain.repository;

import edu.upc.dsa.domain.models.Drone;
import edu.upc.dsa.domain.models.FlightPlan;
import edu.upc.dsa.domain.models.Pilot;
import edu.upc.dsa.domain.models.Position;
import edu.upc.dsa.exceptions.*;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;

public class DroneManagerImpl implements DroneManager {

    private static DroneManager instance;  //Singleton
    final static Logger logger = Logger.getLogger(DroneManagerImpl.class);

    //Estructures de dades escullides al esquema
    private Map<String, Drone> drones;
    private Map<String, Pilot> pilots;
    private Stack<Drone> storage;

    private DroneManagerImpl() {
        this.drones = new HashMap<>();
        this.pilots = new HashMap<>();
        this.storage = new Stack<>();
    }

    public static DroneManager getInstance() {
        if (instance == null) instance = new DroneManagerImpl();
        return instance;
    }

    @Override
    public Drone addDrone(String id, String name, String manufacturer, String model) {
        logger.info("addDrone(" + id + ", " + name + ", " + manufacturer + ", " + model + ")");
        Drone d = new Drone(id, name, manufacturer, model);
        this.drones.put(id, d);
        logger.info("addDrone(" + id + "): " + d);
        return d;
    }

    @Override
    public Pilot addPilot(String id, String name, String surname) {
        logger.info("addPilot(" + id + ", " + name + ", " + surname + ")");
        Pilot p = new Pilot(id, name, surname);
        this.pilots.put(id, p);
        logger.info("addPilot(" + id + "): " + p);
        return p;
    }

    @Override
    public List<Drone> getDronesByFlightHours() {
        logger.info("getDronesByFlightHours()");
        List<Drone> res = new ArrayList<>(this.drones.values());
        res.sort((d1, d2) -> Double.compare(d2.getFlightHours(), d1.getFlightHours()));
        logger.info("getDronesByFlightHours(): " + res);
        return res;
    }

    @Override
    public List<Pilot> getPilotsByFlightHours() {
        logger.info("getPilotsByFlightHours()");
        List<Pilot> res = new ArrayList<>(this.pilots.values());
        res.sort((p1, p2) -> Double.compare(p2.getFlightHours(), p1.getFlightHours()));
        logger.info("getPilotsByFlightHours(): " + res);
        return res;
    }

    @Override
    public Drone storeDrone(String droneId) throws DroneNotFoundException, DroneInMaintenanceException {
        logger.info("storeDrone(" + droneId + ")");
        Drone d = this.drones.get(droneId);
        if (d == null) {
            logger.error("storeDrone: el dron " + droneId + " no existeix");
            throw new DroneNotFoundException();
        }
        if (d.isInMaintenance()) {
            logger.error("storeDrone: el dron " + droneId + " ja és al magatzem");
            throw new DroneInMaintenanceException();
        }
        d.setInMaintenance(true);
        this.storage.push(d);
        logger.info("storeDrone(" + droneId + "): magatzem = " + this.storage.size());
        return d;
    }

    @Override
    public Drone repairDrone() throws EmptyStorageException {
        logger.info("repairDrone()");
        if (this.storage.isEmpty()) {
            logger.error("repairDrone: el magatzem és buit");
            throw new EmptyStorageException();
        }
        Drone d = this.storage.pop();
        d.setInMaintenance(false);
        logger.info("repairDrone(): " + d);
        return d;
    }

    @Override
    public FlightPlan addFlightPlan(String droneId, String pilotId, String date, double hours, List<Position> positions)
            throws DroneNotFoundException, PilotNotFoundException, DroneInMaintenanceException, FlightPlanConflictException {
        logger.info("addFlightPlan(" + droneId + ", " + pilotId + ", " + date + ", " + hours + ", " + positions + ")");
        Drone d = this.drones.get(droneId);
        if (d == null) {
            logger.error("addFlightPlan: el dron " + droneId + " no existeix");
            throw new DroneNotFoundException();
        }
        Pilot p = this.pilots.get(pilotId);
        if (p == null) {
            logger.error("addFlightPlan: el pilot " + pilotId + " no existeix");
            throw new PilotNotFoundException();
        }
        if (d.isInMaintenance()) {
            logger.error("addFlightPlan: el dron " + droneId + " és al magatzem");
            throw new DroneInMaintenanceException();
        }
        if (hasConflict(d.getFlightPlans(), date, hours)) {
            logger.error("addFlightPlan: el dron " + droneId + " ja té un pla en aquest interval");
            throw new FlightPlanConflictException();
        }
        if (hasConflict(p.getFlightPlans(), date, hours)) {
            logger.error("addFlightPlan: el pilot " + pilotId + " ja té un pla en aquest interval");
            throw new FlightPlanConflictException();
        }
        FlightPlan fp = new FlightPlan(droneId, pilotId, date, hours, positions);
        d.addFlightPlan(fp);
        p.addFlightPlan(fp);
        logger.info("addFlightPlan(): " + fp);
        return fp;
    }

    private boolean hasConflict(List<FlightPlan> plans, String date, double hours) {
        logger.info("hasConflict(" + plans.size() + " plans, " + date + ", " + hours + ")");
        LocalDateTime start = LocalDateTime.parse(date);
        LocalDateTime end = start.plusMinutes(Math.round(hours * 60));
        boolean res = false;
        for (FlightPlan fp : plans) {
            LocalDateTime s = LocalDateTime.parse(fp.getDate());
            LocalDateTime e = s.plusMinutes(Math.round(fp.getHours() * 60));
            if (start.isBefore(e) && s.isBefore(end)) res = true;
        }
        logger.info("hasConflict(" + date + ", " + hours + "): " + res);
        return res;
    }

    @Override
    public List<FlightPlan> getFlightPlansByPilot(String pilotId) throws PilotNotFoundException {
        logger.info("getFlightPlansByPilot(" + pilotId + ")");
        Pilot p = this.pilots.get(pilotId);
        if (p == null) {
            logger.error("getFlightPlansByPilot: el pilot " + pilotId + " no existeix");
            throw new PilotNotFoundException();
        }
        List<FlightPlan> res = new ArrayList<>(p.getFlightPlans());
        logger.info("getFlightPlansByPilot(" + pilotId + "): " + res);
        return res;
    }

    @Override
    public List<FlightPlan> getFlightPlansByDrone(String droneId) throws DroneNotFoundException {
        logger.info("getFlightPlansByDrone(" + droneId + ")");
        Drone d = this.drones.get(droneId);
        if (d == null) {
            logger.error("getFlightPlansByDrone: el dron " + droneId + " no existeix");
            throw new DroneNotFoundException();
        }
        List<FlightPlan> res = new ArrayList<>(d.getFlightPlans());
        logger.info("getFlightPlansByDrone(" + droneId + "): " + res);
        return res;
    }

    @Override
    public Drone getDrone(String id) {
        logger.info("getDrone(" + id + ")");
        Drone d = this.drones.get(id);
        logger.info("getDrone(" + id + "): " + d);
        return d;
    }

    @Override
    public Pilot getPilot(String id) {
        logger.info("getPilot(" + id + ")");
        Pilot p = this.pilots.get(id);
        logger.info("getPilot(" + id + "): " + p);
        return p;
    }

    @Override
    public int numDrones() {
        int ret = this.drones.size();
        logger.info("numDrones " + ret);
        return ret;
    }

    @Override
    public int numPilots() {
        int ret = this.pilots.size();
        logger.info("numPilots " + ret);
        return ret;
    }

    @Override
    public int numDronesInStorage() {
        int ret = this.storage.size();
        logger.info("numDronesInStorage " + ret);
        return ret;
    }

    @Override
    public void clear() {
        logger.info("clear()");
        this.drones.clear();
        this.pilots.clear();
        this.storage.clear();
        logger.info("clear(): estructures buides");
    }
}