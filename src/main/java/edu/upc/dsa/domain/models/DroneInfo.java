package edu.upc.dsa.domain.models;

public class DroneInfo {

    private String id;
    private String name;
    private String manufacturer;
    private String model;
    private double flightHours;
    private boolean inMaintenance;

    public DroneInfo() {}

    public DroneInfo(Drone d) {
        this.id = d.getId();
        this.name = d.getName();
        this.manufacturer = d.getManufacturer();
        this.model = d.getModel();
        this.flightHours = d.getFlightHours();
        this.inMaintenance = d.isInMaintenance();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getFlightHours() { return flightHours; }
    public void setFlightHours(double flightHours) { this.flightHours = flightHours; }
    public boolean isInMaintenance() { return inMaintenance; }
    public void setInMaintenance(boolean inMaintenance) { this.inMaintenance = inMaintenance; }
}