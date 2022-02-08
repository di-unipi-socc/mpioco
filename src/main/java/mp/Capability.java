package mp;

public class Capability {
	
	private String name;
	
	public Capability(String name) {
		super();
		this.name = name;
	}	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    @Override
    public boolean equals(Object o) { 
  
        if (o == this) { 
            return true; 
        } 
        
        if (!(o instanceof Capability)) { 
            return false; 
        } 

        Capability c = (Capability) o; 
          
        return this.name.equals(c.getName()); 
    } 

}
