package taskB;

/**
 * This class main purpose is to be a linked list for the current blocks of
 * memory that are placed or free for the simulation of First Fit, Best Fit, and
 * Worst Fit memory allocation methods.
 */
public class MainMemory {

    private BlockNode start;
    private BlockNode end;
    private int size;

    /**
     * Constructor, initialize linked list
     */
    public MainMemory() {
	start = null;
	end = null;
	size = 0;
    }

    /**
     * Checks if linked list is empty
     * 
     * @return True if empty, false if not
     */
    public boolean isEmpty() {
	return start == null;
    }

    /**
     * Gets the size of linked list
     * 
     * @return size of linked list
     */
    public int getSize() {
	return size;
    }

    /**
     * Inserts Block at start of linked list, best to be used to initialize first
     * node.
     * 
     * @param block Block of memory to insert.
     */
    public void insertAtStart(Block block) {
	BlockNode nptr = new BlockNode(block, null);
	size++;
	if (start == null) {
	    start = nptr;
	    end = start;
	} else {
	    nptr.setNext(start);
	    start = nptr;
	}
    }

    /**
     * First fit insert, this method goes through the linked list finding the first
     * place it can insert the block into memory.
     * 
     * @param the Process proc to insert into memory
     * @return True if successfully inserted block of memory, False if failed.
     */
    public boolean firstFitInsert(Process proc) {
	Block block = new Block(proc);
	BlockNode nptr = new BlockNode(block, null);

	if (start == null) {
	    start = nptr;
	    end = start;
	    return true;
	} else {

	    BlockNode curr = start;

	    // look at all available slots/holes in memory
	    // select the first available position of suitable size for block
	    while (curr != null) {

		// enough available space in memory identified
		if (curr.getBlock().canPlace(block.getProcess())) {

		    // get the end memory location for available block curr
		    int end = curr.getBlock().getHole().getEnd();

		    // add the process in memory
		    curr.getBlock().setProcess(block.getProcess());

		    // take only what we need from memory
		    int block_start = curr.getBlock().getHole().getStart();
		    int memory_needs = block.getProcess().getArgument();
		    curr.getBlock().getHole().setRange(block_start, block_start + memory_needs - 1);

		    // create a new block with the rest of memory we don't need
		    // notice curr.getBlock().getHole().getEnd() was changed by line 155
		    if (curr.getBlock().getHole().getEnd() < end) {
			BlockNode newBlock = new BlockNode(
				new Block(null, new Hole(curr.getBlock().getHole().getEnd() + 1, end)), curr.getNext());

			curr.setNext(newBlock);
		    }
		    size++;
		    return true;
		}
		curr = curr.getNext();
	    }
	    return false;
	}
    }

    /**
     * TODO Best fit insert, this method goes through the linked list finding the
     * best place it can insert the block into memory.
     * 
     * @param Process proc to insert into memory
     * @return True if successfully placed, false if it failed.
     */
    public boolean bestFitInsert(Process proc) {
	BlockNode ptr = start;
	BlockNode bestFitNode = null;
	// Traverse the linked list to find the best fit block
	while (ptr != null) {
		Block currentBlock = ptr.getBlock();
		// Check if the current block is available to fit the process
		if (currentBlock.available() && currentBlock.getSize() >= proc.getArgument()) {
			// Update best fit node if it is null/current block is a better fit
			if (bestFitNode == null || currentBlock.getSize() < bestFitNode.getBlock().getSize()) {
				bestFitNode = ptr;
			}
		}
		ptr = ptr.getNext(); // Move to the next block
	}
	// If no block is found return 'false'
	if (bestFitNode == null) {
		return false;
	}
	// Get start, required size and the end of the best fit block
	int blockStart = bestFitNode.getBlock().getHole().getStart();
	int requiredSize = proc.getArgument();
	int blockEnd = bestFitNode.getBlock().getHole().getEnd();
	
	// Allocate the process to the best fit block
	bestFitNode.getBlock().setProcess(proc);
	bestFitNode.getBlock().getHole().getEnd();
	
	// Set the new range after allocating the process
	bestFitNode.getBlock().getHole().setRange(blockStart, blockStart + requiredSize - 1);
	
	// If there is remaining space in the block, create a new block for the remaining hole
	if (blockEnd > bestFitNode.getBlock().getHole().getEnd()) {
		BlockNode newBlockNode = new BlockNode(
				new Block(null, new Hole (bestFitNode.getBlock().getHole().getEnd() + 1, blockEnd)),
				bestFitNode.getNext());
		bestFitNode.setNext(newBlockNode); // Link the new block node to the list
	}
	size++; // Increment the size of the list
	return true; // Show successful allocation
    }

    /**
     * This method goes through current memory blocks. If blocks are next to each
     * other and free it will join the blocks together making a larger block.
     */

    public void joinBlocks() {
        BlockNode ptr = start;
        while (ptr != null && ptr.getNext() != null) {
            BlockNode next = ptr.getNext();
            if (ptr.getBlock().getProcess() == null && next.getBlock().getProcess() == null) {
                int start = ptr.getBlock().getHole().getStart();
                int end = next.getBlock().getHole().getEnd();
                ptr.getBlock().getHole().setRange(start, end);
                ptr.setNext(next.getNext());
                size--;
                continue;
            }
            ptr = ptr.getNext();
        }
    }

    /**
     * TODO This method gets the external fragmentation of the current memory blocks
     * if a block of memory failed to placed.
     * 
     * @return external fragmentation of memory.
     */
    public int externalFragmentation() {
    	BlockNode ptr = start; // Start from the beginning of the block list
    	int totalFragmentation = 0; // total fragmentation counter initialised
	
    	// Traverse the list of blocks
    	while (ptr != null) {
    		// If the block is available then add its size to the total fragmentation
    		if (ptr.getBlock().available()) {
    			totalFragmentation += ptr.getBlock().getSize();
		}
		ptr = ptr.getNext(); // Move to the next block
	}
	return totalFragmentation; // Return total fragmentation
    }

    /**
     * This method goes through the blocks of memory and de-allocates the block for
     * the provided process_number
     * 
     * @param process_number Process to be de-allocated.
     */
    public void deallocateAndMerge(int process_number) {
        BlockNode ptr = start; //  Start from the beginning of the block list
        while (ptr != null) {
        	// Check if the current block has been allocated
            if (ptr.getBlock().getProcess() != null) {
            	// Check if process matches given process_number
                if (ptr.getBlock().getProcess().getReference_number() == process_number) {
                    ptr.getBlock().setProcess(null); // De-allocate the block
                    joinBlocks(); // Merge adjacent free blocks after deallocation
                    return; // Exit
                }
            }
            ptr = ptr.getNext(); // Move to the next block
        }
    }

    public void compact() {
    	BlockNode ptr = start; //  Start from the beginning of the block list
    	while (ptr != null && ptr.getNext() != null) {
    		// If the current block is available and the next block is unavailable
    		if (ptr.getBlock().available() && !ptr.getNext().getBlock().available()) {
    		BlockNode blockToMove = ptr.getNext(); // The block to move is the next block
    		Process tempProcess = blockToMove.getBlock().getProcess();
    		int processNumber = 0;
    		// Get the process in the next block and determine the process number based on the allocation status
    		if(tempProcess.isAllocating()) {
    			processNumber = tempProcess.getReference_number();
    		} else {
    			processNumber = tempProcess.getArgument();
    		}
    		// Deallocate and merge the process block
    		deallocateAndMerge(processNumber);
    		joinBlocks(); // Merge adjacent free blocks after deallocation
    		firstFitInsert(tempProcess); // Use the first-fit strategy to reinsert the process
    	}
    	ptr = ptr.getNext(); // Move to next block
    	}
    }


    /**
     * This method prints the whole list of current memory.
     */
    public void printBlocks() {
	System.out.println("Current memory display");
	BlockNode ptr = start;
	while (ptr != null) {
	    ptr.getBlock().displayBlock();
	    ptr = ptr.getNext();
	}
    }
    
    

}
