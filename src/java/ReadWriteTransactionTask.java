import pt.ist.fenixframework.FenixFramework;

import benchmark.domain.AppRoot;
import benchmark.domain.IntContainer;

public class ReadWriteTransactionTask extends TransactionTask {
    
    int numReads, numWrites, totalOps;
    // int reads, writes;

    // how many reads left to do
    int readsToDo;
    // how many reads left to do
    int writesToDo;
    // how many ops left to do
    int opsToDo;



    ReadWriteTransactionTask(int txNumber, Main main, boolean tryReadOnly, int numReads, int numWrites) {
	super(txNumber, main, tryReadOnly);
	this.totalOps = numReads + numWrites;
	this.numReads = numReads;
	this.numWrites = numWrites;
    }

    @Override
    protected void doTx() {
	this.readsToDo = this.numReads;
	this.writesToDo = this.numWrites;
	this.opsToDo = this.readsToDo + this.writesToDo;

	AppRoot appRoot = FenixFramework.getRoot();
	int totalIterations = appRoot.getIntContainersCount();
	int iterationsRemaining = totalIterations;

	for (IntContainer intContainer : appRoot.getIntContainersSet()) {
	    // System.out.print("(" + totalIterations + " - " + iterationsRemaining + " = " + (totalIterations - iterationsRemaining) + ")");
	    // System.out.print("(" + (totalIterations - iterationsRemaining) + ")");
	    if (iterationsRemaining <= opsToDo) {
		//read-or-write only
		int op = this.rand.nextInt(opsToDo);
		if (op < readsToDo) {
		    read(intContainer);
		} else {
		    write(intContainer);
		}
	    } else { // here we may or may not do an operation
		// [0; numreads[                          => read
		// [numreads; numreads+numwrites [        => write
		// [numreads+numwrites; totalIterations [ => do nothing
		int op = this.rand.nextInt(totalIterations);
		if (op < this.numReads) {
		    maybeRead(intContainer);
		} else if (op < this.numReads + this.numWrites) {
		    maybeWrite(intContainer);
		} // else skip this position
	    }
	    iterationsRemaining--;
	    // System.out.println();
	}
    }

    protected void maybeRead(IntContainer ic) {
	if (this.readsToDo > 0) { read(ic); }
    }

    protected void maybeWrite(IntContainer ic) {
	if (this.writesToDo > 0) { write(ic); }
    }

    protected void read(IntContainer ic) {
	this.result = ic.getI();
	this.readsToDo--; this.opsToDo--;
	// System.out.print("\tR (R:" + this.readsToDo + ")(W:" + this.writesToDo + ")(T:" + this.opsToDo + ")");
    }

    protected void write(IntContainer ic) {
	// ic.getI();
	ic.setI(this.writesToDo);
	this.writesToDo--; this.opsToDo--;
	// System.out.print("\tW (R:" + this.readsToDo + ")(W:" + this.writesToDo + ")(T:" + this.opsToDo + ")");
    }

    @Override
    protected void updateFinalStats(FinalStats stats, int restarts) {
	stats.nTxRW++;
	stats.rwRestarts += restarts;
	if (restarts > stats.maxRestart) stats.maxRestart = restarts;
    }
}

