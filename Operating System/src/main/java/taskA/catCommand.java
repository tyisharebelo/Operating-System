package taskA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class catCommand implements command{ // implements command interface
private final String fileN;
	// Constructor
	public catCommand(String fileN) {
		this.fileN = fileN;
	}
	// Method to print the content of the file onto the console
	public void cat(String fileN) {
	try (BufferedReader br = new BufferedReader (new FileReader(fileN))){
		String line;
		// Read each line and print it out to the console
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	} catch (IOException e) {
		// Handles IOExceptions
		System.err.println("Error readingfile: " + e.getMessage());
		}
	}
	
	@Override
	public List<String> execute(List<String> input) throws IOException { // Implements execute method from the command interface
		List<String> output = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileN))){ // BufferedReader to read the file line by line
			String line; // Adding each line read to the output
			while ((line = br.readLine()) != null) {
				output.add(line);
			}
		}
		return output; // Returning the list of lines read
	}
}
