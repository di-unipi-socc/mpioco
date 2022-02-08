package iolts;

import mp.Operation;

public class OpTerAction extends OutputAction{
	
	private Operation operation;
	
	public OpTerAction(Operation operation) {
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
