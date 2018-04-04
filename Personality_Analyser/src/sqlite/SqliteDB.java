package sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class SqliteDB {
	
	private static Connection con;
	private static boolean hasData = false;
	
	public ResultSet displayUsers() throws ClassNotFoundException, SQLException {
		// Check whether there is a connection and set one up if needed
		if (con == null) {
			getConnection();
		}
		
		//work on DB
		Statement stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT fname, lname FROM user");
		return res;
	}
	
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
	public LinkedList<String> getTables() throws ClassNotFoundException, SQLException{
		if(con==null) {
			getConnection();
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
	
	/**
	 * Creates a new table in the DB. Entry at index i of columnNames and columnTypes belong together
	 * @param name name of the table
	 * @param columnNames list of names for the columns. The size of this list equals the amount of columns
	 * @param columnTypes ist of types for the columns. Must be the same size as columNames
	 * @param primary the column that should be used as primary key. Must be one of the names in columnNames
	 * @return true if queue has been successful, false otherwise
	 */
	public boolean addTable(String name, List<String> columnNames, List<String>columnTypes, String primary) {
		// check if parameters are valid and try to connect to DB
		if(name == null || columnNames == null || columnTypes == null) {
			System.out.println("Error: Invalid Parameters for addTable");
			return false;
		}
		else if(columnNames.size() != columnTypes.size()) {
			System.out.println("Error: Number of Names and Types have to be the same, to addTable!");
			return false;
		}
		if(con == null)
			try {
				getConnection();
			} catch (ClassNotFoundException | SQLException e1) {
				System.out.println("Unable to connect to DB");
				return false;
			}
		
		// build query string and try to execute query
		String query = String.format("CREATE TABLE %s(", name);
		for(int i = 0; i < columnNames.size(); i++)
			query += (String.format("%s", columnNames.get(i)) + String.format(" %s, ", columnTypes.get(i)));
		
		query += String.format("PRIMARY KEY(%s));", primary);
		
		// DEBUG PRINT
		System.out.println("QUERY to be executed: ");
		System.out.println(query);
		// DEBUG PRINT
		
		try {
			// TODO: check ob PreparedStatement beim preparen einen Error wirft, sodass die execution garnicht
			// erst probier wird, wodurch auch keine seltsame Tabelle entsteht
			PreparedStatement stmt = con.prepareStatement("CREATE TABLE name(user VARCHAR(50));");
			stmt.executeUpdate();
			return true;
		}
		catch(SQLException e) {
			System.out.println(String.format("Creation of table %s was not successful:", name));
			System.out.println(e.getMessage());
			// TODO: entferne die seltsame Tabelle, die trotzdem entsteht, selbst wenn CREATE Table fehlschlaegt
			// Ansatz: suche ob tabelle mit dem namen existiert und loesche sie FALLS keine Eintraege drin
			// ansonsten loeschen wir vielleicht eine, die schon laenger existierte
			return false;
		}
	}
}

	/*
	public void addUser(String fName, String lName) throws ClassNotFoundException, SQLException {
		
		if(con == null) {
			getConnection();
		}
		
		PreparedStatement stmt = con.prepareStatement("INSERT INTO user VALUES(?,?,?);");
		stmt.setString(2, fName);
		stmt.setString(3, lName);
		stmt.execute();
	}
	*/

/* old
  	public ResultSet displayUsers() throws ClassNotFoundException, SQLException {
		// Check whether there is a connection and set one up if needed
		if (con == null) {
			getConnection();
		}
		
		//work on DB
		Statement stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT fname, lname FROM user");
		return res;
	}
	
	private void getConnection() throws ClassNotFoundException, SQLException {
		// JDBC = Java Database Connectivity - a standard to provide an interface to connect java to relational DBs
		// Different versions are possible
		// this is the standard way to load a JDBC driver, it registers in DriverManagar
		Class.forName("org.sqlite.JDBC"); 										//may throw ClassNotFoundException
		
		// Since jdbc is now registered at DriverManager, we can establish a connection
		con = DriverManager.getConnection("jdbc:sqlite:PersonalityData.db");	//may throw SQLException
		initalise();
	}

	// TO-DO: ask for name of the person track data
	private void initalise() throws SQLException {
		if(!hasData) {
			hasData = true;
			Statement stmt = con.createStatement();
			
			// sqlite_master ist die master table in jeder SQL DB und speichert Daten ueber alle anderen Tabellen
			// was wir hier also checken ist, ob es eine 'table' mit Namen 'user' bereits gibt, die wir verwenden koennen
			ResultSet res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='user';");
			
			if(!res.next()) {
				// Das ResultSet ist leer und wir muessen eine Tabelle erstellen
				System.out.println("Building User Table with prepopulated values.");
				
				// It almost always is better to use prepared Statements!
				PreparedStatement create_stmt = con.prepareStatement("CREATE TABLE user(id INTEGER, fName VARCHAR(60), lName VARCHAR(60), PRIMARY KEY(id));");
				// when updating the table, executeUpdate() instead of executeStatement() must be used
				create_stmt.executeUpdate();
				
				// fuege Daten ein
				PreparedStatement prepStmt = con.prepareStatement("INSERT INTO user VALUES(?,?,?);");
				prepStmt.setString(2, "Marcel");
				prepStmt.setString(3, "Juschak");
				prepStmt.execute();
				
			}
			else {
				System.out.println("user table already exists!");
			}
		}
	}
	
	public void addUser(String fName, String lName) throws ClassNotFoundException, SQLException {
		
		if(con == null) {
			getConnection();
		}
		
		PreparedStatement stmt = con.prepareStatement("INSERT INTO user VALUES(?,?,?);");
		stmt.setString(2, fName);
		stmt.setString(3, lName);
		stmt.execute();
	}
*/
