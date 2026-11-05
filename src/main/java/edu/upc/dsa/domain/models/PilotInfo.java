package edu.upc.dsa.domain.models;

public class PilotInfo {

    private String id;
    private String name;
    private String surname;
    private double flightHours;

    public PilotInfo() {}

    public PilotInfo(Pilot p) {
        this.id = p.getId();
        this.name = p.getName();
        this.surname = p.getSurname();
        this.flightHours = p.getFlightHours();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public double getFlightHours() { return flightHours; }
    public void setFlightHours(double flightHours) { this.flightHours = flightHours; }
}