package de.tshw.worktracker.model;

import org.joda.time.LocalDateTime;

public class WorkLogEntry extends Entity {

    private Project project;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String comment;

    public WorkLogEntry() {
        super();
        this.project = null;
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.comment = null;
    }

    public WorkLogEntry(Project project) {
        super();
        this.project = project;
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.comment = null;
    }

    public WorkLogEntry(Project project, LocalDateTime startTime, LocalDateTime endTime, String comment) {
        super();
        this.project = project;
        this.startTime = startTime;
        this.endTime = endTime;
        this.comment = comment;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isRunning() {
        return (this.endTime == null);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void stop() {
        this.endTime = LocalDateTime.now();
    }
}
