package iolts;

import java.util.UUID;

public class IOLTSTransition {
	
	private String id = UUID.randomUUID().toString();

	private Action action;
	
	private Configuration sourceConf, targetConf;
	
	public IOLTSTransition(Action action, Configuration sourceConf, Configuration targetConf) {
		super();
		this.action = action;
		this.sourceConf = sourceConf;
		this.targetConf = targetConf;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Configuration getSourceConf() {
		return sourceConf;
	}

	public void setSourceConf(Configuration sourceConf) {
		this.sourceConf = sourceConf;
	}

	public Configuration getTargetConf() {
		return targetConf;
	}

	public void setTargetConf(Configuration targetConf) {
		this.targetConf = targetConf;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	} 
	
    @Override
    public boolean equals(Object o) { 
  
        if (o == this) { 
            return true; 
        } 
        
        if (!(o instanceof IOLTSTransition)) { 
            return false; 
        } 

        IOLTSTransition t = (IOLTSTransition) o; 
          
        return this.id.equals(t.getId()); 
    }
	
}
