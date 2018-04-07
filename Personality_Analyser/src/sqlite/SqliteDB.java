package sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import dataTypes.Pair;

public class SqliteDB {
	
	private static Connection con;
	
	/**
	 * Tries to connect to a DB named "PersonalityData.db". If none exists, creates one.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void getConnection() throws ClassNotFoundException, SQLException {
		// JDBC = Java Database Connectivity - a standard to provide an interface to connect java to relational DBs
		// Different versions are possible
		// this is the standard way to load a JDBC driver, it registers in DriverManagar
		Class.forName("org.sqlite.JDBC"); 										//may throw ClassNotFoundException
		
		// Since jdbc is now registered at DriverManager, we can establish a connection
		con = DriverManager.getConnection("jdbc:sqlite:PersonalityData.db");	//may throw SQLException
	}
	
	/**
	 * Retrieves the names of all tables that are currently stored in the DB
	 * @return a LinkedList, containing the names of all stored tables. NULL if there are no tables.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @return a List, containing the names of all stored tables as Strings
	 */
	public LinkedList<String> getTables(){
		try {
			if(con == null)
				try {
					getConnection();
				} catch (ClassNotFoundException | SQLException e1) {
					System.out.println("Unable to connect to DB, could not retrieve existing tables");
					return null;
				}
			
			// retrieve names of tables in DB
			ResultSet res;
			PreparedStatement stmt = con.prepareStatement("SELECT name FROM sqlite_master WHERE type='table';");
			res = stmt.executeQuery();
			
			// store names of tables in return Array
			boolean containsData = false;
			LinkedList<String> tableNames = new LinkedList<String>();
			while(res.next()) {
				containsData = true;
				tableNames.add(res.getString(1));
			}
			if(containsData)
				return tableNames;
			else
				return null;
		}
		catch(SQLException e) {
			System.out.println("Could not retrieve current Tables");
			return null;
		}
	}
	
	/**
	 * prints the tables stored in the DB, that the user uses to store data, i.e. excluding tables for internal administration/metadata
	 */
	public void printUserTables() {
		LinkedList<String> tables = this.getTables();
		if(tables!=null) {
			System.out.println("Currently stored tables are:");
			for(int i=0; i<tables.size(); i++) {
				System.out.println("\"" + tables.get(i) + "\"");
			}
		}
		else
			System.out.println("Currently there are no stored tables.");
	}
	
	/**
	 * Creates a new table in the DB. Entry at index i of columnNames and columnTypes belong together
	 * @param name name of the table
	 * @param columnNames list of names for the columns. The size of this list equals the amount of columns
	 * @param columnTypes ist of types for the columns. Must be the same size as columNames
	 * @param primary the column that should be used as primary key. Must be one of the names in columnNames
	 * @param userTable specifies whether this table should be a userTable, i.e. needs extra metadata
	 * @param defaultPrimary true if the default Primary, i.e. "ID" is chosen. The primary will be updated internally without awareness of the user.
	 * @return true if queue has been successful, false otherwise
	 */
	public boolean addTable(String tableName, List<Pair<String, String>> columnDefinitions, String primary, boolean userTable, boolean defaultPrimary) {
		// check if parameters are valid and try to connect to DB
		if(tableName == null || columnDefinitions == null) {
			System.out.println("Error: Invalid Parameters for addTable");
			return false;
		}
		//check if connection exists and initiate it if needed
		if(con == null)
			try {
				getConnection();
			} catch (ClassNotFoundException | SQLException e1) {
				System.out.println("Unable to connect to DB, could not add the Table \"" + tableName + "\"");
				return false;
			}
		
		// check if Table already exists
		LinkedList<String> existingTables = getTables();
		if(existingTables!= null && existingTables.contains(tableName)) {
			System.out.println("Database already contains Table with name \"" + tableName + "\"");
			return false;
		}
		
		// build query string
		String query = String.format("CREATE TABLE %s(", tableName);
		for(int i = 0; i < columnDefinitions.size(); i++)
			query += (String.format("%s", columnDefinitions.get(i).getKey()) + String.format(" %s, ", columnDefinitions.get(i).getValue()));
		
		query += String.format("PRIMARY KEY(%s));", primary);
		
		// DEBUG PRINT
		System.out.println("QUERY to be executed: ");
		System.out.println(query);
		// DEBUG PRINT
		
		// TODO: add metadata to metadata table
		
		try {
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.executeUpdate();
			return true;
		}
		catch(SQLException e) {
			System.out.println(String.format("Creation of table \"%s\" was not successful:", tableName));
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	/**
	 * states whether a table with the given name already exists in the Database
	 * @param tableName
	 * @return true if Database contains table with the given name
	 */
	public boolean exists(String tableName) {
		LinkedList<String> tables = getTables();
		if(tables==null)
			return false;
		if(tables.contains(tableName))
			return true;
		else
			return false;
	}


	/**
	 * adds and Entry (row) into the specified table
	 * @param tableName Name of the table to be inserted into
	 * @param values a List of Values, that ist to be inserted as one row into the table
	 * @return true if successful, false otherwise
	 */
	public boolean addEntry(String tableName, List<Object> values){
		if(con == null)
			try {
				getConnection();
			} catch (ClassNotFoundException | SQLException e1) {
				System.out.println("Unable to connect to DB, could not add the Entry into table \"" + tableName + "\"");
				return false;
			}
		
		// build query string
		String query = String.format("INSERT INTO %s VALUES(", tableName);
		for(int i = 0; i < values.size() - 1; i++)
			query += "?,";
		query += "?);";
		
		try {
			//execute Update
			PreparedStatement stmt = con.prepareStatement(query);
			for(int i=0; i<values.size(); i++)
				stmt.setObject(i+1, values.get(i));
			stmt.executeUpdate();
			return true;
		}
		catch(SQLException e) {
			// Print which Entry has not been added
			System.out.println("Could not add Entry into table \"" + tableName + "\"");
			System.out.println("Unadded Entry:");
			System.out.print("(");
			for(int i=0; i < values.size() - 1; i++)
				System.out.print(values.get(i) + ", ");
			System.out.println(values.get(values.size()-1) + ")");
			return false;
		}
	}
}
