package taskC;

import java.io.*;
import java.util.*;

public class TaskC {
    private static final int pageSize = 4096;  // 4 KB pages
    private static final int TLBSize = 4;      // Maximum 4 TLB entries

    public static void main(String[] args) {
        try {
            System.out.println("Starting Memory Management ...");

            // Input and output files
            String fileInput = "taskC.txt";
            String fileOutput = "taskc-sampleoutput.txt";

            // Read the input file
            List<String> address = new ArrayList<>();
            List<TLBEntry> tlb = new ArrayList<>(TLBSize);
            List<PageTableEntry> pageTable = new ArrayList<>();

            readInput(fileInput, address, tlb, pageTable);

            // Process each address and gather output content
            List<String> outputContent = processAddress(address, tlb, pageTable);

            // Write the output file
            writeOutputFile(fileOutput, outputContent);

            System.out.println("Completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Reads the file input and populates address list, TLB and Page Table 
    private static void readInput(String fileInput, List<String> address, List<TLBEntry> tlb, List<PageTableEntry> pageTable) {
        //Ensure the buffered reader is closed automatically 
    	try (BufferedReader br = new BufferedReader(new FileReader(fileInput))) {
            String line;
            boolean readingAddress = false;
            boolean readingTLB = false;
            boolean readingPageTable = false;
            // Read each line from the input file
            while ((line = br.readLine()) != null) {
                line = line.trim(); // Remove trailing whitespace
                if (line.startsWith("#")) {
                    // Figure out which section is being read
                    if (line.contains("Address")) {
                        readingAddress = true;
                        readingTLB = false;
                        readingPageTable = false;
                    } else if (line.contains("Initial TLB")) {
                        readingAddress = false;
                        readingTLB = true;
                        readingPageTable = false;
                    } else if (line.contains("Initial Page table")) {
                        readingAddress = false;
                        readingTLB = false;
                        readingPageTable = true;
                    }
                    continue; // Skip to the next line after updating the sectiom
                }

                if (readingAddress) {
                    address.add(line);
                } else if (readingTLB) {
                	// Split the line by commas and create a new TLB entry
                    String[] divide = line.split(",");
                    tlb.add(new TLBEntry(Integer.parseInt(divide[0]) == 1, Integer.parseInt(divide[1]), Integer.parseInt(divide[2]), Integer.parseInt(divide[3])));
                } else if (readingPageTable) {
                	// Split the line by commas and create a new Page Table entry
                    String[] divide = line.split(",");
                    pageTable.add(new PageTableEntry(Integer.parseInt(divide[1]) == 1, divide[2]));
                }
            }

            System.out.println("Finished reading input file."); // Registering that the input file has been read successfully
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if IOException occurs
        }
    }
    // Processes each address to stimulate memory access
    private static List<String> processAddress(List<String> address, List<TLBEntry> tlb, List<PageTableEntry> pageTable) {
        List<String> outputResult = new ArrayList<>(); // List to store the output results
        System.out.println("Address Result (Hit, Miss, Page Fault)");
        // Iterate through each address in the list
        for (String addressHD : address) {
            int addr = Integer.parseInt(addressHD.substring(2), 16);  // Convert hexadecimal address to a decimal
            int pageNo = addr / pageSize;  // Get the page number by dividing by the page size
            // Process the address and get result
            String result = processOneAddress(pageNo, tlb, pageTable);
            System.out.println(addressHD + "\n" + result);

            // Add result to output content
            outputResult.add("# After the memory access " + addressHD);
            outputResult.add("#Address, Result (Hit, Miss, PageFault)");
            outputResult.add(addressHD + "," + result);

            // Add updated TLB to output content
            outputResult.add("#updated TLB");
            outputResult.add("#Valid, Tag, Physical Page #, LRU");
            for (TLBEntry e : tlb) {
                outputResult.add(e.toString());
            }

            // Add updated Page Table to putput content
            outputResult.add("#updated Page table");
            outputResult.add("#Index,Valid,Physical Page or On Disk");
            for (int i = 0; i < pageTable.size(); i++) {
                outputResult.add(i + "," + pageTable.get(i).toString());
            }
        }

        return outputResult; // Return the list of output results
    }
    // Process a single address to determine the result ("Hit", "Miss", "Page Fault")
    private static String processOneAddress(int pageNo, List<TLBEntry> tlb, List<PageTableEntry> pageTable) {
        boolean hit = false;
        boolean pageFault = false;

        // Check if the page is in the TLB
        for (TLBEntry e : tlb) {
            if (e.valid && e.tags == pageNo) {
                hit = true;
                updateLRU(tlb, e); // Update LRU after this entry
                break;
            }
        }

        if (!hit) {
            // Check the page table when page is not found in TLB
            PageTableEntry ptEntry = pageTable.get(pageNo);
            if (ptEntry.valid) {
                // TLB miss but page is in memory
                updateTLB(tlb, pageNo, Integer.parseInt(ptEntry.physicalPageOrfromDisk));
                return "Miss";
            } else {
                // Page fault: page is not in memory, bring it from disk
                pageFault = true;
                int newPhysicalPage = getNewPhysicalPageNo(pageTable);
                ptEntry.valid = true;
                ptEntry.physicalPageOrfromDisk = String.valueOf(newPhysicalPage);
                updateTLB(tlb, pageNo, newPhysicalPage);
            }
        }

        return hit ? "Hit" : (pageFault ? "Page fault" : "Miss");
    }
    // Updates LRU for TLB entries
    private static void updateLRU(List<TLBEntry> tlb, TLBEntry entryUsed) {
        int indexUsed = tlb.indexOf(entryUsed);
        for (int i = tlb.size() - 1; i >= 0; i--) {
            TLBEntry entry = tlb.get(i);
            if (entry.valid && entry != entryUsed) {
                if (entry.lru >= entryUsed.lru) {
                    entry.lru = Math.max(1, entry.lru - 1); // Decrement the LRU values of entries not just used
                }
            }
        }
        entryUsed.lru = 4; // Set the LRU value of the most recently used address to 4 (highest value)
    }



    // Updates the TLB with new entry/replaces the least recently used entry
    private static void updateTLB(List<TLBEntry> tlb, int pageNo, int physicalPageNo) {
        TLBEntry LRUEntry = null;

        for (TLBEntry e : tlb) {
            if (!e.valid) {
                LRUEntry = e; // Find an invalid entry
                break;
            }
            if (LRUEntry == null || e.lru < LRUEntry.lru) {
                LRUEntry = e; // Find the least recently used entry
            }
        }

        if (LRUEntry != null) {
            LRUEntry.valid = true;
            LRUEntry.tags = pageNo;
            LRUEntry.physicalPageNo = physicalPageNo;
            updateLRU(tlb, LRUEntry); // Update the LRU information after replacing the least recently used with invalid entry
        }
    }
    // Finds a new physical page number that is not used up in the page table
    private static int getNewPhysicalPageNo(List<PageTableEntry> pageTable) {
        int maxPageNo = 0;
    	for (PageTableEntry entry : pageTable) {
            if (entry.valid) {
            	int currentPageNo = Integer.parseInt(entry.physicalPageOrfromDisk);
                if (currentPageNo > maxPageNo) {
                    maxPageNo = currentPageNo;
                }
            }
        }
        int newPageNo = maxPageNo + 1; // Allocates from one above the maximum page number
        return newPageNo;
    }
    // Writes the output content to the specified output file
    private static void writeOutputFile(String outputFile, List<String> outputResult) {
        try (BufferedWriter write = new BufferedWriter(new FileWriter(outputFile))) {
            for (String line : outputResult) {
                write.write(line); // Writes each line to the output file
                write.newLine();
            }
            System.out.println("Output written to file: " + outputFile); // Record that the output has been written
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace if IOException occurs
        }
    }
}
