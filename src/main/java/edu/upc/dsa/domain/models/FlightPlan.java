package edu.upc.dsa.domain.models;

import edu.upc.dsa.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class FlightPlan {

    private String id;
    private String droneId;
    private String pilotId;
    private String date;
    private double hours;
    private List<Position> positions;  //list de les posicions com al esquema

    public FlightPlan() {
        this.id = RandomUtils.getId();
        this.positions = new ArrayList<>();
    }

    public FlightPlan(String droneId, String pilotId, String date, double hours, List<Position> positions) {
        this();
        this.droneId = droneId;
        this.pilotId = pilotId;
        this.date = date;
        this.hours = hours;
        if (positions != null) this.positions = new ArrayList<>(positions);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDroneId() { return droneId; }
    public void setDroneId(String droneId) { this.droneId = droneId; }

    public String getPilotId() { return pilotId; }
    public void setPilotId(String pilotId) { this.pilotId = pilotId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

    public List<Position> getPositions() { return positions; }
    public void setPositions(List<Position> positions) { this.positions = positions; }

    @Override
    public String toString() {
        return "FlightPlan [id=" + id + ", droneId=" + droneId + ", pilotId=" + pilotId +
                ", date=" + date + ", hours=" + hours + ", positions=" + positions + "]";
    }
}
