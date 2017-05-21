package model;

public class Laboratory {
    private int id;
    private String name;
    private int capacity;

    public Laboratory() {}

    public Laboratory(int id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }
}
