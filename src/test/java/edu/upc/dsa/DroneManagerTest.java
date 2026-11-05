package edu.upc.dsa;

import edu.upc.dsa.domain.models.Drone;
import edu.upc.dsa.domain.models.FlightPlan;
import edu.upc.dsa.domain.models.Pilot;
import edu.upc.dsa.domain.models.Position;
import edu.upc.dsa.domain.repository.DroneManager;
import edu.upc.dsa.domain.repository.DroneManagerImpl;
import edu.upc.dsa.exceptions.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DroneManagerTest {
    DroneManager dm;
    List<Position> positions;


    @Before
    public void setUp() {
        this.dm = DroneManagerImpl.getInstance();
        this.dm.addDrone("D1", "Falcon", "DJI", "Mavic 3");
        this.dm.addDrone("D2", "Eagle", "Parrot", "Anafi");
        this.dm.addDrone("D3", "Hawk", "DJI", "Mini 4");
        this.dm.addPilot("P1", "Anna", "Puig");
        this.dm.addPilot("P2", "Marc", "Soler");
        this.positions = new ArrayList<>();
        this.positions.add(new Position(41.27, 1.98));
        this.positions.add(new Position(41.28, 1.99));
    }


    @After
    public void tearDown() {
        this.dm.clear();
    }

    //probem d'afegir dron i pilot
    @Test
    public void testAddDroneAndPilot() {
        Assert.assertEquals(3, this.dm.numDrones());
        Assert.assertEquals(2, this.dm.numPilots());
        this.dm.addDrone("D4", "Owl", "Autel", "EVO");
        this.dm.addPilot("P3", "Laia", "Vidal");
        Assert.assertEquals(4, this.dm.numDrones());
        Assert.assertEquals(3, this.dm.numPilots());
        Assert.assertEquals("Owl", this.dm.getDrone("D4").getName());
        Assert.assertEquals("Vidal", this.dm.getPilot("P3").getSurname());
    }

    //provem guardar i treure drons del stack
    @Test
    public void testStorageAndRepair() throws Exception {
        this.dm.storeDrone("D1");
        this.dm.storeDrone("D2");
        Assert.assertEquals(2, this.dm.numDronesInStorage());
        Assert.assertTrue(this.dm.getDrone("D1").isInMaintenance());
        Assert.assertThrows(DroneInMaintenanceException.class, () -> this.dm.storeDrone("D1"));
        Assert.assertThrows(DroneNotFoundException.class, () -> this.dm.storeDrone("XXX"));


        Drone d = this.dm.repairDrone();
        Assert.assertEquals("D2", d.getId());
        Assert.assertFalse(d.isInMaintenance());
        Assert.assertEquals("D1", this.dm.repairDrone().getId());
        Assert.assertThrows(EmptyStorageException.class, () -> this.dm.repairDrone());
    }

    //afegim pla de vol
    @Test
    public void testAddFlightPlan() throws Exception {
        FlightPlan fp = this.dm.addFlightPlan("D1", "P1", "2026-11-05T10:00", 2, this.positions);
        Assert.assertEquals("D1", fp.getDroneId());
        Assert.assertEquals(2, fp.getPositions().size());
        Assert.assertEquals(1, this.dm.getFlightPlansByDrone("D1").size());
        Assert.assertEquals(1, this.dm.getFlightPlansByPilot("P1").size());

        Assert.assertThrows(FlightPlanConflictException.class,
                () -> this.dm.addFlightPlan("D1", "P2", "2026-11-05T11:00", 1, this.positions));
        Assert.assertThrows(FlightPlanConflictException.class,
                () -> this.dm.addFlightPlan("D2", "P1", "2026-11-05T09:30", 1, this.positions));
        this.dm.addFlightPlan("D1", "P1", "2026-11-05T12:00", 1, this.positions);
        Assert.assertEquals(2, this.dm.getFlightPlansByDrone("D1").size());


        this.dm.storeDrone("D3");
        Assert.assertThrows(DroneInMaintenanceException.class,
                () -> this.dm.addFlightPlan("D3", "P2", "2026-11-06T10:00", 1, this.positions));
        Assert.assertThrows(DroneNotFoundException.class,
                () -> this.dm.addFlightPlan("XXX", "P1", "2026-11-06T10:00", 1, this.positions));
        Assert.assertThrows(PilotNotFoundException.class,
                () -> this.dm.addFlightPlan("D1", "XXX", "2026-11-06T10:00", 1, this.positions));
    }

    //llista ordenada per h
    @Test
    public void testListsByFlightHours() throws Exception {
        this.dm.addFlightPlan("D2", "P2", "2026-11-05T10:00", 5, this.positions);
        this.dm.addFlightPlan("D1", "P1", "2026-11-05T10:00", 2, this.positions);

        this.dm.addFlightPlan("D2", "P1", "2026-11-06T10:00", 1, this.positions);

        List<Drone> drones = this.dm.getDronesByFlightHours();
        Assert.assertEquals("D2", drones.get(0).getId());
        Assert.assertEquals(6, drones.get(0).getFlightHours(), 0.0001);
        Assert.assertEquals("D1", drones.get(1).getId());
        Assert.assertEquals("D3", drones.get(2).getId());


        List<Pilot> pilots = this.dm.getPilotsByFlightHours();
        Assert.assertEquals("P2", pilots.get(0).getId());
        Assert.assertEquals(5, pilots.get(0).getFlightHours(), 0.0001);
        Assert.assertEquals("P1", pilots.get(1).getId());
        Assert.assertEquals(3, pilots.get(1).getFlightHours(), 0.0001);
    }
}