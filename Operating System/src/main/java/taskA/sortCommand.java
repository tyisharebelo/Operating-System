package taskA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class sortCommand implements command {
	// Method below sorts the contents of a file and prints it to console
	public void sortFile(String fileM) {
		List<String> lines = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileM))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line); // Stores the lines read from the file in a list
			}
		} catch (IOException e) {
			System.out.println ("Error reading file: " + e.getMessage()); // Handles I/O Exception
			return;
		}
		
		Collections.sort(lines); // Sort the list of lines
		for (String line : lines) {
			System.out.println(line); // Prints sorted lines onto console
			}
			
		}
	@Override
	public List<String> execute(List<String> input) throws IOException {
		// Create a new list to store the stored output
		List <String> output = new ArrayList<>(input);
		// Read lines from input and add to the output list
		try(BufferedReader br = new BufferedReader(new FileReader(input.get(0)))){
			String line;
			while((line = br.readLine()) != null) {
				output.add(line);
			}
		} catch (IOException e) {
			System.out.println("Error reading file: " + e.getMessage()); // Handles IOException
			throw e;
		}
		Collections.sort(output); // Sort the output list
		return output; // Return sorted output
	}
}
