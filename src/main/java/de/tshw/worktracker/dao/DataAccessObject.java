/******************************************************************************
 * This file is part of WorkTracker, Copyright (c) 2015 Till Helge Helwig.    *
 *                                                                            *
 * WorkTracker is distributed under the MIT License (MIT), so feel free       *
 * to do whatever you want with this code. You may notify the author about    *
 * bugs via http://github.com/Tar-Minyatur/WorkTracker/issues, but be aware   *
 * that he is not (legally) obligated to provide support. You are using       *
 * this software at your own risk.                                            *
 ******************************************************************************/

package de.tshw.worktracker.dao;

import de.tshw.worktracker.model.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple abstraction for a data access object.
 * <p>
 * Inherit from this class to create a straight-forwards DAO to handle basic read and write
 * database operations for your entities.
 *
 * @param <T> Entity to be managed by this DAO, needs to inherit from {@link de.tshw.worktracker.model.Entity}
 */
public abstract class DataAccessObject<T extends Entity> {

	private final DatabaseProvider dbProvider;

	/**
	 * This will be used to store all known entities to prevent the creation of
	 * multiple copies.
	 */
	private Map<Long, T> objectCache;

	/**
	 * Create an instance of this DAO.
	 *
	 * @param dbProvider The provider that arranges the database connection
	 */
	public DataAccessObject( DatabaseProvider dbProvider ) {
		this.dbProvider = dbProvider;
		this.objectCache = new HashMap<>();
	}

	/**
	 * Retrieve all entities of this DAO's type from the database.
	 *
	 * @return List of all entities in the database, sorted by the default sorting defined in {@link
	 * DataAccessObject#getDefaultOrderBy()}
	 */
	public List<T> getAll() {
		List<T> entities = new ArrayList<>();
		try {
			Connection connection = getDbProvider().getDatabaseConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(getSelectAllStatement());
			while ( result.next() ) {
				T entity = this.getEntityFromResultSet(result);
				entities.add(entity);
			}
		}
		catch (Exception ex) {
			System.err.println(
					"Failed to retrieve entities from database. (" + ex.getClass().getName() + ": " + ex.getMessage() +
					")");
		}
		return entities;
	}

	/**
	 * Retrieve the database provider.
	 * <p>
	 * Usually used as {@code getDbProvider().getDatabaseConnection();} right before
	 * a database statement is issued.
	 *
	 * @return The provider that arranges the database connection
	 */
	protected DatabaseProvider getDbProvider() {
		return dbProvider;
	}

	/**
	 * Get the SQL statement to retrieve all entities of this DAO's type.
	 * <p>
	 * This statement contains no placeholders at all.
	 *
	 * @return SQL statement
	 */
	protected String getSelectAllStatement() {
		return "SELECT " + this.getIdColumnName() + ", " + String.join(", ", this.getFieldList()) +
			   " FROM " + this.getTableName() +
			   " ORDER BY " + this.getDefaultOrderBy();
	}

	/**
	 * Converts a single database query result into the actual entity object.
	 * <p>
	 * Also takes care of internal caching to prevent the creation of multiple objects for
	 * the same entity.
	 *
	 * @param result Query result, iterated to the row to be extracted
	 * @return Fully populated entity object
	 */
	protected T getEntityFromResultSet( ResultSet result ) {
		T entity = null;
		try {
			long id = result.getLong(getIdColumnName());
			if ( this.isEntityCached(id) ) {
				entity = this.getCachedEntity(id);
			} else {
				entity = mapFieldsToEntity(result);
				entity.setId(id);
				this.cacheEntity(id, entity);
			}
		}
		catch (Exception ex) {
			System.err.println("Failed to retrieve ID for entity of type " + this.getEntityName() + ". (" +
							   ex.getClass().getName() + ": " + ex.getMessage() + ")");
		}
		return entity;
	}

	/**
	 * Get the ID database column for this entity.
	 * <p>
	 * Overwrite this method only if you really need to change the name of the ID column.
	 *
	 * @return Name of the database field for this entity's ID
	 */
	protected String getIdColumnName() {
		return "id";
	}

	/**
	 * Get the list of database fields that are used for this entity.
	 * <p>
	 * The order is order of these fields is important and you will need it for the implementation
	 * of {@link DataAccessObject#setInsertUpdateParameters}.
	 *
	 * @return List of database fields for this entity
	 */
	protected abstract String[] getFieldList();

	/**
	 * Get the name of the database table to be used for this DAO's entity.
	 * <p>
	 * Only overwrite this method, if you don't want the name to be the same as the entity's.
	 *
	 * @return Name of the database table
	 */
	protected String getTableName() {
		return this.getEntityName();
	}

	/**
	 * Get the default sorting order statement.
	 * <p>
	 * This will be used by some of the standard SQL statements.
	 *
	 * @return Default SQL order by clause
	 */
	protected abstract String getDefaultOrderBy();

	/**
	 * Check if an entity is already cached.
	 * <p>
	 * Note: The class mostly deals with caching itself.
	 *
	 * @param id Cache entry key, usually the entity's ID
	 * @return true, if the entity is in the cache; false otherwise
	 */
	protected boolean isEntityCached( long id ) {
		return this.objectCache.containsKey(id);
	}

	/**
	 * Retrieve an entity from the object cache.
	 * <p>
	 * Note: The class mostly deals with caching itself.
	 *
	 * @param id Cache entry key, usually the entity's ID
	 * @return The cached entity or null, if it isn't cached
	 * @see DataAccessObject#isEntityCached(long)
	 */
	protected T getCachedEntity( long id ) {
		return this.objectCache.get(id);
	}

	/**
	 * This method is called when a database query result needs to be converted into an actual
	 * entity object.
	 * <p>
	 * The ResultSet has already been iterated to the correct row. Retrieve all of your fields
	 * from the result and place the values inside your newly created entity object.
	 * <p>
	 * Note: You don't have to set the ID yourself, the DAO will take care of it.
	 *
	 * @param result Database query result, iterated to the right row
	 * @return Fully populated entity object
	 */
	protected abstract T mapFieldsToEntity( ResultSet result );

	/**
	 * Store an entity in the object cache.
	 * <p>
	 * Note: The class mostly deals with caching itself.
	 *
	 * @param id     Cache entry key, usually the entity's ID
	 * @param object The actual object to be cached
	 */
	protected void cacheEntity( long id, T object ) {
		this.objectCache.put(id, object);
	}

	/**
	 * Get the name of the managed entity.
	 * This is used to determine the database table name and for error handling purposes.
	 * <p>
	 * If you need to change the name of the database table name, overwrite {@link DataAccessObject#getTableName()}.
	 *
	 * @return Name of the entity handled by this DAO
	 */
	protected abstract String getEntityName();

	/**
	 * Retrieve a single entity of this DAO's type from the database.
	 *
	 * @param id ID of the entity to retrieve
	 * @return Found entity or null if it wasn't found
	 */
	public T findById( long id ) {
		T entity = null;
		if ( !this.isEntityCached(id) ) {
			try {
				Connection connection = getDbProvider().getDatabaseConnection();
				PreparedStatement statement = connection.prepareStatement(getSelectByIdStatement());
				statement.setLong(1, id);
				ResultSet result = statement.executeQuery();
				if ( result.next() ) {
					entity = this.getEntityFromResultSet(result);
				}
			}
			catch (Exception ex) {
				System.err.println("Failed to retrieve entity from database. (" + ex.getClass().getName() + ": " +
								   ex.getMessage() + ")");
			}
		} else {
			entity = this.getCachedEntity(id);
		}
		return entity;
	}

	/**
	 * Get the SQL statement to retrieve a single entity via its ID.
	 * <p>
	 * This statement contains a single placeholder for the entity's ID.
	 *
	 * @return SQL statement
	 */
	protected String getSelectByIdStatement() {
		return "SELECT " + this.getIdColumnName() + ", " + String.join(", ", this.getFieldList()) +
			   " FROM " + this.getTableName() +
			   " WHERE " + this.getIdColumnName() + " = ?";
	}

	/**
	 * Save an entity to the database.
	 * <p>
	 * You can call this with a new or an already known entity. If it isn't in the database yet, it will
	 * be inserted and an ID will be assigned to it. Otherwise the existing entry will be updated.
	 *
	 * @param entity Entity to store in the database
	 */
	public void save( T entity ) {
		try {
			Connection connection = getDbProvider().getDatabaseConnection();
			this.ensureForeignKeyConsistency(entity);
			boolean entityIsKnown = ( entity.getId() != null );
			String sql = entityIsKnown ? getUpdateStatement() : getInsertStatement();
			PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			this.setInsertUpdateParameters(statement, entity);
			if ( entityIsKnown ) {
				statement.setLong(this.getFieldList().length + 1, entity.getId());
			}
			statement.execute();
			if ( entity.getId() == null ) {
				ResultSet result = statement.getGeneratedKeys();
				if ( ( result != null ) && result.next() ) {
					long newId = result.getLong(1);
					entity.setId(newId);
					this.cacheEntity(newId, entity);
				} else {
					System.err.println(
							"Failed to retrieve auto-generated id for inserted entity of type " + this.getEntityName() +
							".");
				}
			}
		}
		catch (Exception ex) {
			System.err.println(
					"Failed to save entity of type " + this.getEntityName() + ". (" + ex.getClass().getName() + ": " +
					ex.getMessage() + ")");
		}
	}

	/**
	 * Deletes a known entity from the database.
	 *
	 * @param entity Entity to delete
	 */
	public void delete( T entity ) {
		if ( entity.getId() != null ) {
			try {
				Connection connection = getDbProvider().getDatabaseConnection();
				PreparedStatement statement = connection.prepareStatement(getDeleteStatement());
				statement.setLong(1, entity.getId());
				statement.execute();
				entity.clearId();
			}
			catch (Exception ex) {
				System.err.println(
						"Failed to delete entity of type " + this.getEntityName() + ". (" + ex.getClass().getName() +
						": " + ex.getMessage() + ")");
			}
		}
	}

	/**
	 * Get the SQL statement to delete an existing entity from the database.
	 * <p>
	 * This statement contains a single placeholder for the entity's ID.
	 *
	 * @return SQL statement
	 */
	protected String getDeleteStatement() {
		return "DELETE FROM " + this.getTableName() +
			   " WHERE " + this.getIdColumnName() + " = ?";
	}

	/**
	 * Get the SQL statement to insert a new entity into the database.
	 * <p>
	 * This statement contains n placeholders, one for each field of the entity. The order follows
	 * the one introduced in {@link DataAccessObject#getFieldList()}.
	 *
	 * @return SQL statement
	 */
	protected String getInsertStatement() {
		return "INSERT INTO " + this.getTableName() + " (" +
			   String.join(", ", this.getFieldList()) + ") VALUES (" + this.getInsertPlaceholders() + ")";
	}

	/**
	 * Get the SQL statement to update an existing entity in the database.
	 * <p>
	 * This statement contains n+1 placeholders, where 1..n are for the entity's fields. The order follows
	 * the one introduced in {@link DataAccessObject#getFieldList()}. The n+1th parameter is for the entity's ID.
	 *
	 * @return SQL statement
	 */
	protected String getUpdateStatement() {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(this.getTableName()).append(" SET ");
		String[] fields = this.getFieldList();
		for ( int i = 0; i < fields.length; ++i ) {
			fields[i] += " = ?";
		}
		sql.append(String.join(", ", fields));
		sql.append(" WHERE ").append(this.getIdColumnName()).append(" = ?");
		return sql.toString();
	}

	/**
	 * Get a list of parameter placeholders to be used in the INSERT statement.
	 *
	 * @return String of placeholder ("?, ?, ?, ...")
	 */
	private String getInsertPlaceholders() {
		String[] placeholders = this.getFieldList();
		for ( int i = 0; i < placeholders.length; ++i ) {
			placeholders[i] = "?";
		}
		return String.join(", ", placeholders);
	}

	/**
	 * This method is called when the DAO is executing an insert or update statement to
	 * poopulate the field values.
	 * <p>
	 * Retrieve all values from your entity and assign them to the statement's parameters
	 * in the same order as you returned the field values in {@link DataAccessObject#getFieldList}.
	 * <p>
	 * The parameters are counted from 1..n, where n is the total number of fields.
	 *
	 * @param statement SQL stamement to be populated with parameter values
	 * @param entity    Entity to take the parameter values from
	 * @throws SQLException when accessing a parameter that doesn't exist
	 */
	protected abstract void setInsertUpdateParameters( PreparedStatement statement, T entity ) throws SQLException;

	/**
	 * Handle relationships to other entities.
	 * <p>
	 * If your entity has relationships with other entities, use this method to make sure they
	 * are stored to the database before a foreign key constraint can fail.
	 * <p>
	 * You might have to adjust your constructor to make sure you have access to a
	 * DAO for the related Entity.
	 *
	 * @param entity Entity that is about to be stored to the database
	 */
	protected void ensureForeignKeyConsistency( T entity ) {
	}
}
