package composite;

import java.util.ArrayList;
import java.util.List;

import mp.Capability;
import mp.Requirement;
import mp.State;

public class GlobalState {
	
	private List<State> nodeStates;
	
	private List<GsTransition> transitions;
	
	private String name;
	
	private List<Requirement> requirements;
	
	private List<Capability> capabilities;
	
	/**
	 * Generates a new global state from a list of node states. 
	 * @param nodeStates the list of node states in the global state
	 */
	public GlobalState(List<State> nodeStates) {
		this.nodeStates = nodeStates;
		this.transitions = new ArrayList<GsTransition>();
		this.setName();
	}
	
	/**
	 * Calculates the requirements of all the current node states
	 * in the global state. 
	 * @return the list of all requirements needed by the node states.
	 */
	public ArrayList<Requirement> rho(){
		
		ArrayList<Requirement> rho = new ArrayList<Requirement>();
		
		for(State s : this.getNodeStates()) {
			for(Requirement r : s.getRequirements()) {
				if(!rho.contains(r))
					rho.add(r);
			}
		}
		
		return rho;
	}
	
	/**
	 * Calculates the capabilities of all the current node states
	 * in the global states. 
	 * @return the list of all capabilities offered by the node states.
	 */
	public ArrayList<Capability> chi(){
		
		ArrayList<Capability> chi = new ArrayList<Capability>();
		
		for(State s : this.getNodeStates()) {
			for(Capability c : s.getCapabilities()) {
				if(!chi.contains(c))
					chi.add(c);
			}
		}
		
		return chi;
	}
	
	/**
	 * Calculates all the bound capabilities of all the current node states
	 * in the global state. 
	 * @param reqs list of requirements which need to be bound.
	 * @return list of capabilities which are bound by the requirements.
	 */
	public ArrayList<Capability> bind(List<Requirement> reqs){
		
		ArrayList<Capability> bind = new ArrayList<Capability>();
		
		for(Requirement r : reqs) {
			Capability c = new Capability(r.getName());
			if(!bind.contains(c))
				bind.add(c);
		}
		
		return bind;
	}
	
	/**
	 * Calculates all the pending faults of the global states, by
	 * comparing the list of requirements which are bounded
	 * and the already bound requirements. 
	 * @return the list of pending faults consisting of the requirements of the node state which are not bound.
	 */
	public ArrayList<Requirement> pendingFaults(){
		
		ArrayList<Requirement> faults = new ArrayList<Requirement>();	
		
		ArrayList<Requirement> rho = this.rho();
		ArrayList<Capability> chi = this.chi();
		ArrayList<Capability> bind = bind(rho);
		
		for(Capability b : bind) {
			boolean found = false;
			for(Capability c : chi) {
				if(b.equals(c))
					found = true;
			}
			if(!found)
				faults.add(new Requirement(b.getName()));
		}
		
		return faults;
	}
	
	/**
	 * Adds a fault transition to the global state to a target.
	 * @param target the target global state of the transition.
	 */
	public void addTransition(GlobalState target) {
		this.transitions.add(new GsTransition(this, target));
	}
	
	/**
	 * Adds a operation transition of the global state to a target.
	 * @param target the target global state of the transition.
	 * @param operation the operation invoked in the transition.
	 * @param conditions the list of requirements of the global state given by its node states.
	 * @param capabilities the list of capabilities of the global state given by its node states..
	 */
	public void addTransition(GlobalState target, String operation, List<Requirement> conditions, List<Capability> capabilities) {
		this.transitions.add(new GsTransition(this, target, operation, conditions, capabilities));
	}
	
	/**
	 * @return the generated name of the global state.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Calculates the name of the global state from its node states, by ordering them.
	 */
	public void setName() {
		List<State> states = new ArrayList<State>();
		for(int i=0; i<this.nodeStates.size(); i++) {
			for(State s : this.nodeStates) {
				if(s.getName().startsWith("n" + i))
					states.add(s);
			}
		}
		this.nodeStates = states;
		
		String result = "{";
		for(int i=0; i<this.getNodeStates().size(); i++) {
			State s = this.getNodeStates().get(i);
			result = result  + s.getName() + ",";
		}

		result = result.substring(0, result.length() - 1);
		result = result + "}";
		
		this.name = result;
	}
	
	/** Calculates the list of requirements of the global state from its node states. If already calculated it just returns the list.
	 * @return the list of requirements of the global state.
	 */
	public List<Requirement> getRequirements() {
		if(this.requirements != null) {
			return this.requirements;
		}
		else {
			List<Requirement> result = new ArrayList<Requirement>();
			for(State s : this.getNodeStates()) {
				for(Requirement r : s.getRequirements()) {
					if(!result.contains(r))
						result.add(r);
				}				
			}
			
			this.requirements = result;
			
			return result;
		}
	}
	
	/** Calculates the list of capabilities of the global state from its node states. If already calculated it just returns the list.
	 * @return the list of capabilities of the global state.
	 */
	public List<Capability> getCapabilities() {
		if(this.capabilities != null) {
			return this.capabilities;
		}
		else {
			List<Capability> result = new ArrayList<Capability>();
			for(State s : this.getNodeStates()) {
				for(Capability c : s.getCapabilities()) {
					if(!result.contains(c))
						result.add(c);
				}				
			}
			
			this.capabilities = result;
			
			return result;
		}
	}

	public List<GsTransition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<GsTransition> transitions) {
		this.transitions = transitions;
	}
	
	public List<State> getNodeStates() {
		return nodeStates;
	}

	public void setNodeStates(List<State> nodeStates) {
		this.nodeStates = nodeStates;
	}
	
	@Override
    public boolean equals(Object o) { 
  
        if (o == this) { 
            return true; 
        } 
        
        if (!(o instanceof GlobalState)) { 
            return false; 
        } 

        GlobalState g = (GlobalState) o;
        
        ArrayList<State> one = new ArrayList<State>(this.getNodeStates());
        ArrayList<State> two = new ArrayList<State>(g.getNodeStates());
        
        if(one.size() != two.size())
        	return false;
        
        boolean foundOne = true;
        for(State so : one) {
        	boolean foundO = false;
        	for(State st : two) {
        		if(so.equals(st))
        			foundO = true;
        	}
        	if(!foundO)
        		foundOne = false;
        }
        boolean foundTwo = true;
        for(State st : two) {
        	boolean foundT = false;
        	for(State so : one) {
        		if(st.equals(so))
        			foundT = true;
        	}
        	if(!foundT)
        		foundTwo = false;
        }
        
        if(foundOne && foundTwo)
        	return true;
        else
        	return false;
    }
	
}
