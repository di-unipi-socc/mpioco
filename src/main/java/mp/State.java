package mp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class State {
	
	private String id = UUID.randomUUID().toString();
	
	private String name;
	
	private List<Requirement> requirements;
	
	private List<Capability> capabilities;
	
//	private List<Transition> transitions;
	
	public State(String name) {
		super();
		this.name = name;
		this.requirements = new ArrayList<Requirement>();
		this.capabilities = new ArrayList<Capability>();
//		this.transitions = new ArrayList<Transition>();
	}
	
	public void addRequirement(Requirement requirement) {
		this.requirements.add(requirement);
	}
	
	public void addCapability(Capability capability) {
		this.capabilities.add(capability);
	}
	
	
//	public void addTransition(State targetState, Operation operation, List<Requirement> conditions, List<Capability> capabilities) {
//		Transition nt = new Transition(operation, this, targetState, conditions, capabilities);
////		this.transitions.add(nt);
//	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}

	public List<Capability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(List<Capability> capabilities) {
		this.capabilities = capabilities;
	}
/*
	public List<Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}
*/
	
    @Override
    public boolean equals(Object o) { 
  
        if (o == this) { 
            return true; 
        } 
        
        if (!(o instanceof State)) { 
            return false; 
        } 

        State s = (State) o; 
          
        return this.id.equals(s.getId()); 
    } 
}
