import jvstm.TransactionalCommand;

import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.FenixFramework;
// import pt.ist.fenixframework.pstm.Transaction;
// import pt.ist.fenixframework.pstm.VersionNotAvailableException;
// import pt.ist.fenixframework.example.externalization.domain.DataStore;
// import pt.ist.fenixframework.example.externalization.domain.EnumType;

// import pt.ist.fenixframework.example.externalization.domain.*;

// import org.joda.time.DateTimeFieldType;
// import org.joda.time.DateTime;
// import org.joda.time.LocalDate;
// import org.joda.time.LocalTime;
// import org.joda.time.Partial;

// import java.lang.reflect.*;
// import java.util.TimeZone;
// import java.util.Iterator;
// import java.math.BigDecimal;
// import java.math.BigInteger;

// import dml.DomainClass;
// import dml.Slot;

public class Configuration {

    static void initializeFenixFramework() {
        Config configOJB = new Config() {{
            domainModelPath = "/p-array.dml";
            dbAlias = "//localhost:3306/test";
            dbUsername = "test";
            dbPassword = "test";
            rootClass = benchmark.domain.AppRoot.class;
	    /* uncomment the next line if you want the repository structure automatically updated when your domain definition
	       changes */
	    // updateRepositoryStructureIfNeeded = true;
        }};
        // Config configHBaseBDB = new Config() {{
        //     domainModelPath = "/p-array.dml";
        //     dbAlias = "/tmp/array-bdb";
        //     dbUsername = "";
        //     dbPassword = "";
        //     rootClass = benchmark.domain.AppRoot.class;
	//     repositoryType = pt.ist.fenixframework.Config.RepositoryType.BERKELEYDB;
	//     /* uncomment the next line if you want the repository structure automatically updated when your domain definition
	//        changes */
	//     // updateRepositoryStructureIfNeeded = true;
        // }};
        FenixFramework.initialize(configOJB);
    }
    
}
