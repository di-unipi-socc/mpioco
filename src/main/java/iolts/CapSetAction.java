package iolts;

import java.util.List;

import mp.Capability;

public class CapSetAction extends OutputAction{
	
	private List<Capability> capabilities;
	
	public CapSetAction(List<Capability> capabilities) {
		super();
		this.capabilities = capabilities;
	}

	public List<Capability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(List<Capability> capabilities) {
		this.capabilities = capabilities;
	}

}
