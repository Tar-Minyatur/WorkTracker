package de.tshw.worktracker.dao;

import de.tshw.worktracker.model.WorkLogEntry;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkLogEntryDAO extends DataAccessObject<WorkLogEntry> {

    private final ProjectDAO projectDAO;

    public WorkLogEntryDAO(DatabaseProvider dbProvider, ProjectDAO projectDAO) {
        super(dbProvider);
        this.projectDAO = projectDAO;
    }

    @Override
    protected String getEntityName() {
        return WorkLogEntry.class.getSimpleName();
    }

    @Override
    protected String[] getFieldList() {
        return new String[]{"project_id", "start_time", "end_time", "comment"};
    }

    @Override
    protected String getDefaultOrderBy() {
        return "start_time ASC";
    }

    @Override
    protected WorkLogEntry mapFieldsToEntity(ResultSet result) {
        WorkLogEntry entry = null;
        try {
            entry = new WorkLogEntry(projectDAO.findById(result.getLong("project_id")));
            entry.setStartTime(LocalDateTime.fromDateFields(result.getTimestamp("start_time")));
            if (result.getDate("end_time") != null) {
                entry.setEndTime(LocalDateTime.fromDateFields(result.getTimestamp("end_time")));
            }
            entry.setComment(result.getString("comment"));
        } catch (SQLException ex) {
            System.err.println("Error while mapping WorkLogEntry entity: " + ex.getMessage());
        }
        return entry;
    }

    @Override
    protected void setInsertUpdateParameters(PreparedStatement statement, WorkLogEntry entity) throws SQLException {
        statement.setLong(1, entity.getProject().getId());
        statement.setDate(2, new Date(entity.getStartTime().toDate().getTime()));
        if (entity.getEndTime() == null) {
            statement.setNull(3, Types.DATE);
        } else {
            statement.setDate(3, new Date(entity.getEndTime().toDate().getTime()));
        }
        if (entity.getComment() == null) {
            statement.setNull(4, Types.VARCHAR);
        } else {
            statement.setString(4, entity.getComment());
        }
    }

    public List<WorkLogEntry> findByDay(LocalDate day) {
        List<WorkLogEntry> entries = new ArrayList<>();
        try {
            Connection connection = getDbProvider().getDatabaseConnection();
            PreparedStatement statement = connection.prepareStatement(getSelectByDayStatement());
            statement.setDate(1, new Date(day.toDate().getTime()));
            statement.setDate(2, new Date(day.toDate().getTime()));
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                WorkLogEntry entry = getEntityFromResultSet(result);
                entries.add(entry);
            }
        } catch (Exception ex) {
            System.err.println("Failed to retrieve WorkLogEntries by day (Error: " + ex.getMessage() + ")");
        }
        return entries;
    }

    public List<WorkLogEntry> findWithoutEndDate() {
        List<WorkLogEntry> entries = new ArrayList<>();
        try {
            Connection connection = getDbProvider().getDatabaseConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(getSelectWithoutEndDateStatement());
            while (result.next()) {
                WorkLogEntry entry = getEntityFromResultSet(result);
                entries.add(entry);
            }
        } catch (Exception ex) {
            System.err.println("Failed to retrieve WorkLogEntries without end date (Error: " + ex.getMessage() + ")");
        }
        return entries;
    }

    @Override
    protected void ensureForeignKeyConsistency(WorkLogEntry entity) {
        this.projectDAO.save(entity.getProject());
    }

    private String getSelectWithoutEndDateStatement() {
        return "SELECT " + this.getIdColumnName() + ", " + String.join(", ", this.getFieldList()) +
                " FROM " + this.getTableName() +
                " WHERE end_time IS NULL";
    }

    private String getSelectByDayStatement() {
        return "SELECT " + this.getIdColumnName() + ", " + String.join(", ", this.getFieldList()) +
                " FROM " + this.getTableName() +
                " WHERE DATE(start_time) = ? OR DATE(end_time) = ?";
    }
}
