package test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import iolts.Action;
import iolts.Configuration;
import iolts.IOLTS;
import iolts.OpInvAction;
import iolts.OpTerAction;
import mp.Capability;
import mp.FaultHandling;
import mp.ManagementProtocol;
import mp.Operation;
import mp.Requirement;
import mp.State;

public class TestIOLTS {
	
	@Test
	public void testABC() {
        List<State> states = new ArrayList<State>();
		State a = new State("A");
		State b = new State("B");
		State c = new State("C");
		State failed = new State("failed");
		states.add(a);
		states.add(b);
		states.add(c);
        states.add(failed);
		
        List<Requirement> requirements = new ArrayList<Requirement>();
        Requirement need = new Requirement("needs");
        requirements.add(need);
        
        List<Capability> capabilities = new ArrayList<Capability>();
        Capability offer = new Capability("offers");
        capabilities.add(offer);
        
        a.addRequirement(need);
        c.addCapability(offer);
        
        List<Operation> operations = new ArrayList<Operation>();
        Operation op_1 = new Operation("op_1");
        Operation op_2 = new Operation("op_2");
        Operation op_3 = new Operation("op_3");
        operations.add(op_1);
        operations.add(op_2);
        operations.add(op_3);
        
        
        List<FaultHandling> fh = new ArrayList<FaultHandling>();
        FaultHandling c_f = new FaultHandling(c, failed);
        fh.add(c_f);
        
        List<Requirement> empty_r = new ArrayList<Requirement>();
        List<Capability> empty_c = new ArrayList<Capability>();
        
        a.addRequirement(need);
        c.addCapability(offer);
        
        ManagementProtocol mp = new ManagementProtocol(states, requirements, capabilities, operations, a, fh);
        
        mp.addTransition(op_1, a, b, empty_r, empty_c);
        mp.addTransition(op_2, b, c, empty_r, empty_c);
        mp.addTransition(op_3, c, a, empty_r, empty_c);
        
        IOLTS iolts = new IOLTS(mp);
        
        // General construction test
        assertTrue(iolts.getInitialConf().getName().equals(mp.getInitialState().getName()));
        assertTrue(iolts.getConfigurations().size() == (states.size() + operations.size() + fh.size()));
        
        iolts.straceToAll(iolts.getInitialConf());
        // assertTrue(iolts.getStraceList().size() == 21);        
        
        // Input-Enabledness test
        iolts.makeInputEnabled();
        
        assertTrue(iolts.getConfigurations().size() == (states.size() + operations.size() + fh.size() + 1));
        
       // Quiescence test 
        iolts.checkQuiescence();
        
        assertTrue(iolts.getInitialConf().isQuiescent());
        
        // Reachability test
        OpInvAction one_inv = new OpInvAction(op_1);
        OpTerAction one_ter = new OpTerAction(op_1);
        OpInvAction two_inv = new OpInvAction(op_2);
        OpTerAction two_ter = new OpTerAction(op_2);
        OpInvAction three_inv = new OpInvAction(op_3);
        OpTerAction three_ter = new OpTerAction(op_3);
        List<Action> strace = new ArrayList<Action>();
        strace.add(one_inv);
        strace.add(one_ter);
        strace.add(two_inv);
        strace.add(two_ter);
        strace.add(three_inv);
        strace.add(three_ter);
        
        List<Configuration> res = iolts.gammaReachability(iolts.getInitialConf(), strace, false);
        
        assertTrue(res.get(0).getName().equals("A"));
	}

}
