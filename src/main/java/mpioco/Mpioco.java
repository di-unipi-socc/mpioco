package mpioco;

import java.util.ArrayList;
import java.util.List;

import iolts.Action;
import iolts.CapSetAction;
import iolts.Configuration;
import iolts.FaultAction;
import iolts.IOLTS;
import iolts.OpInvAction;
import iolts.OpTerAction;
import iolts.OutputAction;
import iolts.QuiescenceAction;
import iolts.ReqSetAction;
import mp.Capability;
import mp.Operation;
import mp.Requirement;
import mp.State;

public class Mpioco {
	
	private IOLTS specification;
	private IOLTS implementation;
	private boolean gamma;
	private boolean beta;
	
	/**
	 * Generates the mpioco relation from a specification, the implementation and the parameters gamma and beta
	 * from which conformance can be checked.
	 * @param specification the IOLTS of the specification that is to be checked.
	 * @param implementation the IOLTS of the implementation that is to be checked.
	 * @param gamma the gamma parameter of the mpioco relation, where true responds to 'equal' and false to greater than equal to.
	 * @param beta the beta parameter of the mpioco relation, where true responds to the strict comparison nad false to the superset comparison.
	 */
	public Mpioco(IOLTS specification, IOLTS implementation, boolean gamma, boolean beta) {
		this.specification = specification;
		this.implementation = implementation;
		this.gamma = gamma;
		this.beta = beta;
	}
	
	/**
	 * Checks the conformance of the implementation to the specification according to the method discussed in the paper.
	 * @return whether the implementation conforms to the specification with respect to the parameters set.
	 */
	public boolean checkConformance() {
		if(specification.getMp() != null && implementation.getMp() != null) {
			for(State s_spec : specification.getMp().getStates()) {
				boolean found_s = false;
				for(State s_imp : implementation.getMp().getStates()) {
					if(s_spec.getName().equals(s_imp.getName()))
						found_s = true;
				}
				if(!found_s)
					throw new IllegalArgumentException("The two management protocols do not describe the same behaviour.");
			}
			
			for(Operation o_spec : specification.getMp().getOperations()) {
				boolean found_o = false;
				for(Operation o_imp : implementation.getMp().getOperations()) {
					if(o_spec.getName().equals(o_imp.getName()))
						found_o = true;
				}
				if(!found_o)
					throw new IllegalArgumentException("The two management protocols do not describe the same behaviour.");
			}
		}

		this.implementation.makeInputEnabled();
		this.specification.checkQuiescence();
		this.implementation.checkQuiescence();
		
        this.specification.straceToAll(this.specification.getInitialConf());
        List<List<Action>> straces = this.specification.getStraceList();
        
        for(List<Action> strace : straces) {
        	List<Action> strace_spec = new ArrayList<Action>(strace);
        	List<Action> strace_imp = new ArrayList<Action>(strace);
        	
        	List<Configuration> confs_imp = this.implementation.gammaReachability(this.implementation.getInitialConf(), strace_imp, this.gamma);        	
        	List<Configuration> confs_spec = this.specification.gammaReachability(this.specification.getInitialConf(), strace_spec, this.gamma);
        	
        	if(!compareSets(this.implementation.out(confs_imp), this.specification.out(confs_spec), this.beta) || confs_imp.isEmpty()) {
        	//if(!compareSets(this.specification.out(confs_spec), this.implementation.out(confs_imp), this.beta) || confs_imp.isEmpty()) {
        		System.out.println("Conformance failed at strace: ");
            	printStrace(strace);
            	return false;
        	}
        }
        
//        for(List<Action> strace : straces) {
//        	printStrace(strace);
//        }
		return true;
	}
	
	/**
	 * Compares two sets corresponding to the parameter beta and the method discussed in the paper.
	 * @param set_one the list of output actions of a configuration in the specification.
	 * @param set_two the list of output actions of a configuration in the implementation.
	 * @param beta the beta parameter set of the mpioco relation.
	 * @return how set_one and set_two compare to each other with respect to beta.
	 */
	public boolean compareSets(List<OutputAction> set_one, List<OutputAction> set_two, boolean beta) {
		if(beta) {
			//if(containsSet(set_one, set_two) && containsSet(set_two, set_one)) {
			if(containsSet(set_one, set_two)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if(containsSuperSet(set_one, set_two)) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * Checks whether list two is a superset of list one.
	 * @param one list of output actions.
	 * @param two list of output actions.
	 * @return whether set one contains set two.
	 */
	private boolean containsSet(List<OutputAction> one, List<OutputAction> two) {		
		for(OutputAction oa : one) {
			boolean found = false;
			for(OutputAction oa2 : two) {
				if(oa instanceof CapSetAction && oa2 instanceof CapSetAction) {
					for(Capability c_one : ((CapSetAction) oa).getCapabilities()) {
						boolean found_c = false;
						for(Capability c_two : ((CapSetAction) oa2).getCapabilities()) {
							if(c_one.getName().equals(c_two.getName()))
								found_c = true;
						}
						if(!found_c)
							return false;
					
					}
					for(Capability c_two : ((CapSetAction) oa2).getCapabilities()) {
						boolean found_c = false;
						for(Capability c_one : ((CapSetAction) oa).getCapabilities()) {
							if(c_two.getName().equals(c_one.getName()))
								found_c = true;
						}
						if(!found_c)
							return false;
					
					}
					found = true;
				}
				else if(oa instanceof FaultAction && oa2 instanceof FaultAction)
					found = true;				
				else if (oa instanceof QuiescenceAction && oa instanceof QuiescenceAction)
					found = true;
			}
			if(!found)
				return false;
		}
		return true;
	}
	
	/**
	 * Checks whether list two is a superset of list one.
	 * @param one list of output actions.
	 * @param two list of output actions.
	 * @return whether set one contains a super set of two.
	 */
	private boolean containsSuperSet(List<OutputAction> one, List<OutputAction> two) {		
		for(OutputAction oa : one) {
			boolean found = false;
			for(OutputAction oa2 : two) {
				if(oa instanceof CapSetAction && oa2 instanceof CapSetAction) {
					for(Capability c_two : ((CapSetAction) oa2).getCapabilities()) {
						boolean found_c = false;
						for(Capability c_one : ((CapSetAction) oa).getCapabilities()) {
							if(c_two.getName().equals(c_one.getName()))
								found_c = true;
						}
						if(!found_c)
							return false;					
					}
					found = true;
				}
				else if(oa instanceof FaultAction && oa2 instanceof FaultAction)
					found = true;				
				else if (oa instanceof QuiescenceAction && oa instanceof QuiescenceAction)
					found = true;
			}
			if(!found)
				return false;
		}
		return true;
	}
	
	/**
	 * Prints a strace of actions given in a list corresponding to their type.
	 * @param strace the strace that is to be printed.
	 */
	private void printStrace(List<Action> strace) {
		for(Action action : strace) {
			if(action instanceof ReqSetAction) {
				for(Requirement r : ((ReqSetAction) action).getRequirements()) {
					System.out.println("Req " + r.getName());
				}
			}
			if(action instanceof CapSetAction) {
				for(Capability c : ((CapSetAction) action).getCapabilities()) {
					System.out.println("Cap " + c.getName());
				}
			}
			if(action instanceof OpInvAction)
				System.out.println("OpInv " + ((OpInvAction) action).getOperation().getName());
			if(action instanceof OpTerAction)
				System.out.println("OpTer " + ((OpTerAction) action).getOperation().getName());
			if(action instanceof FaultAction)
				System.out.println("Fault");
		}
		System.out.println();
	}

}
