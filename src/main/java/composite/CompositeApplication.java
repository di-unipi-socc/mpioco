package composite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mp.Capability;
import mp.FaultHandling;
import mp.ManagementProtocol;
import mp.Operation;
import mp.Requirement;
import mp.State;
import mp.Transition;
import util.ToscaUtil;

public class CompositeApplication {
	
	private List<ManagementProtocol> nodeList;
	
	private GlobalState initialState;
	
	private List<GlobalState> globalStates;
	
	private List<GsTransition> transitionList;
	
	private List<List<String>> compositeTraceList;
	
	private List<Operation> operations; 
	
	private List<Requirement> requirements;
	
	private List<Capability> capabilities;
	
	/**
	 * Generates a new composite application from a given directory. 
	 * @param appDirectory the directory where the node documentation lies.
	 */
	public CompositeApplication(String appDirectory) {
		this.globalStates = new ArrayList<GlobalState>();
		this.transitionList = new ArrayList<GsTransition>();
		init(appDirectory);
		opInference();
		faultInference();
	}
	
	/**
	 * Initializes the composite application, by generating all the nodes
	 * of the application from the given directory and setting the initial state. 
	 * @param directory the directory from which the nodes are to be generated.
	 */
	public void init(String directory) {
		
		nodeList = new ArrayList<ManagementProtocol>();
		
		File[] dir = new File(directory).listFiles();
		int length = dir.length;
		
		for(int i=0; i<length; i++) {
			File file = dir[i];
			String path = file.getAbsolutePath();
			nodeList.add(ToscaUtil.fetchToMp(path));
		}
		
		for(int i=0; i<nodeList.size(); i++) {
			ManagementProtocol mp = nodeList.get(i);
			for(int j=0; j<mp.getStates().size(); j++) {
				mp.getStates().get(j).setName("n" + i + ":" + mp.getStates().get(j).getName());
			}
		}
		
		ArrayList<State> initStates = new ArrayList<State>();
		
		for(int i=0; i<nodeList.size(); i++) {
			initStates.add(nodeList.get(i).getInitialState());
		}
		
		this.setInitialState(new GlobalState(initStates));
		globalStates.add(this.getInitialState());
	}
	

	
	/**
	 * Generates all possible operation transitions of the composite application, by
	 * using the operation inference rule given in the paper.
	 */
	public void opInference() {
		
		ArrayList<GlobalState> workingList = new ArrayList<GlobalState>();
		workingList.add(this.getInitialState());
		
		while(!workingList.isEmpty()) {
			GlobalState current = workingList.get(0);
			for(State s : current.getNodeStates()) {
				ArrayList<Transition> stateTransitionList = new ArrayList<Transition>();
				boolean foundS = false;
				for(ManagementProtocol mp : this.getNodeList()) {
					if(foundS)
						break;
					for(State mps : mp.getStates()) {
						if(mps.equals(s)) {
							foundS = true;
							for(Transition mpt : mp.getTransitions()) {
								if(mpt.getSourceState().equals(s))
									stateTransitionList.add(mpt);
							}
							break;
						}
					}					
				}
				for(Transition t : stateTransitionList) {
					ArrayList<Requirement> pf = current.pendingFaults();
					ArrayList<Capability> bound = current.bind(t.getConditions());
					ArrayList<Capability> cap = current.chi();
					boolean subset = true;
					for(Capability cb : bound) {
						boolean found = false;
						for(Capability cc : cap) {
							if(cb.equals(cc)) {
								found = true;
								break;
							}								
						}
						if(!found) {
							subset = false;
							break;
						}							
					}

					if(pf.isEmpty() && subset) {
						ArrayList<State> infStates = new ArrayList<State>(current.getNodeStates());
						infStates.remove(s);
						infStates.add(t.getTargetState());
						GlobalState target = new GlobalState(infStates);
						String nodePrefix = s.getName().substring(0, s.getName().indexOf(":"));
						GsTransition targetTrans = new GsTransition(current, target, nodePrefix + ":" + t.getOperation().getName(), t.getConditions(), t.getCapabilities());
						if(!globalStates.contains(target)) {
							globalStates.add(target);
							workingList.add(target);
						}
							
						if(!transitionList.contains(targetTrans)) {
							transitionList.add(targetTrans);
							current.addTransition(target, nodePrefix + ":" + t.getOperation().getName(), t.getConditions(), t.getCapabilities());
						}							
					}
				}
			}
			workingList.remove(0);
		}
	}
	
	/**
	 * Generates all possible fault transitions in the composite application, by
	 * using the fault inference rule given in the paper.
	 */
	public void faultInference() {
		
		ArrayList<GlobalState> workingList = new ArrayList<GlobalState>();
		workingList.addAll(globalStates);
		
		while(!workingList.isEmpty()) {
			GlobalState current = workingList.get(0);
			for(State s : current.getNodeStates()) {
				ArrayList<FaultHandling> faultTransitionList = new ArrayList<FaultHandling>();
				boolean foundS = false;
				for(ManagementProtocol mp : this.getNodeList()) {
					if(foundS)
						break;
					for(State mps : mp.getStates()) {
						if(mps.equals(s)) {
							foundS = true;
							for(FaultHandling fht : mp.getFaultHandler()) {
								if(fht.getSourceState().equals(s))
									faultTransitionList.add(fht);
							}
							break;
						}
					}
				}

				for(FaultHandling fh : faultTransitionList) {
					
					ArrayList<Requirement> reqS = (ArrayList<Requirement>) fh.getSourceState().getRequirements();
					ArrayList<Requirement> reqSprime = (ArrayList<Requirement>) fh.getFaultState().getRequirements();
					reqS.removeAll(current.pendingFaults());
					
					boolean subsetOne = true;
					for(Requirement rb : reqSprime) {
						boolean found = false;
						for(Requirement rc : reqS) {
							if(rb.equals(rc))
								found = true;
						}
						if(!found) {
							subsetOne = false;
							break;
						}
					}

					boolean exists = false;
					for(int i=0; i<faultTransitionList.size(); i++) {
						FaultHandling fht = faultTransitionList.get(i);
						ArrayList<Requirement> reqSDoublePrime = (ArrayList<Requirement>) fht.getFaultState().getRequirements();
						
				        boolean foundOne = true;
				        for(Requirement ro : reqS) {
				        	boolean foundO = false;
				        	for(Requirement rt : reqSDoublePrime) {
				        		if(ro.equals(rt))
				        			foundO = true;
				        	}
				        	if(!foundO)
				        		foundOne = false;
				        }
				        boolean foundTwo = true;
				        for(Requirement rt : reqSDoublePrime) {
				        	boolean foundT = false;
				        	for(Requirement ro : reqS) {
				        		if(rt.equals(ro))
				        			foundT = true;
				        	}
				        	if(!foundT)
				        		foundTwo = false;
				        }
				        
				        if((foundOne && foundTwo) || (!foundOne))
				        	continue;
				        
						boolean subsetTwo = true;
						for(Requirement rb : reqSDoublePrime) {
							boolean found = false;
							for(Requirement rc : reqS) {
								if(rb.equals(rc))
									found = true;
							}
							if(!found) {
								subsetTwo = false;
								break;
							}
						}
						
						if(subsetTwo) {
							exists = true;
							break;
						}							
					}
					
					if(!exists && subsetOne) {
						ArrayList<State> infStates = new ArrayList<State>(current.getNodeStates());
						infStates.remove(s);
						infStates.add(fh.getFaultState());
						GlobalState target = new GlobalState(infStates);
						GsTransition targetTrans = new GsTransition(current, target);
						if(!globalStates.contains(target)) {
							globalStates.add(target);
							workingList.add(target);
						}
							
						if(!transitionList.contains(targetTrans)) {
							transitionList.add(targetTrans);
							current.addTransition(target);
						}							
					}
				}
			}
			workingList.remove(0);
		}
	}
	
	/**
	 * Generates the list of composite traces from a given start global state, by
	 * calculating all possible paths to all global states from the start global state.
	 * Circles in traces are omitted. 
	 * @param start the global state from which the calculations begin.
	 */
	public void compositeTraces(GlobalState start) {
		this.compositeTraceList = new ArrayList<List<String>>();
		for(GlobalState gs : this.globalStates) {
			compositeTraceTo(start, gs);
		}
	}
	
	/**
	 * Generates the list of composite traces, by making a recursive depth-first search
	 * where the method is the head. 
	 * @param start the start global state
	 * @param end the global state were the composite trace stops
	 */
	public void compositeTraceTo(GlobalState start, GlobalState end) {
		List<String> compositeTrace = new ArrayList<String>();		
		List<GsTransition> marked = new ArrayList<GsTransition>();
		
		compositeTraceToRec(start, end, compositeTrace, marked);
	}
	
	/**
	 * Generates the list of composite traces, by making a recursive depth-first search.
	 * Circles are avoided, by marking the transitions which have already been taken. 
	 * @param globalState the global state that is currently being evaluated.
	 * @param end the global state where the composite trace stops.
	 * @param compositeTrace the list of already calculated composite traces.
	 * @param marked the list of transitions which have already been visited.
	 */
	public void compositeTraceToRec(GlobalState globalState, GlobalState end, List<String> compositeTrace, List<GsTransition> marked) {
		if(globalState.equals(end) && !compositeTrace.isEmpty()) {
			List<String> trace_add = new ArrayList<String>(compositeTrace);
			compositeTraceList.add(trace_add);
		}
		
		for(GsTransition t : globalState.getTransitions()) {
			if(!marked.contains(t)) {
				compositeTrace = new ArrayList<String>(compositeTrace);
				if(!t.isFaultFlag())
					compositeTrace.add(t.getOperation().getName());
				else
					compositeTrace.add("Fault-Symbol");
				marked = new ArrayList<GsTransition>(marked);
				marked.add(t);
				
				compositeTraceToRec(t.getTargetState(), end, compositeTrace, marked);
				
				marked.remove(marked.size()-1);
				compositeTrace.remove(compositeTrace.size()-1);				
			}					
		}
		return;
	}
	
	public List<Operation> getOperations() {
		if(this.operations != null) {
			return this.operations;
		}
		else {
			List<Operation> result = new ArrayList<Operation>();
			for(ManagementProtocol mp : this.getNodeList()) {
				for(Operation o : mp.getOperations()) {
					if(!result.contains(o))
						result.add(o);
				}
			}
			return result;
		}
	}
	
	public List<Requirement> getRequirements() {
		if(this.operations != null) {
			return this.requirements;
		}
		else {
			List<Requirement> result = new ArrayList<Requirement>();
			for(ManagementProtocol mp : this.getNodeList()) {
				for(Requirement r : mp.getRequirements()) {
					if(!result.contains(r))
						result.add(r);
				}
			}
			return result;
		}
	}
	
	public List<Capability> getCapabilities() {
		if(this.capabilities != null) {
			return this.capabilities;
		}
		else {
			List<Capability> result = new ArrayList<Capability>();
			for(ManagementProtocol mp : this.getNodeList()) {
				for(Capability c : mp.getCapabilities()) {
					if(!result.contains(c))
						result.add(c);
				}
			}
			return result;
		}
	}
	
	public GlobalState getInitialState() {
		return initialState;
	}

	public void setInitialState(GlobalState initialState) {
		this.initialState = initialState;
	}

	public List<ManagementProtocol> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<ManagementProtocol> nodeList) {
		this.nodeList = nodeList;
	}

	public List<List<String>> getCompositeTraceList() {
		return compositeTraceList;
	}

	public void setCompositeTraceList(List<List<String>> compositeTraceList) {
		this.compositeTraceList = compositeTraceList;
	}

	public List<GlobalState> getGlobalStates() {
		return globalStates;
	}

	public void setGlobalStates(List<GlobalState> globalStates) {
		this.globalStates = globalStates;
	}

	public List<GsTransition> getTransitionList() {
		return transitionList;
	}

	public void setTransitionList(List<GsTransition> transitionList) {
		this.transitionList = transitionList;
	}

}
