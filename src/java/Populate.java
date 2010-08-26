
import jvstm.Atomic;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.pstm.Transaction;

import benchmark.domain.AppRoot;
import benchmark.domain.IntContainer;

public class Populate {


    public static void main(String[] args) throws Exception {
	if (args.length != 1) {
	    System.err.println("Syntax:\n\njava Populate <n_items>");
	    System.exit(1);
	}

	int nItems = Integer.parseInt(args[0]);

	Configuration.initializeFenixFramework();
	populate(nItems);
    }

    @Atomic
    static void populate(int nItems) {
	AppRoot appRoot = FenixFramework.getRoot();
	for (int i = 0; i < nItems; i++) {
	    appRoot.addIntContainers(new IntContainer(0));
	}
    }
}
