package taskC;
// Represents an entry in the TLB
public class TLBEntry {
    boolean valid; // Checks if TLB entry is valid
    int tags; // Tag associated with TLB entry
    int physicalPageNo; // Physical page number corresponding to the tag
    int lru; // The LRU counter for the TLB entry
    
    // Constructor :
    public TLBEntry(boolean valid, int tags, int physicalPageNo, int lru) {
        this.valid = valid;
        this.tags = tags;
        this.physicalPageNo = physicalPageNo;
        this.lru = lru;
    }
    // Returns a string representation of the TLBEntry
    @Override
    public String toString() {
        return (valid ? 1 : 0) + "," + tags + "," + physicalPageNo + "," + lru;
    }
}
