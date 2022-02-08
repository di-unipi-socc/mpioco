package iolts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mp.Capability;
import mp.Requirement;
import mp.State;

public class Configuration {
	
	private String id = UUID.randomUUID().toString();

	private String name;
	
	private List<IOLTSTransition> transitions;
	
	private boolean isState;
	
	private boolean quiescent;
	
	public Configuration(String name, List<IOLTSTransition> transitions) {
		super();
		this.name = name;
		this.transitions = transitions;
	}
	
	public Configuration(String name, boolean state) {
		super();
		this.name = name;
		this.transitions = new ArrayList<IOLTSTransition>();
		this.isState = state;
	}
	
	public Configuration(String name) {
		super();
		this.name = name;
		this.transitions = new ArrayList<IOLTSTransition>();
		this.isState = false;
	}
	
	public void addTransition(Action action, Configuration targetConf) {
		IOLTSTransition nt = new IOLTSTransition(action, this, targetConf);
		this.transitions.add(nt);
	}
	
	public ReqSetAction getRequirements() {
		for(IOLTSTransition t : transitions) {
			if(t.getAction() instanceof ReqSetAction) {
				return (ReqSetAction) t.getAction();
			}
		}
		return new ReqSetAction(new ArrayList<Requirement>());
	}
	
	public CapSetAction getCapabilities() {
		for(IOLTSTransition t : transitions) {
			if(t.getAction() instanceof CapSetAction) {
				return (CapSetAction) t.getAction();
			}
		}
		return new CapSetAction(new ArrayList<Capability>());
	}
	
	public List<OpInvAction> getOpInvActions(){
		List<OpInvAction> result = new ArrayList<OpInvAction>();
		for(IOLTSTransition t : transitions) {
			if(t.getAction() instanceof OpInvAction) {
				result.add((OpInvAction) t.getAction());
			}
		}
		return result;
	}
	
	public List<OpTerAction> getOpTerActions(){
		List<OpTerAction> result = new ArrayList<OpTerAction>();
		for(IOLTSTransition t : transitions) {
			if(t.getAction() instanceof OpTerAction) {
				result.add((OpTerAction) t.getAction());
			}
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IOLTSTransition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<IOLTSTransition> transitions) {
		this.transitions = transitions;
	}

	public boolean isState() {
		return isState;
	}

	public void setState(boolean isState) {
		this.isState = isState;
	}

	public boolean isQuiescent() {
		return quiescent;
	}

	public void setQuiescent(boolean quiescent) {
		this.quiescent = quiescent;
	}
	
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
