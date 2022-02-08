package mp;

public class FaultHandling {

	private State sourceState, faultState;
	
	public FaultHandling(State sourceState, State faultState) {
		super();
		this.sourceState = sourceState;
		this.faultState = faultState;
	}

	public State getSourceState() {
		return sourceState;
	}

	public void setSourceState(State sourceState) {
		this.sourceState = sourceState;
	}

	public State getFaultState() {
		return faultState;
	}

	public void setFaultState(State faultState) {
		this.faultState = faultState;
	}

}
