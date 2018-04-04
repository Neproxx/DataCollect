package dataCollector;

import sqlite.SqliteDB;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;

/*
 * Flow: Init Database -> does data already exist -> ask what user wants to do, e.g. daily routine data insertion? add new table?
 * implement methods to extend the DB
 * implement methods to add Data to the DB
 * 
 */

public class Client {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		System.out.println("Hello User!");
		
		SqliteDB db = new SqliteDB();
		
		LinkedList<String> names = db.getTables();
		if(names!=null) {
			System.out.println("Currently stored tables:");
			for(int i=0; i<names.size(); i++) {
				System.out.println(names.get(i));
			}
		}
		else
			System.out.println("No currently stored tables!");
		
		LinkedList<String> colNames = new LinkedList<String>(Arrays.asList("krank", "muede", "schmerzen"));
		LinkedList<String> colTypes = new LinkedList<String>(Arrays.asList("VARCHAR(60)", "INTEGER", "REAL"));
		boolean success = db.addTable("daily", colNames, colTypes, "krank");
		if(success)
			System.out.println("Table was successfully created!");

		names = db.getTables();
		if(names!=null) {
			System.out.println("Currently stored tables:");
			for(int i=0; i<names.size(); i++) {
				System.out.println(names.get(i));
			}
		}
		else
			System.out.println("No currently stored tables!");
		
		
		/*
		try {
			res = db.displayUsers();
			
			while(res.next()) {
				// ResultSet liefert interface um die Werte aus bestimmten Spalten der aktuellen Reihe zu bekommen
				System.out.println(res.getString("fName") + " " + res.getString("lName"));
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e.getMessage());
		}
		*/
	}
}
