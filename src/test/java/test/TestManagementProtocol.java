package test;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import mp.Capability;
import mp.FaultHandling;
import mp.ManagementProtocol;
import mp.Operation;
import mp.Requirement;
import mp.State;

public class TestManagementProtocol {
	
	@Test
	public void testAbc() {		
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
        
        // General construction test
        assertTrue(mp.getInitialState().getName().equals(a.getName()));
        assertTrue(a.getRequirements().get(0).getName().equals(need.getName()));
        assertTrue(c.getCapabilities().get(0).getName().equals(offer.getName()));
        
        mp.addTransition(op_1, a, b, empty_r, empty_c);
        mp.addTransition(op_2, b, c, empty_r, empty_c);
        mp.addTransition(op_3, c, a, empty_r, empty_c);
        
        // Transitions test
        assertTrue(mp.getTransitions().size() == 3);        
	}

}
