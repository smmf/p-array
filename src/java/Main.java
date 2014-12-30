
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Random;

import jvstm.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.pstm.Transaction;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import benchmark.domain.AppRoot;
import benchmark.domain.IndirectionLevel;

public class Main {

    ProgramArgs progArgs;
    Random rand;

    /* The statistics array. This eliminates contention during tx execution, because statistics are collected independently */
    TxStatistics[] stats;
    long timerBegin, timerEnd;

    /* The data array */
    /*final*/ AppRoot appRoot;
    /* Thread pool */
    ExecutorService threadPool;

    @Atomic
    int getArraySize() {
	AppRoot appRoot = FenixFramework.getRoot();
	int count = 0;
	for (IndirectionLevel il : appRoot.getIndirectionLevelsSet()) {
	    if  (++count % 1000 == 0) {
		System.out.print(".");
	    }
	    il.getIntContainer();
	}
	System.out.println("");
	return appRoot.getIndirectionLevelsCount();
    }

    void processArgs(String[] args) {
	progArgs = new ProgramArgs(args, getArraySize());
    }

    Main(String[] args) {
	processArgs(args);
	// create final arrays
	stats = new TxStatistics[progArgs.nTx];
 	init();
    }
    
//     @Atomic
    void init() {
 	// Transaction.begin();
	// create Random instance
	rand = new Random();

	// create thread pool
	threadPool = Executors.newFixedThreadPool(progArgs.nThreads);
	// Transaction.commit();
    }

    // this ugly return type should be Set<TransactionTask>, but due to java 5 API of invokeAll we need to do like this
    private Set<Callable<Void>> initBenchmark() {
	int txNumber = 0;
	Set<Callable<Void>> txs = new HashSet<Callable<Void>>(progArgs.nTx);
	for (int i = 0; i < progArgs.nTxWOnly; i++) {
 	    txs.add(new ReadWriteTransactionTask(txNumber++, this, progArgs.tryReadOnly,
						 0,
						 numberWithDeviation(progArgs.txWOnlyNumOps, progArgs.txWOnlyNumOpsDevVal)));
	}
 	for (int i = 0; i < progArgs.nTxROnly; i++) {
 	    txs.add(new ReadWriteTransactionTask(txNumber++, this, progArgs.tryReadOnly,
						 numberWithDeviation(progArgs.txROnlyNumOps, progArgs.txROnlyNumOpsDevVal),
						 0));
 	    // txs.add(new ReadOnlyTransactionTask(txNumber++, this, progArgs.tryReadOnly,
	    // 					numberWithDeviation(progArgs.txROnlyNumOps, progArgs.txROnlyNumOpsDevVal)));
 	}
 	for (int i = 0; i < progArgs.nTxRW; i++) {
 	    txs.add(new ReadWriteTransactionTask(txNumber++, this, progArgs.tryReadOnly,
						 numberWithDeviation(progArgs.txRWNumReads, progArgs.txRWNumReadsDevVal),
						 numberWithDeviation(progArgs.txRWNumWrites, progArgs.txRWNumWritesDevVal)));
 	}

	return txs;
    }

    private void runBenchmarkWithThreads(Set<Callable<Void>> txs) {
// 	System.out.println("Running benchmark...");
	int nThreads = txs.size();
	Thread [] threads = new Thread[nThreads];

	System.out.println("Creating " + nThreads + " threads.");
	int pos = 0;
	Iterator txIterator = txs.iterator();
	while (txIterator.hasNext()) {
	    Runnable task = (Runnable)txIterator.next();
	    threads[pos++] = new Thread(task);
	}

	System.out.println("Launching " + nThreads + " threads.");
	timerBegin = System.currentTimeMillis();
	for (int i = 0; i < nThreads; i++) {
	    threads[i].start();
	}

	System.out.println("Joining " + nThreads + " threads.");
	for (int i = 0; i < nThreads; i++) {
	    try {
		threads[i].join();
	    } catch (InterruptedException ex) {
		System.out.println("Interrupted while waiting for thread " + i);
	    }
	}
	timerEnd = System.currentTimeMillis();
    }

    private void runBenchmarkWithPool(Set<Callable<Void>> txs) {
// 	System.out.println("Running benchmark...");
	timerBegin = System.currentTimeMillis();
	try {
	    threadPool.<Void>invokeAll(txs);
	} catch (InterruptedException ie) {
	    ie.printStackTrace();
	    System.err.println("BENCHMARK FAILED.");
	    System.exit(1);
	}
	timerEnd = System.currentTimeMillis();
    }

    void shutdown() {
	threadPool.shutdown();
    }
    
    // @Atomic
    // void dumpArray() {
    // 	if (progArgs.show) {
    // 	    StringBuilder str = new StringBuilder();

    // 	    str.append(array[0].get());
    // 	    for (int i = 1; i < array.length; i++) {
    // 		if (i % 50 == 0) {
    // 		    str.append("\n" + array[i].get());
    // 		} else {
    // 		    str.append("," + array[i].get());
    // 		}
    // 	    }
    // 	    System.out.println(str);
    // 	}
    // }

    
    int randomPositionInArray(Random rand) {
	return rand.nextInt(appRoot.getIndirectionLevelsCount());
    }

    // absDeviation must be less than or equal to center
    private int numberWithDeviation(int center, int absDeviation) {
	if (absDeviation > 0) {
	    return center + rand.nextInt(2 * absDeviation) - absDeviation;
	} else {
	    return center;
	}
    }

    FinalStats collectStats() {
	FinalStats fstats = new FinalStats();
	for (int i = 0; i < progArgs.nTx; i++) {
	    stats[i].updateFinalStats(fstats);
	}
	return fstats;
    }

    void printStats(FinalStats stats) {
	StringBuilder str = new StringBuilder();
	str.append("\nBenchmark took: " + (timerEnd - timerBegin) + " ms");
	str.append("\nTx statistics:");
	str.append("\n  read-only    => " + String.format("%10d", stats.nTxRO));
	str.append("\n   write-only  => " + String.format("%10d", stats.nTxWO));
	str.append("\n     restarts   : " + String.format("%10d", stats.woRestarts));
	str.append("\n   read-write  => " + String.format("%10d", stats.nTxRW));
	str.append("\n     restarts   : " + String.format("%10d", stats.rwRestarts));
	str.append("\n\n maxRestart   : " + String.format("%10d", stats.maxRestart));
	str.append("\n");
	System.out.println(str);
    }

    public static void main(String[] args) throws Exception {
	Configuration.initializeFenixFramework();

// 	System.out.println("Setting up...");
 	Main main = new Main(args);

	Set<Callable<Void>> init = main.initBenchmark();

  	main.runBenchmarkWithPool(init);
//   	main.runBenchmarkWithThreads(main.initBenchmark());

	main.shutdown();
  	main.printStats(main.collectStats());
	// jvstm.CommitStats.printAllStats();
	// main.dumpArray();
  }
}
