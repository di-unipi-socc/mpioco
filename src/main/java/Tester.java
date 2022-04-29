import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import composite.CompositeApplication;
import iolts.IOLTS;
import mp.*;
import mpioco.Mpioco;
import util.ToscaUtil;


public class Tester {
	
	public static void main( String[] args ) throws Exception
    {	
        // Parsing CLI options (beta, gamma)
        CommandLine commandLine;
        Options options = new Options();
        Option optCompositeTest = Option.builder("c").longOpt("composite").build();
        options.addOption(optCompositeTest);
        Option optBeta = Option.builder("b").longOpt("beta").hasArg().build();
        options.addOption(optBeta);
        Option optGamma = Option.builder("g").longOpt("gamma").hasArg().build();
        options.addOption(optGamma);
        CommandLineParser parser = new DefaultParser();
        commandLine = parser.parse(options, args);
        
        // Setting test type (composite if specified, single by default)
        boolean compositeTest = false;
        if (commandLine.hasOption("c"))
            compositeTest = true;
        // Setting value of beta (specified, or relaxed - by default)
        boolean beta = false;
        if (commandLine.hasOption("b")) {
            if (commandLine.getOptionValue("b").equalsIgnoreCase("strict")) 
            beta = true;
            else if (!commandLine.getOptionValue("b").equalsIgnoreCase("relaxed"))
            throw new Exception("unknown value for beta");
        }
        // Setting value of gamma (specified, or relaxed - by default)
        boolean gamma = false;
        if (commandLine.hasOption("g")) {
            if (commandLine.getOptionValue("g").equalsIgnoreCase("strict")) 
                gamma = true;
            else if (!commandLine.getOptionValue("g").equalsIgnoreCase("relaxed"))
                throw new Exception("unknown value for gamma");
        }

        // Parsing arguments
        String[] paths = commandLine.getArgs();
        if (paths.length != 2)
            throw new Exception("wrong amount of arguments");
        String specPath = paths[0];
        String implPath = paths[1];
		boolean result = checkComposability(compositeTest,specPath,implPath,gamma,beta);
        if (result)
            System.out.println("Conformance holds");
        else 
            System.out.println("Conformance does not hold");
    }
	
	public static boolean checkComposability(boolean composite, String spec, String impl, boolean gamma, boolean beta) {
		boolean result;

        if (composite) {
            System.out.println("-----Composed Test-----");
            
            long startTime = System.nanoTime();
            
            CompositeApplication ca = new CompositeApplication(spec);		
            CompositeApplication ca2 = new CompositeApplication(impl);
            
            IOLTS iolts = new IOLTS(ca);
            IOLTS iolts2 = new IOLTS(ca2);
            
            Mpioco mpioco = new Mpioco(iolts, iolts2, gamma, beta);
            result = mpioco.checkConformance();
            
            long estimatedTime = System.nanoTime() - startTime;
            double seconds = (double)estimatedTime / 1_000_000_000.0;
            System.out.println("Time needed for composed test: " + seconds + " seconds");
        } else {
            System.out.println("-----Management Protocol Test-----");
            
            long startTime = System.nanoTime();
            
            ManagementProtocol mp = ToscaUtil.fetchToMp(spec);
            ManagementProtocol mp2 = ToscaUtil.fetchToMp(impl);
            
            IOLTS iolts = new IOLTS(mp);
            IOLTS iolts2 = new IOLTS(mp2);
            
            Mpioco mpioco = new Mpioco(iolts, iolts2, gamma, beta);
            result = mpioco.checkConformance();
            
            long estimatedTime = System.nanoTime() - startTime;
            double seconds = (double)estimatedTime / 1_000_000_000.0;
            System.out.println("Time needed for management protocol test: " + seconds + " seconds");
        }
        return result;
	}
	
	// public static void runningExample() {
	// 	// first management protocol
		
	// 	List<State> states = new ArrayList<State>();
    //     State unavail = new State("unavail");
    //     State installed = new State("installed");
    //     State started = new State("started");
    //     State failed = new State("failed");
    //     states.add(unavail);
    //     states.add(installed);
    //     states.add(started);
    //     states.add(failed);
        
    //     List<Requirement> requirements = new ArrayList<Requirement>();
    //     Requirement db = new Requirement("db");
    //     requirements.add(db);
        
    //     List<Capability> capabilities = new ArrayList<Capability>();
    //     Capability endp = new Capability("endp");
    //     capabilities.add(endp);
        
    //     List<Operation> operations = new ArrayList<Operation>();
    //     Operation install = new Operation("install");
    //     Operation uninstall = new Operation("uninstall");
    //     Operation configure = new Operation("configure");
    //     Operation start = new Operation("start");
    //     Operation stop = new Operation("stop");
    //     operations.add(install);
    //     operations.add(uninstall);
    //     operations.add(configure);
    //     operations.add(start);
    //     operations.add(stop);
        
    //     List<FaultHandling> fh = new ArrayList<FaultHandling>();
    //     FaultHandling started_f = new FaultHandling(started, failed);
    //     FaultHandling installed_f = new FaultHandling(installed, failed);
    //     fh.add(started_f);
    //     fh.add(installed_f);
        
    //     List<Requirement> empty_r = new ArrayList<Requirement>();
    //     List<Capability> empty_c = new ArrayList<Capability>();
        
    //     installed.addRequirement(db);
    //     started.addRequirement(db);
    //     started.addCapability(endp);

    //     ManagementProtocol mp = new ManagementProtocol(states, requirements, capabilities, operations, unavail, fh);
    //     mp.addTransition(install, unavail, installed, empty_r, empty_c);
    //     mp.addTransition(uninstall, installed, unavail, empty_r, empty_c);
    //     mp.addTransition(configure, installed, installed, requirements, empty_c);
    //     mp.addTransition(start, installed, started, empty_r, empty_c);
    //     mp.addTransition(stop, started, installed, empty_r, empty_c);
        
    //     // second management protocol
        
	// 	List<State> states2 = new ArrayList<State>();
    //     State unavail2 = new State("unavail");
    //     State installed2 = new State("installed");
    //     State started2 = new State("started");
    //     State failed2 = new State("failed");
    //     states2.add(unavail2);
    //     states2.add(installed2);
    //     states2.add(started2);
    //     states2.add(failed2);
        
    //     List<Requirement> requirements2 = new ArrayList<Requirement>();
    //     Requirement db2 = new Requirement("db");
    //     requirements2.add(db2);
        
    //     List<Capability> capabilities2 = new ArrayList<Capability>();
    //     Capability endp2 = new Capability("endp");
    //     capabilities2.add(endp2);
        
    //     List<Operation> operations2 = new ArrayList<Operation>();
    //     Operation install2 = new Operation("install");
    //     Operation uninstall2 = new Operation("uninstall");
    //     Operation configure2 = new Operation("configure");
    //     Operation start2 = new Operation("start");
    //     Operation stop2 = new Operation("stop");
    //     operations2.add(install2);
    //     operations2.add(uninstall2);
    //     operations2.add(configure2);
    //     operations2.add(start2);
    //     operations2.add(stop2);
        
    //     List<FaultHandling> fh2 = new ArrayList<FaultHandling>();
    //     FaultHandling started_f2 = new FaultHandling(started2, failed2);
    //     fh2.add(started_f2);
        
    //     List<Requirement> empty_r2 = new ArrayList<Requirement>();
    //     List<Capability> empty_c2 = new ArrayList<Capability>();
        
    //     started2.addRequirement(db2);
    //     started2.addCapability(endp2);

    //     ManagementProtocol mp2 = new ManagementProtocol(states2, requirements2, capabilities2, operations2, unavail2, fh2);
    //     mp2.addTransition(install2, unavail2, installed2, empty_r2, empty_c2);
    //     mp2.addTransition(uninstall2, installed2, unavail2, empty_r2, empty_c2);
    //     mp2.addTransition(configure2, installed2, installed2, empty_r2, empty_c2);
    //     mp2.addTransition(start2, installed2, started2, empty_r2, empty_c2);
    //     mp2.addTransition(stop2, started2, installed2, empty_r2, empty_c2);
    //     mp2.addTransition(uninstall2, failed2, unavail2, empty_r2, empty_c2);
        
    //     IOLTS iolts = new IOLTS(mp);
    //     IOLTS iolts2 = new IOLTS(mp2);
        
    //     // true --> equals, false --> greater than equal to
    //     boolean gamma = false;
        
    //     // true --> strict comparison, false --> superset comparison 
    //     boolean beta = false;
        
    //     Mpioco mpioco = new Mpioco(iolts, iolts2, gamma, beta);
    //     mpioco.checkConformance();     
	// }
	
}
