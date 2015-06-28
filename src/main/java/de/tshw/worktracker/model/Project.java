package de.tshw.worktracker.model;

public class Project extends Entity {

    private String name;

    public Project() {
        super();
    }

    public Project(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
