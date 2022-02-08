package mp;

import java.util.ArrayList;
import java.util.List;

public class ManagementProtocol {
	
	private String name;

	private State initialState;
	
	private List<State> states;
	
	private List<Requirement> requirements;
	
	private List<Capability> capabilities;
	
	private List<Operation> operations;
	
	private List<FaultHandling> faultHandler;
	
	private List<Transition> transitions;
	
	public ManagementProtocol(List<State> states, List<Requirement> requirements, List<Capability> capabilities,
			List<Operation> operations, State initialState, List<FaultHandling> faultHandler) {
		super();
		this.states = states;
		this.requirements = requirements;
		this.capabilities = capabilities;
		this.operations = operations;
		this.initialState = initialState;
		this.faultHandler = faultHandler;
		this.transitions = new ArrayList<Transition>();
	}
	
	public void addTransition(Operation operation, State sourceState, State targetState, List<Requirement> conditions, List<Capability> capabilities) {
		Transition nt = new Transition(operation, sourceState, targetState, conditions, capabilities);
//		sourceState.addTransition(targetState, operation, conditions, capabilities);
		this.transitions.add(nt);
	}
	
	public List<Transition> getTransitions(){
		return transitions;
	}
	
	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public State getInitialState() {
		return initialState;
	}

	public void setInitialState(State initialState) {
		this.initialState = initialState;
	}

	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
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

	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}

	public List<FaultHandling> getFaultHandler() {
		return faultHandler;
	}

	public void setFaultHandler(List<FaultHandling> faultHandler) {
		this.faultHandler = faultHandler;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
