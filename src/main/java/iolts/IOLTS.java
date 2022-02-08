package iolts;

import java.util.ArrayList;
import java.util.List;

import composite.CompositeApplication;
import composite.GlobalState;
import composite.GsTransition;
import mp.Capability;
import mp.FaultHandling;
import mp.ManagementProtocol;
import mp.Operation;
import mp.Requirement;
import mp.State;
import mp.Transition;

public class IOLTS {
	
	private Configuration initialConf;
	
	private List<Configuration> configurations;
	
	private ManagementProtocol mp;
	
	private CompositeApplication ca;
	
	private List<List<Action>> straceList;
	
	/**
	 * Generates an IOLTS semantics from a given management protocol.
	 * @param mp the management protocol from which the IOLTS semantics is to be calculated from.
	 */
	public IOLTS(ManagementProtocol mp) {		
		this.mp = mp;
		
		this.configurations = new ArrayList<Configuration>();
		
		for(State s : mp.getStates()) {
			Configuration c = new Configuration(s.getName(), true);			
			ReqSetAction rsa = new ReqSetAction(s.getRequirements());			
			CapSetAction csa = new CapSetAction(s.getCapabilities());
			
			c.addTransition(rsa, c);
			c.addTransition(csa, c);

			this.configurations.add(c);
		}
		
		int opCounter = 0;
		for(Transition t : mp.getTransitions()) {
			Configuration c_t = new Configuration(t.getOperation().getName() + opCounter);
			ReqSetAction rsa = new ReqSetAction(t.getConditions());
			CapSetAction csa = new CapSetAction(t.getCapabilities());
			
			c_t.addTransition(rsa, c_t);
			c_t.addTransition(csa ,c_t);
			
			Configuration source = configurations.stream()
					.filter(configuration -> t.getSourceState().getName().equals(configuration.getName()))
					.findAny()
					.orElse(null);
			Configuration target = configurations.stream()
					.filter(configuration -> t.getTargetState().getName().equals(configuration.getName()))
					.findAny()
					.orElse(null);
			
			OpInvAction in = new OpInvAction(t.getOperation());
			OpTerAction out = new OpTerAction(t.getOperation());
			
			source.addTransition(in, c_t);
			c_t.addTransition(out, target);
			
			this.configurations.add(c_t);
			opCounter++;
		}
		
		int fCounter = 0;
		for(FaultHandling f : mp.getFaultHandler()) {			
			
			Configuration c_f = new Configuration("failing" + fCounter);
			ReqSetAction rsa = new ReqSetAction(f.getFaultState().getRequirements());
			
			Configuration source = configurations.stream()
					.filter(configuration -> f.getSourceState().getName().equals(configuration.getName()))
					.findAny()
					.orElse(null);
			
			source.addTransition(rsa, c_f);
			
			Configuration fault = configurations.stream()
					.filter(configuration -> f.getFaultState().getName().equals(configuration.getName()))
					.findAny()
					.orElse(null);
			
			FaultAction fa = new FaultAction();
			
			c_f.addTransition(fa, fault);
			
			this.configurations.add(c_f);
			
			fCounter++;
		}
		
		Configuration init = configurations.stream()
				.filter(configuration -> mp.getInitialState().getName().equals(configuration.getName()))
				.findAny()
				.orElse(null);
		
		this.initialConf = init;
	}
	
	/**
	 * Generates an IOLTS semantics from a given composite application.
	 * @param ca the composite application from which the IOLTS semantics is to be calculated from.
	 */
	public IOLTS(CompositeApplication ca) {
		this.ca = ca;
		
		this.configurations = new ArrayList<Configuration>();
		
		for(GlobalState gs : ca.getGlobalStates()) {
			Configuration c = new Configuration(gs.getName(), true);
			ReqSetAction rsa = new ReqSetAction(gs.getRequirements());
			CapSetAction csa = new CapSetAction(gs.getCapabilities());
			
			c.addTransition(rsa, c);
			c.addTransition(csa, c);

			this.configurations.add(c);
		}
		int fCounter = 0;
		int opCounter = 0;
		for(GsTransition gt : ca.getTransitionList()) {
			if(!gt.isFaultFlag()) {
				Configuration c_t = new Configuration(gt.getOperation().getName() + opCounter);
				ReqSetAction rsa = new ReqSetAction(gt.getConditions());
				CapSetAction csa = new CapSetAction(gt.getCapabilities());
				
				c_t.addTransition(rsa, c_t);
				c_t.addTransition(csa ,c_t);
				
				Configuration source = configurations.stream()
						.filter(configuration -> gt.getSourceState().getName().equals(configuration.getName()))
						.findAny()
						.orElse(null);
				Configuration target = configurations.stream()
						.filter(configuration -> gt.getTargetState().getName().equals(configuration.getName()))
						.findAny()
						.orElse(null);
				
				OpInvAction in = new OpInvAction(gt.getOperation());
				OpTerAction out = new OpTerAction(gt.getOperation());
				
				source.addTransition(in, c_t);
				c_t.addTransition(out, target);
				
				this.configurations.add(c_t);
				opCounter++;
			}
			else {
				Configuration c_f = new Configuration("failing" + fCounter);
				ReqSetAction rsa = new ReqSetAction(gt.getSourceState().getRequirements());
				
				Configuration source = configurations.stream()
						.filter(configuration -> gt.getSourceState().getName().equals(configuration.getName()))
						.findAny()
						.orElse(null);
				
				source.addTransition(rsa, c_f);
				
				Configuration fault = configurations.stream()
						.filter(configuration -> gt.getTargetState().getName().equals(configuration.getName()))
						.findAny()
						.orElse(null);
				
				FaultAction fa = new FaultAction();
				
				c_f.addTransition(fa, fault);
				
				this.configurations.add(c_f);
				
				fCounter++;
			}
			
		}
		
		Configuration init = configurations.stream()
				.filter(configuration -> ca.getInitialState().getName().equals(configuration.getName()))
				.findAny()
				.orElse(null);
		
		this.initialConf = init;
	}
	
	/**
	 * Makes the IOLTS semantics input enabled.
	 */
	public void makeInputEnabled() {		
		Configuration sink = new Configuration("sink");

		for(Configuration c : configurations) {
			//if(c.isState()) {				
				List<Operation> inputOperations = new ArrayList<Operation>();
				List<Requirement> inputRequirements = new ArrayList<Requirement>();
				for(IOLTSTransition t : c.getTransitions()) {					
					if(t.getAction() instanceof OpInvAction)
						inputOperations.add(((OpInvAction) t.getAction()).getOperation());
					if(t.getAction() instanceof ReqSetAction)
						inputRequirements.addAll(((ReqSetAction) t.getAction()).getRequirements());					
				}
				
				List<Operation> mp_op;
				List<Requirement> mp_req;
				
				if(mp != null) {
					mp_op = new ArrayList<Operation>(mp.getOperations());
					mp_req = new ArrayList<Requirement>(mp.getRequirements());
				}
				else {
					mp_op = new ArrayList<Operation>(ca.getOperations());
					mp_req = new ArrayList<Requirement>(ca.getRequirements());
				}
				
				mp_op.removeAll(inputOperations);
				mp_req.removeAll(inputRequirements);
				
				for(Operation o : mp_op) {
					OpInvAction in = new OpInvAction(o);
					c.addTransition(in, sink);
				}
				
				if(!mp_req.isEmpty()) {
					ReqSetAction rsa = new ReqSetAction(mp_req);
					c.addTransition(rsa, sink);
				}
			//}
		}
		
		// self looping fault transition
		FaultAction fault = new FaultAction();
		sink.addTransition(fault, sink);
		
		// self looping input transitions
		List<Requirement> mp_req;
		if(mp !=null)
			mp_req = new ArrayList<Requirement>(mp.getRequirements());
		else
			mp_req = new ArrayList<Requirement>(ca.getRequirements());
		
		ReqSetAction rsa = new ReqSetAction(mp_req);
		sink.addTransition(rsa, sink);
		
		List<Operation> mp_op;
		if(mp != null)
			mp_op = new ArrayList<Operation>(mp.getOperations());
		else
			mp_op = new ArrayList<Operation>(ca.getOperations());
		
		for(Operation o : mp_op) {
			OpInvAction in = new OpInvAction(o);
			sink.addTransition(in, sink);
		}
		
		this.configurations.add(sink);		
	}

	/**
	 * Generates a list of output actions from a list of configurations. 
	 * @param configurations the configurations from which the output actions are to be calculated from.
	 * @return the output actions of a list of configurations. 
	 */
	public List<OutputAction> out(List<Configuration> configurations) {
		List<OutputAction> result = new ArrayList<OutputAction>();
		for(Configuration c : configurations) {
			boolean delta = false;
			boolean f = false;
			List<Capability> caps = new ArrayList<Capability>();
			for(OutputAction a : out(c)) {
				if(a instanceof FaultAction)
					f = true;
				else if(a instanceof QuiescenceAction)
					delta = true;
				else if(a instanceof CapSetAction)
					caps.addAll(((CapSetAction) a).getCapabilities());
					
			}
			if(!caps.isEmpty())
				result.add(new CapSetAction(caps));			
			if(f)
				result.add(new FaultAction());						
			if(delta)
				result.add(new QuiescenceAction());
		}
		return result;
	}
	
	/**
	 * Generates the list of output actions from a single configuration.
	 * @param configuration the configuration from which the output actions are the be calculated from.
	 * @return the output actions of a configuration.
	 */
	public List<OutputAction> out(Configuration configuration) {
		List <OutputAction> result = new ArrayList<OutputAction>();
		
		if(!configuration.isQuiescent()) {
			for(IOLTSTransition t : configuration.getTransitions()) {
				if(t.getAction() instanceof CapSetAction)
					result.add(((OutputAction) t.getAction()));
				else if(t.getAction() instanceof FaultAction)
					result.add(((OutputAction) t.getAction()));
			}
		}		
		else {
			QuiescenceAction q = new QuiescenceAction();
			result.add(q);
		}		
		return result;
	}
	
	/**
	 * Checks the quiescence property by setting a flag in the configurations to true.
	 */
	public void checkQuiescence() {
		// if configurations provide no capabilities they are set as quiescent
		for(Configuration c : configurations) {			
			boolean hasCap = false;
			for(IOLTSTransition t : c.getTransitions()) {
				if(t.getAction() instanceof CapSetAction) {
					CapSetAction csa = (CapSetAction) t.getAction();
					if(!csa.getCapabilities().isEmpty())
						hasCap = true;
				}					
			}
			if(!hasCap  && !c.getName().equals("sink"))
				c.setQuiescent(true);
		}
	}
	
	/**
	 * Generates a list of configurations which are reachable from a start configuration a strace and the gamma parameter.
	 * @param configuration the configuration from which reachable configurations are to be calculated from. 
	 * @param strace the strace that consists of a list of actions.
	 * @param gamma the parameter as discussed in the paper.
	 * @return a list of configurations that can be reached by the strace.
	 */
	public List<Configuration> gammaReachability(Configuration configuration, List<Action> strace, boolean gamma){
		Configuration currentConf = configuration;
		List<Configuration> result = new ArrayList<Configuration>();
		
		if(currentConf.getName().equals("sink")) {
			result.add(currentConf);
			return result;
		}
		if(!strace.isEmpty()) {
			Action a = strace.get(0);		
			if(a instanceof ReqSetAction) {
				if(gamma) {
					Configuration nextConf = null;
					for(IOLTSTransition t : currentConf.getTransitions()) {
						if(t.getAction() instanceof ReqSetAction) {
							if(equalToR(((ReqSetAction) t.getAction()).getRequirements(), ((ReqSetAction) a).getRequirements()) && equalToR(((ReqSetAction) a).getRequirements(), ((ReqSetAction) t.getAction()).getRequirements())) {
								nextConf = t.getTargetConf();
								break;
							}
						}
					}
					if(nextConf == null)
						return result;
					strace.remove(0);
					result.addAll(gammaReachability(nextConf, strace, gamma));					
					return result;
				}
				else {
					Configuration nextConf = null;
					for(IOLTSTransition t : currentConf.getTransitions()) {
						if(t.getAction() instanceof ReqSetAction) {
							if(equalToR(((ReqSetAction) t.getAction()).getRequirements(), ((ReqSetAction) a).getRequirements()) && !t.getTargetConf().getName().startsWith("failing")) {
								nextConf = t.getTargetConf();
								break;
							}
						}
					}
					if(nextConf == null)
						return result;
					strace.remove(0);
					result.addAll(gammaReachability(nextConf, strace, gamma));
					return result;
				}
			}
			else if(a instanceof CapSetAction){
				if(gamma) {
					Configuration nextConf = null;
					for(IOLTSTransition t : currentConf.getTransitions()) {
						if(t.getAction() instanceof CapSetAction) {
							if(equalToC(((CapSetAction) t.getAction()).getCapabilities(), ((CapSetAction) a).getCapabilities()) && equalToC(((CapSetAction) a).getCapabilities(), ((CapSetAction) t.getAction()).getCapabilities())) {
								nextConf = t.getTargetConf();
								break;
							}
						}
					}
					strace.remove(0);
					if(nextConf == null)
						return result;
					result.addAll(gammaReachability(nextConf, strace, gamma));					
					return result;
				}
				else {
					Configuration nextConf = null;
					for(IOLTSTransition t : currentConf.getTransitions()) {
						if(t.getAction() instanceof CapSetAction) {
							if(equalToC(((CapSetAction) a).getCapabilities(), ((CapSetAction) t.getAction()).getCapabilities()) && !((CapSetAction) t.getAction()).getCapabilities().isEmpty()) {
								nextConf = t.getTargetConf();
								break;
							}
						}
					}
					if(nextConf == null)
						return result;
					strace.remove(0);
					result.addAll(gammaReachability(nextConf, strace, gamma));					
					return result;
				}
			}
			else if(a instanceof OpInvAction){
				List<Configuration> nextConfs = new ArrayList<Configuration>();
				for(IOLTSTransition t : currentConf.getTransitions()) {
					if(t.getAction() instanceof OpInvAction) {
						if(((OpInvAction) t.getAction()).getOperation().getName().equals(((OpInvAction) a).getOperation().getName())) {
							nextConfs.add(t.getTargetConf());
						}
					}
				}
				strace.remove(0);
				if(nextConfs.isEmpty())
					return result;
				for(Configuration c : nextConfs) {
					 result.addAll(gammaReachability(c, strace, gamma));
				}
				return result;
			}
			else if(a instanceof OpTerAction){
				List<Configuration> nextConfs = new ArrayList<Configuration>();
				for(IOLTSTransition t : currentConf.getTransitions()) {
					if(t.getAction() instanceof OpTerAction) {
						if(((OpTerAction) t.getAction()).getOperation().getName().equals(((OpTerAction) a).getOperation().getName())) {
							nextConfs.add(t.getTargetConf());
						}
					}
				}
				strace.remove(0);
				if(nextConfs.isEmpty())
					return result;
				for(Configuration c : nextConfs) {
					 result.addAll(gammaReachability(c, strace, gamma));
				}
				return result;
			}
		}
		else {
			result.add(currentConf);
			return result;
		}
		return result;
	}
	
	/**
	 * Checks whether two lists of requirements contain the same elements.
	 * @param one a list of requirements.
	 * @param two a list of requirements.
	 * @return whether one and two are equal.
	 */
	private boolean equalToR(List<Requirement> one, List<Requirement> two) {		
		for(Requirement r : one) {
			boolean found = false;
			for(Requirement r2 : two) {
				if(r.equals(r2))
					found = true;
			}
			if(!found)
				return false;
		}
		return true;
	}
	
	/**
	 * Checks whether two lists of capabilities contain the same elements.
	 * @param one a list of capabilities.
	 * @param two a list of capabilities.
	 * @return whether one and two are equal.
	 */
	private boolean equalToC(List<Capability> one, List<Capability> two) {		
		for(Capability c : one) {
			boolean found = false;
			for(Capability c2 : two) {
				if(c.equals(c2))
					found = true;
			}
			if(!found)
				return false;
		}
		return true;
	}
	
	/**
	 * Generates the list of all possible straces from a start configuration.
	 * @param start the start configuration form which all possible straces are to be calculated.
	 */
	public void straceToAll(Configuration start) {
		this.straceList = new ArrayList<List<Action>>();
		for(Configuration c : this.configurations) {
				straceTo(start, c);
		}
	}

	/**
	 * Generates a list of all possible straces from a start configuration, by making a recursive depth-first search
	 * where this the the head.
	 * @param start the start configuration.
	 * @param end the configuration where the strace stops.
	 */
	public void straceTo(Configuration start, Configuration end){		
		List<Action> strace = new ArrayList<Action>();
		List<IOLTSTransition> marked = new ArrayList<IOLTSTransition>();
		
		straceToRec(start, end, strace, marked);
	}

	/**
	 * Generates a list of all possible straces from a start configuration, by making a recursive depth-first search.
	 * Circles are avoided, by marking the transitions which have already been taken.
	 * @param configuration the configuration that is currently being evaluated.
	 * @param end the configuration where the strace stops.
	 * @param strace the list of already calculated  straces.
	 * @param marked the list of transitions which have already been visited.
	 */
	public void straceToRec(Configuration configuration, Configuration end, List<Action> strace, List<IOLTSTransition> marked){
		if(configuration.getName().equals(end.getName()) && !strace.isEmpty()) {
			List<Action> strace_add = new ArrayList<Action>(strace);
			straceList.add(strace_add);
			return;
		}
		
		for(IOLTSTransition t : configuration.getTransitions()) {
			if(!marked.contains(t)) {
				if(!((t.getAction() instanceof CapSetAction && ((CapSetAction) t.getAction()).getCapabilities().isEmpty()) || (t.getAction() instanceof ReqSetAction && ((ReqSetAction) t.getAction()).getRequirements().isEmpty()))) {
					if(!t.getTargetConf().getName().startsWith("failing")) {
						if(!(t.getAction() instanceof FaultAction)) {							
							strace = new ArrayList<Action>(strace);
							strace.add(t.getAction());
							marked = new ArrayList<IOLTSTransition>(marked);
							marked.add(t);
							
							straceToRec(t.getTargetConf(), end, strace, marked);
							
							strace.remove(strace.size()-1);
						}
					}
				}
			}
		}
		return;
	}
	
	public Configuration getInitialConf() {
		return initialConf;
	}

	public void setInitialConf(Configuration initialConf) {
		this.initialConf = initialConf;
	}

	public List<Configuration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<Configuration> configurations) {
		this.configurations = configurations;
	}

	public ManagementProtocol getMp() {
		return mp;
	}

	public void setMp(ManagementProtocol mp) {
		this.mp = mp;
	}

	public List<List<Action>> getStraceList() {
		return straceList;
	}

	public void setStraceList(List<List<Action>> straceList) {
		this.straceList = straceList;
	}

}
