package test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import iolts.IOLTS;
import mp.Capability;
import mp.FaultHandling;
import mp.ManagementProtocol;
import mp.Operation;
import mp.Requirement;
import mp.State;
import mpioco.Mpioco;

public class TestMpioco {
	
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
        
        a.addRequirement(need);
        c.addCapability(offer);
        
        ManagementProtocol mp = new ManagementProtocol(states, requirements, capabilities, operations, a, fh);
        
        IOLTS iolts = new IOLTS(mp);
        IOLTS iolts2 = new IOLTS(mp);
        
        Mpioco mpioco = new Mpioco(iolts, iolts2, true, true);
        assertTrue(mpioco.checkConformance());
        
        Mpioco mpioco2 = new Mpioco(iolts, iolts2, false, false);
        assertTrue(mpioco2.checkConformance());
	}
}
