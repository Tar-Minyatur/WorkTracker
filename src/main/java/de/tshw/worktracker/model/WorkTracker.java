package de.tshw.worktracker.model;

import java.util.HashSet;
import java.util.Set;

public class WorkTracker {

    private Set<Project> projects;
    private WorkLogEntry currentLogEntry;

    public WorkTracker() {
        this.projects = new HashSet<Project>();
        this.currentLogEntry = null;
    }

    public WorkLogEntry getCurrentLogEntry() {
        return currentLogEntry;
    }

    public void setCurrentLogEntry(WorkLogEntry currentLogEntry) {
        this.currentLogEntry = currentLogEntry;
    }

    public void addProject(Project project) {
        this.projects.add(project);
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public WorkLogEntry switchProject(Project project) {
        if (this.currentLogEntry != null) {
            this.currentLogEntry.stop();
        }

        WorkLogEntry newLogEntry = new WorkLogEntry(project);
        this.currentLogEntry = newLogEntry;

        return newLogEntry;
    }
}
