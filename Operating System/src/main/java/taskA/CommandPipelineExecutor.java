package taskA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class CommandPipelineExecutor {
    public static List<String> executeCommand(List<String> input, String command) {
        String[] tokens = command.split("\\s+"); // Split command string into tokens

        try {
            Process process = Runtime.getRuntime().exec(tokens); // Execute the command 
            // If input is provided write it to the process's output 
            if (input != null) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                    for (String line : input) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.flush(); // Check that all input is written
                }
            }
            // Read process's output from its input 
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                List<String> output = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
                process.waitFor(); // Waits for process to complete
                return output;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + e.getMessage()); // Handles exception and prints an error message
            return null;
        }
    }
}
