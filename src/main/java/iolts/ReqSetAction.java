package iolts;

import java.util.List;

import mp.Requirement;

public class ReqSetAction extends InputAction{
	
	private List<Requirement> requirements;
	
	public ReqSetAction(List<Requirement> requirements) {
		super();
		this.requirements = requirements;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}

	

}
