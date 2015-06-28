package de.tshw.worktracker.model;

/**
 * Simple abstraction for an entity stored in the database.
 *
 * <p>Inherit from this class and create a DAO inheriting from {@link de.tshw.worktracker.dao.DataAccessObject} to setup
 * a simple and straight-forward database read and write access for your object.
 *
 * <p>The system will automatically take care of IDs, so just add the fields you need. All of the mapping will
 * be done directly in the DAO.
 */
public abstract class Entity {

    private Long id;

    public Entity() {
        this.id = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void clearId() {
        this.id = null;
    }
}
