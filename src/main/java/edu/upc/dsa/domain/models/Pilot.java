package edu.upc.dsa.domain.models;

import java.util.LinkedList;
import java.util.List;

public class Pilot {

    private String id;
    private String name;
    private String surname;
    private double flightHours;
    private List<FlightPlan> flightPlans;

    public Pilot() {
        this.flightPlans = new LinkedList<>();
    }

    public Pilot(String id, String name, String surname) {
        this();
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.flightHours = 0;
    }

    public void addFlightPlan(FlightPlan fp) {
        this.flightPlans.add(fp);
        this.flightHours = this.flightHours + fp.getHours();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public double getFlightHours() { return flightHours; }
    public void setFlightHours(double flightHours) { this.flightHours = flightHours; }

    public List<FlightPlan> getFlightPlans() { return flightPlans; }
    public void setFlightPlans(List<FlightPlan> flightPlans) { this.flightPlans = flightPlans; }

    @Override
    public String toString() {
        return "Pilot [id=" + id + ", name=" + name + ", surname=" + surname + ", flightHours=" + flightHours + "]";
    }
}