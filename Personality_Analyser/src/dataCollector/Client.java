package dataCollector;

import sqlite.SqliteDB;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.cli.*;

import dataTypes.Pair;

/*
 * Flow: Init Database -> does data already exist -> ask what user wants to do, e.g. daily routine data insertion? add new table?
 * TODO: Aufrufen -> begruesst werden -> what do you want to do? -> Liste an Kommandos anzeigen
 * (--help IMMER als moeglichkeit)
 * TODO: Beim initialisieren der DB auch eine Tabelle mit Metadaten zu jeder Table anlegen, zB die Beschreibung, die der User angegeben hat
 * 		-> erweitere addTable um Parameter "Description"
 * TODO: UEBERLEG DIR EINE ALLGEMEIEN STRUKTUR WO WELCHE INTERAKTIVEN SACHEN GEMACHT WERDE UND WO WAS GEPRUEFT WIRD BEVOR DU IMPLEMENTIERST!!!!
 * TODO: Ueberall ueberlegen was scheif gehen koennte, z.B. division durch null / nullptr exception, etc...
 */

/**
 * This Class communicates with the user by printing commands to the console
 * and calling other classes and functions according to the user input.
 * @author Neprox
 *
 */
public class Client {
	
	/** the Parser used to dissect the user's commands */
	DefaultParser parser;
	
	/** used to print possible Commands to the user */
	HelpFormatter formatter;
	
	/** Options (commands) that are available to the user at the start, i.e. "home"*/
	Options homeOptions;
	
	/** Options usable when creating a Table */
	Options createTableOptions;
	
	/** Options usable when inserting Data into a Table */
	Options insertionOptions;
	
	/** the BufferedReader uses to read from the console */
	BufferedReader in;
	
	/** the SQLite DB that is used for storing and retrieving data */
	SqliteDB db;
	/**
	 * Constructor, that initializes all fields
	 */
	public Client() {
		initializeOpts();
		
		// initialize fields
		this.parser = new DefaultParser();
		this.formatter = new HelpFormatter();
		this.in = new BufferedReader(new InputStreamReader(System.in));
		this.db = new SqliteDB();
	}
	
	/**
	 * reads input from the console with the given options and returns a CommandLine Object, that can be used to retrieve the options.
	 * Returns <strong>null</strong> if there was an error.
	 * @param options the options, that should be parsed.
	 * @return a CommandLine Object. null if there is an exception.
	 */
	private CommandLine readInput(Options options) {
		try {
			// read input
			String input = in.readLine();
			
			// convert input into array and parse
			String[] inputArr = input.split(" ");
			CommandLine cmds = parser.parse(options, inputArr);
			
			return cmds;
		} catch (ParseException e) {
			System.err.println("ERROR - could not parse the input. Reason:");
			System.err.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.err.println("ERROR when trying to read input:");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Main function of Client. Reads and processes user input in an endless loop until user wants to exit
	 */
	public void processInput() {
		while(true) {
			System.out.println();
			System.out.println("Please choose what you want to do next. Enter \"-h\" to display all available commands.");

			CommandLine cmds = readInput(homeOptions);
			if(cmds==null) {
				// if there was an exception when reading the Input -> try again
				continue;
			}
			// list available commands
			if(cmds.hasOption("help"))
				formatter.printHelp("You can pick one command (starting with \"-\" or \"--\") and need to specify the additional argument if needed", homeOptions);
			// print user tables
			else if(cmds.hasOption("tables"))
				db.printUserTables();
			// create new table
			else if(cmds.hasOption("createTable")) {
				String tableName = cmds.getOptionValue("createTable");
				if(db.exists(tableName)) {
					System.out.println("Table with name \"" + tableName + "\" already exists");
					continue;
				}
				createTable(tableName);
			}
			// insert data
			else if(cmds.hasOption("insertInto")) {
				String tableName = cmds.getOptionValue("insertInto");
				if(!db.exists(tableName)) {
					System.out.println("Table with name \"" + tableName + "\" does not exist");
					continue;
				}
				insertIntoTable(tableName);
			}
			// exit program
			else if(cmds.hasOption("exit"))
				System.exit(0);
			else
				System.out.println("No Command recognized, please try again");
		}
		
	}
	/**
	 * reads input from the user in order to create a new table and insert it into the database
	 * @param tableName the name of the table that should be created
	 * @return true if table was created and inserted into database, false otherwise
	 */
	private boolean createTable(String tableName) {
		System.out.println("");
		System.out.println("Please enter the Definition of each column one by one. Enter -h for more details and/or help");
		
		// Stores Pairs of <ColumnName, Datatype>
		LinkedList<Pair<String,String>> columns = new LinkedList<>();
		// Stores Columnnames
		LinkedList<String> primaries = new LinkedList<>();
		
		while(true) {
			// read and process input
			CommandLine cmds = readInput(createTableOptions);
			if(cmds==null) {
				// if there was an exception when reading the Input -> try again
				continue;
			}
			// print available commands
			if(cmds.hasOption("help"))
				formatter.printHelp("Enter one of the following commands and the additional arguments if needed", createTableOptions);
			// return to home menu
			else if(cmds.hasOption("cancel")) {
				System.out.println("Returning to home menu");
				return false;
			}
			// Invoke SQL CREATE TABLE Query
			else if(cmds.hasOption("done")) {
				if(primaries.size()==0) {
					// TODO: create default_ID as Primary and store counter in table for metadata
				}
				if(columns.size() != 0) {
					// TODO: Add table + Metadata
				}
				else
					System.out.println("You need to specify a column, before finishing.");
				
			}
			// add a Column
			else if(cmds.hasOption("add")){
				String[] input = (cmds.getOptionValues("add"));
				
				// check whether there are enough arguments
				if(input.length != 2) {
					System.out.println("Wrong number of arguments: " + input.length + ", required number of arguments: 2");
					continue;
				}
				// give names for better readability
				String name = input[0];
				String type = input[1];
				// give Feedback to the user
				System.out.println("You have entered Column_Name = " + name + ", Type = " + type);
				
				// check whether Name of the column already exists
				for(int i=0; i<columns.size(); i++)
					if(columns.get(i).getKey().equals(name)) {
						System.out.println("Error: Column with name \"" + name + "\" has already been added!");
						continue;
					}
				// check whether the right type was specified
				if(!(type.equals("integer") || type.equals("real") || type.equals("text"))){
					System.out.println("Wrong Type: \"" + type + "\", must be one of the following: \"integer\", \"real\", \"text\" ");
					continue;
				}
				
				// convert input types to SQL types and add column to the list of columns
				type = type.equals("text") ? "VARCHAR(50)" : type.equals("integer")? "INTEGER" : "REAL";
				Pair<String, String> column = new Pair<>(name, type);
				columns.add(column);
			}
			
			// add an already existing column to the primary
			else if(cmds.hasOption("add_Primary")) {
				String primary = cmds.getOptionValue("add_Primary");
				// check whether user has already entered the corresponding column -> find column with columnName == primary
				boolean defined = false;
				for(int i=0; i<columns.size(); i++)
					if(columns.get(i).getKey().equals(primary))
						defined = true;
				if(!defined) {
					System.out.println("Column " + primary + " has to be defined first!");
					System.out.println("try: --add " + primary + " < + " + primary + "_type>");
					continue;
				}
				primaries.add(primary);
				System.out.println("\"" + primary + "\" was added as Primary");
			}
			else if(cmds.hasOption("display_Columns")) {
				System.out.println("You have entered the following columns:"); // Table layout on console!
				String leftAlignFormat = "| %-20s | %-11s | %-7s |%n";

				System.out.format("+----------------------+-------------+---------+%n");
				System.out.format("| Column name          | Type        | Primary +%n");
				System.out.format("+----------------------+-------------+---------+%n");
				for (int i = 0; i < columns.size(); i++) {
				    System.out.format(leftAlignFormat, columns.get(i).getKey(), columns.get(i).getValue(), primaries.contains(columns.get(i).getKey()) ? "yes" : "no");
				}
				System.out.format("+----------------------+-------------+---------+%n");
			}
			else
				System.out.println("No Command recognized, please try again");
		}
		
	}
	
	// TODO: implement Routine for inserting Data into a table from console
	private boolean insertIntoTable(String tableName) {
		System.out.println("TODO: instructions");
		System.out.println("Enter \"--cancel\" or \"-cc\" to select another option");
		System.out.println("Enter \"--help\" or \"-h\" to display the required format of your input");
		System.out.println("insertIntoTable Method in Client not implemented");
		System.out.flush();
		
		while(true) {
		// read and process input
		CommandLine cmds = readInput(insertionOptions);
		if(cmds==null) {
			// if there was an exception when reading the Input -> try again
			continue;
		}
		// TODO: Process options
		//else
		//	System.out.println("No Command recognized, please try again");
		}
		
		// TODO: if default ID is created for a table -> do not ask for value here, but create it automatically instead
		// keep an entry for that in the Metadata table
	}
	
	private void initializeOpts() {
		// initialize Options used at several different places
		Option help = new Option("h", "help", false, "list all available Commands");
		Option done = new Option("d", "done", false, "signals that you do not want to enter any further Data");
		
		// initialize Options for "home"
		OptionGroup grpHome = new OptionGroup();
		Option tables = new Option("t", "tables", false, "list all currently stored tables and their Descriptions");
		Option insertInto = new Option("i", "insertInto", true, "add Entries to the specified table");
		Option createTable = new Option("c", "createTable", true, "create a new Table with the specified Name");
		Option exit = new Option("e", "exit", false, "exits the program");
		createTable.setArgName("Tablename");
		insertInto.setArgName("Tablename");
		grpHome.addOption(help);
		grpHome.addOption(insertInto);
		grpHome.addOption(createTable);
		grpHome.addOption(tables);
		grpHome.addOption(exit);
		this.homeOptions = new Options();
		this.homeOptions.addOptionGroup(grpHome);
		
		// initialize Options for "createTable"
		OptionGroup grpCT = new OptionGroup();
		Option cancel = new Option("cc", "cancel", false, "abort the current task and choose another one");
		Option addPrim = new Option("aP", "add_Primary", true, "It is recommended that at least one of the (already entered) column names has to be specified here, " + 
										"multiple ones can be specified though. The primary key is used to uniquely distinguish each entry from others. " +
										"If you want to store data per day, the attribute \"day\" would be unique for every entry and would therefore be the primary " + 
										"If no value is specified, a numeric ID is automatically created for each entry");
		Option addedCols = new Option("dC", "display_Columns", false, "displays all Column definitions that you have entered already. Columns with type \"text\" " + 
										"internally have the type \"VARCHAR(50)\", so there is no problem, if you see this");
		Option addCol = new Option("a", "add", true, "adds a column with the given Name and Type. Type can either be \"integer\", \"real\" or \"text\"");
		addCol.setArgs(2);
		addCol.setArgName("Column_Name Type");
		addPrim.setArgName("Name");
		grpCT.addOption(addPrim);
		grpCT.addOption(addedCols);
		grpCT.addOption(cancel);
		grpCT.addOption(addCol);
		grpCT.addOption(help);
		grpCT.addOption(done);
		this.createTableOptions = new Options();
		this.createTableOptions.addOptionGroup(grpCT);
		
		// initialize Options for "insertInto"
		OptionGroup grpInsert = new OptionGroup();
		grpInsert.addOption(help);
		grpInsert.addOption(done);
		this.insertionOptions = new Options();
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		System.out.println("Hello User!");
		Client cli = new Client();
		cli.processInput();
		
		
		/*
		
		// add a table for testing
		LinkedList<String> colNames = new LinkedList<String>(Arrays.asList("krank", "muede", "schmerzen"));
		LinkedList<String> colTypes = new LinkedList<String>(Arrays.asList("VARCHAR(50)", "INTEGER", "REAL"));
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
