package taskA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class uniqCommand implements command {
	// Method below removes consecutive duplicate lines from a file and prints the output to the console
	public void uniq(String fileB) throws IOException{
		try (BufferedReader reader = new BufferedReader(new FileReader(fileB))){
			String prevLine = null;
			String line;
		// Iterate through each line of this file
		while((line = reader.readLine()) != null) {
			if (!line.equals(prevLine)) {
				System.out.println(line); // Print unique line
				prevLine = line;
			} else {
				System.out.println(line); // Print duplicate line
			}
		}
	}
}
	@Override
	public List<String> execute(List<String> input) throws IOException {
		Set<String> seen = new LinkedHashSet<>(); // Preserves the order of unique lines
		for (String line : input) {
			seen.add(line); // Adds each line to the set
		}
		// Converts the set to a list and returns it
		return new ArrayList<>(seen);
	}
}
