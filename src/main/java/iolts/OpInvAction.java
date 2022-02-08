package iolts;

import mp.Operation;

public class OpInvAction extends InputAction{
	
	Operation operation;
	
	public OpInvAction(Operation operation) {
		super();
		this.operation = operation;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}	

}
