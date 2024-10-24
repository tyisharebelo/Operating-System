package taskC;

public class PageTableEntry {
    boolean valid; // Page in memory = valid and page is not in memory but on disk = invalid
    String physicalPageOrfromDisk; // Stores physical page number or disk location

   // Constructor :
    public PageTableEntry(boolean valid, String physicalPageOrfromDisk) {
        this.valid = valid;
        this.physicalPageOrfromDisk = physicalPageOrfromDisk;
    }
    // Return a string representation of PageTableEntry 
    @Override
    public String toString() {
        return (valid ? 1 : 0) + "," + physicalPageOrfromDisk;
    }
}
