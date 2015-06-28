package de.tshw.worktracker.dao;

import de.tshw.worktracker.model.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectDAO extends DataAccessObject<Project> {

    public ProjectDAO(DatabaseProvider dbProvider) {
        super(dbProvider);
    }

    @Override
    protected String getEntityName() {
        return Project.class.getSimpleName();
    }

    @Override
    protected String[] getFieldList() {
        return new String[]{"name"};
    }

    @Override
    protected String getDefaultOrderBy() {
        return "name ASC";
    }

    @Override
    protected Project mapFieldsToEntity(ResultSet result) {
        Project project = null;
        try {
            project = new Project(result.getString("name"));
        } catch (SQLException ex) {
            System.err.println("Error while mapping Project entity: " + ex.getMessage());
        }
        return project;
    }

    @Override
    protected void setInsertUpdateParameters(PreparedStatement statement, Project entity) throws SQLException {
        statement.setString(1, entity.getName());
    }

    public Project findByName(String name) {
        Project project = null;
        try {
            Connection connection = getDbProvider().getDatabaseConnection();
            PreparedStatement statement = connection.prepareStatement(getSelectByNameStatement());
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                project = this.getEntityFromResultSet(result);
            }
        } catch (Exception ex) {
            System.err.println("Failed to retrieve Project by name. (" + ex.getClass().getName() + ": " + ex.getMessage());
        }
        return project;
    }

    private String getSelectByNameStatement() {
        return "SELECT " + this.getIdColumnName() + ", " + String.join(", ", this.getFieldList()) +
                " FROM " + this.getTableName() +
                " WHERE name = ?";
    }

}
