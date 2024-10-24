package taskA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TaskA {

    public static void main(String[] args) {

	System.out.println("Operating Systems Coursework");
	System.out.println("Name: Tyisha Rebelo");
	System.out.println("Please enter your commands - cat, cut, sort, uniq, wc or |");

	// read the command from the terminal
	// read the text file
	String filePath = "taskA.txt";
	Scanner scanner = new Scanner(System.in);

	while (true) {
		System.out.print(">> ");
		// Reads user's command input from console
		String command = scanner.nextLine().trim();
		if (command.equals("exit")) {
			break;
		}
		// Checking for pipeline commands
		if (command.contains("|")) { // Pipe character to split the commands based on
		    List<String> commands = Arrays.asList(command.split("\\|"));
		    List<String> input = null;
		    for (String cmd : commands) {
		        input = CommandPipelineExecutor.executeCommand(input, cmd.trim()); // Execute each command in the pipeline
		        if (input == null) {
		            break; // Exit loop if any command fails
		        }
		    }
		    if (input != null) {
		        input.forEach(System.out::println); // Print the final output
		    }
		} else {
			// Splitting commands into parts for individual command handling
			String[] parts = command.split("\\s+");
			String commandName = parts[0];
			switch (commandName) {
			case "cat":
			if (parts.length > 1) {
				String fileName = parts[1].trim();
				catCommand catCmd = new catCommand(fileName);
				try { // Execute cat command and print the output
					List<String> output = catCmd.execute(null);
					for (String line : output) {
						System.out.println(line);
					}
				} catch (IOException e) {
					System.err.println("Error executing cat command: " + e.getMessage());
				}
			} else {
				System.err.println("Usage: cat <filename>");
			}
			break;
			case "cut":
				String filename = "";
				List<Integer> fieldList = new ArrayList<>();
				for (int i = 1; i < parts.length; i++) {
					if (parts[i].equals("-f")) {
						// Field specifications for the cut command
						String[] fieldSpecs = parts[++i].split(",");
						for (String fieldSpec : fieldSpecs) {
							if (fieldSpec.contains("-")) {
								String[] range = fieldSpec.split("-");
								int start = Integer.parseInt(range[0]);
								int end = Integer.parseInt(range[1]);
								for (int h = start; h<= end; h++) {
									fieldList.add(h);
								}
							} else {
								fieldList.add(Integer.parseInt(fieldSpec));
							}
						}
					} else if (parts[i].equals("-d")) {
					} else {
						filename = parts[i];
					}
				}
				if (!filename.isEmpty() && !fieldList.isEmpty()) {
					int[] fields = fieldList.stream().mapToInt(Integer::intValue).toArray();
					cutCommand cutCmd = new cutCommand();
					cutCmd.cut(filename,  fields);
				} else {
					System.err.println("Usage: cut <filename> -f <field>[-<field>] -d <delimeter>");
				}
			break;
			case "sort":
			if (parts.length > 1) {
				String fileM = parts[1].trim(); // Retreive and trim filename 
				sortCommand sortCmd = new sortCommand();
				sortCmd.sortFile(fileM); // Sort the contents of the file using sort file method
			} else {
				System.err.println("Usage: sort <filename>"); // Error message if filename is not provided
			}
			break;
			case "uniq":
			if (parts.length > 1) {
				String fileB = parts[1].trim();
				uniqCommand uniqCmd = new uniqCommand();
				try {
					uniqCmd.uniq(fileB); // Calling uniq method to remove duplicate lines from this file
				} catch (IOException e) {
					System.err.println("Error executing uniq command: " + e.getMessage()); // Error message if exception occurs during execution
				}
			} else {
				System.err.println("Usage: uniq <filename>");
			}
			break;
			
			case "wc":
			boolean onlyLine = false;
			String fileH = null;
			
			if (parts.length > 1 && "-l".equals(parts[1])) { // Check if the filename is given after -l 
				onlyLine = true;
				if(parts.length > 2) {
					fileH = parts[2];
				} else {
					System.err.println("Usage: wc -l <filename>"); // Error message if filename is not provided
					continue;
				}
			} else if (parts.length > 1) {
				fileH = parts[1];
				} else {
					System.err.println("Usage: wc[-l] <filename>"); // Print error message if no arguments are provided
					continue;
				}
			wcCommand wcCmd = new wcCommand();
			try {
				wcCmd.wc(fileH, onlyLine); // Calling wc method counts words/lines in a specific file
			} catch (IOException e) {
				System.err.println("Error executing wc command: " + e.getMessage()); // Error message if exception occurs during execution
			}
			break;
			
		default:
				System.err.println("Unknown command: " + command); // Error message for unknown commands
			}
	}
	}
	scanner.close();
    }
}
