package taskA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class cutCommand {
	public void cut(String fileName, int[] fields) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
			String line; // Read each line from the file
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(","); // Use comma as delimiter to split line
				StringBuilder output = new StringBuilder(); // Stores the output line after cutting feilds
				for (int field : fields) { // Iterate through the specified fields
					if (field >= 1 && field <= parts.length) {
						// Append the field value to the output with comma separating it
						output.append(parts[field - 1]).append(",");
					} else {
						output.append(","); // Appends an empty field if index is out of bounds
					}
				}
				System.out.println(output.deleteCharAt(output.length() - 1).toString()); // Remove the trailing comma and print the output line
			}
		} catch (IOException e) {
			System.err.println("Error reading file: " +e.getMessage()); // Handles I/O Exception 
		}
	}
   
}
