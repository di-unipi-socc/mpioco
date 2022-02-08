package mp;

import java.util.List;

public class Transition {	

	private Operation operation;
	
	private State sourceState, targetState;
	
	private List<Requirement> conditions;
	
	private List<Capability> capabilities;
	
	public Transition(Operation operation, State sourceState, State targetState, List<Requirement> conditions,
			List<Capability> capabilities) {
		super();
		this.operation = operation;
		this.sourceState = sourceState;
		this.targetState = targetState;
		this.conditions = conditions;
		this.capabilities = capabilities;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public State getSourceState() {
		return sourceState;
	}

	public void setSourceState(State sourceState) {
		this.sourceState = sourceState;
	}

	public State getTargetState() {
		return targetState;
	}

	public void setTargetState(State targetState) {
		this.targetState = targetState;
	}

	public List<Requirement> getConditions() {
		return conditions;
	}

	public void setConditions(List<Requirement> conditions) {
		this.conditions = conditions;
	}

	public List<Capability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(List<Capability> capabilities) {
		this.capabilities = capabilities;
	}

}
