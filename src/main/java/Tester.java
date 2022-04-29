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
	
	public static void main( String[] args )
    {	
        // Parsing CLI options (beta, gamma)
        CommandLine commandLine = null;
        Options options = new Options();
        Option optHelp = Option.builder("h").longOpt("help").build();
        options.addOption(optHelp);
        Option optCompositeTest = Option.builder("c").longOpt("composite").build();
        options.addOption(optCompositeTest);
        Option optBeta = Option.builder("b").longOpt("beta").hasArg().build();
        options.addOption(optBeta);
        Option optGamma = Option.builder("g").longOpt("gamma").hasArg().build();
        options.addOption(optGamma);
        CommandLineParser parser = new DefaultParser();
        try {
            commandLine = parser.parse(options, args);
        } catch(Exception e) {
            printHelp("Wrong options used");
        }

        // Printing help, if needed
        if (commandLine.hasOption("h")) {
            printHelp(null);
            return;
        }
        
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
                printHelp("Unknown value for beta");
                return;
        }
        // Setting value of gamma (specified, or relaxed - by default)
        boolean gamma = false;
        if (commandLine.hasOption("g")) {
            if (commandLine.getOptionValue("g").equalsIgnoreCase("strict")) 
                gamma = true;
            else if (!commandLine.getOptionValue("g").equalsIgnoreCase("relaxed"))
                printHelp("Unknown value for gamma");
                return;
        }

        // Parsing arguments
        String[] paths = commandLine.getArgs();
        if (paths.length != 2) {
            printHelp("Wrong amount of arguments");
            return;
        }
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
	
	public static void printHelp(String errorMessage) {
        if(errorMessage != null) {
            System.out.println("ERROR: " + errorMessage + "\n");
        }
        System.out.println("The tester can be used as follows:");
        System.out.println("\tjava -jar mpioco-0.1.jar OPTIONS PATH_TO_SPEC PATH_TO_IMPL");
        System.out.println("where OPTIONS can be:");
        System.out.println("  -b strict|relaxed   to test with stricter/relaxed version of beta");
        System.out.println("  -g strict|relaxed   to test with stricter/relaxed version of gamma");
        System.out.println("  -c                  to test whole application (instead of single node)");
    }
}
