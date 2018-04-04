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
		
		LinkedList<String> existingTables = db.getTables();
		if(existingTables != null) {
			System.out.println("Currently stored tables:");
			for(int i=0; i<existingTables.size(); i++) {
				System.out.println(existingTables.get(i));
			}
		}
		else
			System.out.println("No currently stored tables!");
		System.out.println("=================");
		
		/*
		// add a table for testing
		LinkedList<String> colNames = new LinkedList<String>(Arrays.asList("krank", "muede", "schmerzen"));
		LinkedList<String> colTypes = new LinkedList<String>(Arrays.asList("VARCHAR(60)", "INTEGER", "REAL"));
		boolean success = db.addTable("test", colNames, colTypes, "krank");
		if(success)
			System.out.println("Table was successfully created!");

		// check again what tables are stored
		existingTables = db.getTables();
		if(existingTables!=null) {
			System.out.println("Currently stored tables:");
			for(int i=0; i<existingTables.size(); i++) {
				System.out.println(existingTables.get(i));
			}
		}
		else
			System.out.println("No currently stored tables!");
		
		
		// try to add some Entries
		LinkedList<Object> data1 = new LinkedList<>(); data1.add("a"); data1.add(1); data1.add(1.5);
		LinkedList<Object> data2 = new LinkedList<>(); data2.add("nein"); data2.add(100); data2.add(99.5);
		LinkedList<Object> data3 = new LinkedList<>(); data3.add("ieLLEicht"); data3.add(5999); data3.add(1.3421);
		
		db.addEntry("wrong", data1);
		db.addEntry("wrong", data2);
		db.addEntry("wrong", data3);
		
		*/
		
		
	}
}
