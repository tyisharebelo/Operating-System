package taskA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class wcCommand {
	// Method below counts lines, words and bytes in a file
	public void wc(String fileH, boolean onlyLine) throws IOException {
						try(BufferedReader b = new BufferedReader(new FileReader(fileH))){
							int countLine = 0;
				
				String line; // Counts the number of lines in the file
				while ((line = b.readLine()) != null) {
					countLine++;
				}				
				// Output line if it is requested by calling -l
				if (onlyLine) {
					System.out.println(countLine);
				} else {
					int countWord = 0;
					int countByte = 0;
					// Count words and bytes if requested (without -l)
					try(BufferedReader d = new BufferedReader(new FileReader(fileH))){
						String line1;
						while ((line1 = d.readLine()) != null) {
							countWord += line1.split("\\s+").length;
							countByte += line1.getBytes().length + 1;
						}
					}
					System.out.println(countLine + " " + countWord + " " + countByte); // Print the number of lines, words and bytes in the console
				}
			}
	}
	
}
