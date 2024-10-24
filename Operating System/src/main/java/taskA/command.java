package taskA;

import java.io.IOException;
import java.util.List;
// Execute a command with the given input to return the output
public interface command {
	List<String> execute (List<String> input) throws IOException; 
	// Throws IOException if I/O error occurs during execution
}
