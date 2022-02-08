package mp;

public class Operation {
	
	private String name;
	
	public Operation(String name) {
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
        
        if (!(o instanceof Operation)) { 
            return false; 
        } 

        Operation r = (Operation) o; 
          
        return this.name.equals(r.getName()); 
    } 

}
