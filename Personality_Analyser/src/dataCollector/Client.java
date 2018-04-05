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

/**
 * This Class communicates with the user by printing possible commands to the console
 * and calls other classes and functions according to the user input.
 * @author Neprox
 *
 */
public class Client {
	
	/** the Parser used to dissect the user's commands */
	DefaultParser parser;
	
	/** used to print possible Commands to the user */
	HelpFormatter formatter;
	
	/** Options (commands) that are available to the user */
	Options options;
	
	/** the BufferedReader uses to read from the console */
	BufferedReader in;
	
	/** the SQLite DB that is used for storing and retrieving data */
	SqliteDB db;
	/**
	 * 
	 */
	public Client() {
		// initialize Options for the parser
		OptionGroup mutualExclusiveOpts = new OptionGroup();
		Option tables = new Option("t", "tables", false, "list all currently stored tables and their Descriptions");
		Option insertInto = new Option("i", "insertInto", true, "add Entries to the specified table");
		Option createTable = new Option("c", "createTable", true, "create a new Table with the specified Name");
		Option help = new Option("h", "help", false, "list all possible Commands");
		createTable.setArgName("Tablename");
		insertInto.setArgName("Tablename");
		mutualExclusiveOpts.addOption(help);
		mutualExclusiveOpts.addOption(insertInto);
		mutualExclusiveOpts.addOption(createTable);
		mutualExclusiveOpts.addOption(tables);
		
		// initialize fields
		this.options = new Options();
		this.options.addOptionGroup(mutualExclusiveOpts);
		this.parser = new DefaultParser();
		this.formatter = new HelpFormatter();
		this.in = new BufferedReader(new InputStreamReader(System.in));
		this.db = new SqliteDB();
	}
	
	public void processInput() {
		System.out.println();
		System.out.println("Please choose what you want to do next. Enter \"-h\" to display possible commands.");
		
		try {
			// read input
			String input = in.readLine();
			
			// convert into array and parse
			String[] inputArr = input.split(" ");
			CommandLine cmds = parser.parse(options, inputArr);
			
			// process user's commands
			if(cmds.hasOption("help"))
				formatter.printHelp("You can pick one command (starting with \"-\" or \"--\") and need to specify the additional argument if needed", options);
			else if(cmds.hasOption("tables"))
				db.printUserTables();
			else if(cmds.hasOption("createTable"))
				createTable();
			else if(cmds.hasOption("insertInto")) 
				insertIntoTable();
			
				
			
		} catch (IOException e) {
			System.err.println("ERROR when trying to read input:");
			e.printStackTrace();
			this.processInput();
		} catch (ParseException e) {
			System.err.println("ERROR - could not parse the input. Reason:");
			System.err.println(e.getMessage());
			System.out.println();
			System.out.println("Please try again:");
			this.processInput();
		}
		this.processInput();
		
	}
	// TODO: implement Routine for creating a table from console
	private boolean createTable() {
		// TODO: Parser nebenbei auch auf diesem Input laufen lassen und schauen ob die cancel flag gesetzt ist und gegebenfalls abbrechen
		System.out.println("TODO: instructions");
		System.out.println("Enter \"--cancel\" or \"-c\" to select another option");
		System.out.println("createTable Method in Client not implemented");
		return false;
	}
	
	// TODO: implement Routine for inserting Data into a table from console
	private boolean insertIntoTable() {
		System.out.println("TODO: instructions");
		System.out.println("Enter \"--cancel\" or \"-c\" to select another option");
		System.out.println("Enter \"--help\" or \"-h\" to display the required format of your input");
		System.out.println("insertIntoTable Method in Client not implemented");
		System.out.flush();
		return false;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		System.out.println("Hello User!");
		Client cli = new Client();
		cli.processInput();
		
		
		/*
		
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
		*/
	}
}
