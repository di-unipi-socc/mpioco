package mp;

public class Requirement {
	
	private String name;
	
	public Requirement(String name) {
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
        
        if (!(o instanceof Requirement)) { 
            return false; 
        } 

        Requirement r = (Requirement) o; 
          
        return this.name.equals(r.getName()); 
    } 

}
