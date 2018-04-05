package dataCollector;

import sqlite.SqliteDB;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.cli.*;
import java.io.BufferedReader;

/*
 * Flow: Init Database -> does data already exist -> ask what user wants to do, e.g. daily routine data insertion? add new table?
 * TODO: Aufrufen -> begruesst werden -> what do you want to do? -> Liste an Kommandos anzeigen
 * (--help IMMER als moeglichkeit)
 * TODO: Beim initialisieren der DB auch eine Tabelle mit Metadaten zu jeder Table anlegen, zB die Beschreibung, die der User angegeben hat
 * 		-> erweitere addTable um Parameter "Description"
 * TODO: UEBERLEG DIR EINE ALLGEMEIEN STRUKTUR WO WELCHE INTERAKTIVEN SACHEN GEMACHT WERDE UND WO WAS GEPRUEFT WIRD BEVOR DU IMPLEMENTIERST!!!!
 * 
 */

public class Client {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO: Init von Options in eigene Funktion auslagern?
		OptionGroup option = new OptionGroup();
		Option opt1 = new Option("t", "tables", false, "list all currently stored tables and their Descriptions");
		Option opt2 = new Option("iI", "insertInto", true, "add Entries to table with name \"tableName\"");
		Option opt3 = new Option("cT", "createTable", true, "create a new Table with the given Name");
		Option opt4 = new Option("h", "help", false, "list all possible Commands");		// TODO: realize this with helper function
		option.addOption(opt1); option.addOption(opt2); option.addOption(opt3); option.addOption(opt4);
		// TODO: add option
		System.out.println("Hello User!");
		System.out.println("Please choose what you want to do:");
		
		// TODO: print all possible Commands
		
		// Read things and respond accordingly
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		DefaultParser parser = new DefaultParser();
		
		while(true) {
			try {
				String input = in.readLine();
				String[] userInput = input.split(" ");
				//CommandLine cmd = parser.parse(option, userInput);
			} catch (IOException e) {
				System.out.println("Error when trying to read Input:");
				e.printStackTrace();
				
			}
		}
		
		
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
		
		
		// add a table for testing
		LinkedList<String> colNames = new LinkedList<String>(Arrays.asList("krank", "muede", "schmerzen"));
		LinkedList<String> colTypes = new LinkedList<String>(Arrays.asList("VARCHAR(60)", "INTEGER", "REAL"));
		boolean success = db.addTable("wrong", colNames, colTypes, "krank");
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
		
		
		
		
	}
}
