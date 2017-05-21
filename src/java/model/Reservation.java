package model;

public class Reservation {
    private int id;
    private String labName;
    private String startTime;
    private String endTime;
    private String userName;

    public Reservation() {}

    public Reservation(int id, String labName, String startTime, String endTime, String userName) {
        this.id = id;
        this.labName = labName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public String getLabName() {
        return labName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getUserName() {
        return userName;
    }
    
}
