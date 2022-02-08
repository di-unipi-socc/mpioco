package composite;

import java.util.List;

import mp.Capability;
import mp.Operation;
import mp.Requirement;

public class GsTransition {
	
	private GlobalState sourceState;
	
	private GlobalState targetState;
	
	private Operation operation;
	
	private List<Requirement> conditions;
	
	private List<Capability> capabilities;
	
	private boolean faultFlag;
	
	public GsTransition(GlobalState sourceState, GlobalState targetState, String operation, List<Requirement> conditions, List<Capability> capabilities) {
		this.setSourceState(sourceState);
		this.setTargetState(targetState);
		Operation op = new Operation(operation);
		this.setOperation(op);
		this.conditions = conditions;
		this.capabilities = capabilities;
		this.faultFlag = false;
	}
	
	public GsTransition(GlobalState sourceState, GlobalState targetState) {
		this.setSourceState(sourceState);
		this.setTargetState(targetState);
		this.faultFlag = true;
	}

	public GlobalState getSourceState() {
		return sourceState;
	}

	public void setSourceState(GlobalState sourceState) {
		this.sourceState = sourceState;
	}

	public GlobalState getTargetState() {
		return targetState;
	}

	public void setTargetState(GlobalState targetState) {
		this.targetState = targetState;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}	

	public boolean isFaultFlag() {
		return faultFlag;
	}

	public void setFaultFlag(boolean faultFlag) {
		this.faultFlag = faultFlag;
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
	
    @Override
    public boolean equals(Object o) { 
  
        if (o == this) { 
            return true; 
        } 
        
        if (!(o instanceof GsTransition)) { 
            return false; 
        } 

        GsTransition gt = (GsTransition) o;
        
        
        if(!faultFlag)
        	return (this.getSourceState().equals(gt.getSourceState()) && this.getTargetState().equals(gt.getTargetState()) && this.getOperation().equals(gt.getOperation()));
        else
        	return  (this.getSourceState().equals(gt.getSourceState()) && this.getTargetState().equals(gt.getTargetState()));
    }

}
