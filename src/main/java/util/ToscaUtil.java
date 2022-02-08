package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mp.Capability;
import mp.FaultHandling;
import mp.ManagementProtocol;
import mp.Operation;
import mp.Requirement;
import mp.State;

public class ToscaUtil {
	
	/**
	 * Fetches a management protocol from a given file in the tosca format.
	 */
	public ToscaUtil() {
		super();
	}
	
	/**
	 * Fetches a management protocol from a given file in the tosca format.
	 * @param input the input path to the file in tosca format.
	 * @return the fetched management protocol.
	 */
	public static ManagementProtocol fetchToMp(String input){

		ManagementProtocol mp = null;
		String mpName = null;
		try {
	         File inputFile = new File(input);
	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         doc.getDocumentElement().normalize();
	         
	         // get name
	         
	         NodeList nameNode = doc.getElementsByTagName("tosca:NodeType");
	         Node node = nameNode.item(0);
	            if (node.getNodeType() == Node.ELEMENT_NODE) {
	            	Element element = (Element) node;	            	
	            	String name = element.getAttribute("name");
	            	//System.out.println(name);
	            	mpName = name;
	            }
	         
	         // fetch capabilities
	         
	         List<Capability> capList = new ArrayList<Capability>();
	         NodeList capNode = doc.getElementsByTagName("tosca:CapabilityDefinition");         
	         for (int temp = 0; temp < capNode.getLength(); temp++) {
	            Node nNode = capNode.item(temp);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	Element element = (Element) nNode;	            	
	            	String name = element.getAttribute("name");
	            	//System.out.println(name);
	            	capList.add(new Capability(name));
	            }
	         }
	         
	         // fetch requirements
	         
	         List<Requirement> reqList = new ArrayList<Requirement>();
	         NodeList reqNode = doc.getElementsByTagName("tosca:RequirementDefinition");         
	         for (int temp = 0; temp < reqNode.getLength(); temp++) {
	            Node nNode = reqNode.item(temp);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	Element element = (Element) nNode;	            	
	            	String name = element.getAttribute("name");
	            	//System.out.println(name);
	            	reqList.add(new Requirement(name));
	            }
	         }
	         
	         // fetch states
	         
	         List<State> stateList = new ArrayList<State>();
	         NodeList stateNode = doc.getElementsByTagName("tosca:InstanceState");         
	         for (int temp = 0; temp < stateNode.getLength(); temp++) {
	        	State state = null;
	            Node nNode = stateNode.item(temp);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	Element element = (Element) nNode;	            	
	            	String name = element.getAttribute("state");
	            	state = new State(name);    
	            	
	            	Node offer_node = element.getElementsByTagName("Offers").item(0);	            	
	            	if (offer_node != null) {
	            		Element offer_element = (Element) offer_node;
		            	NodeList cap_nodes = offer_element.getElementsByTagName("Capability");
		            	for(int i=0; i<cap_nodes.getLength(); i++) {
		            		Node cap_node = cap_nodes.item(i);
		            		Element cap_element = (Element) cap_node;	 
		            		state.addCapability(new Capability(cap_element.getAttribute("name")));	            	
		            	}
	            	}
	            	Node rely_node = element.getElementsByTagName("ReliesOn").item(0);	            	
	            	if (rely_node != null) {
	            		Element rely_element = (Element) rely_node;
		            	NodeList req_nodes = rely_element.getElementsByTagName("Requirement");
		            	for(int i=0; i<req_nodes.getLength(); i++) {
		            		Node req_node = req_nodes.item(i);
		            		Element req_element = (Element) req_node;	 
		            		state.addRequirement(new Requirement(req_element.getAttribute("name")));	            	
		            	}
	            	}
	            }
	            stateList.add(state);
	         }
	         
	         // fetch operations
	         
	         List<Operation> opList = new ArrayList<Operation>();
	         NodeList opNode = doc.getElementsByTagName("tosca:Operation");         
	         for (int temp = 0; temp < opNode.getLength(); temp++) {
	            Node nNode = opNode.item(temp);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	Element element = (Element) nNode;	            	
	            	String name = element.getAttribute("name");
	            	opList.add(new Operation(name));
	            }
	         }
	         
	         // fetch initial state
	         State init_state = null;
	         Node init_node = doc.getElementsByTagName("InitialState").item(0); 
	         Element element = (Element) init_node;
	         String init_name = element.getAttribute("state");
	         for(int i=0; i<stateList.size();i++) {
	        	 if(stateList.get(i).getName().equals(init_name)) {
	        		 init_state = stateList.get(i);
	        	 }
	         }
	         
	         mp = new ManagementProtocol(stateList, reqList, capList, opList, init_state, null);
	         
	         // fetch transitions
	         
	         NodeList transitionNode = doc.getElementsByTagName("Transition");         
	         for (int temp = 0; temp < transitionNode.getLength(); temp++) {
	            Node nNode = transitionNode.item(temp);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	Element trans_element = (Element) nNode;	            	
	            	String source = trans_element.getAttribute("sourceState");
	            	String target = trans_element.getAttribute("targetState");
	            	String operation = trans_element.getAttribute("operationName");
	            	
	            	List<Capability> cap_list = new ArrayList<Capability>();
	            	Node offer_node = trans_element.getElementsByTagName("Offers").item(0);	            	
	            	if (offer_node != null) {
	            		Element offer_element = (Element) offer_node;
		            	NodeList cap_nodes = offer_element.getElementsByTagName("Capability");
		            	for(int i=0; i<cap_nodes.getLength(); i++) {
		            		Node cap_node = cap_nodes.item(i);
		            		Element cap_element = (Element) cap_node;	 
		            		cap_list.add(new Capability(cap_element.getAttribute("name")));	            	
		            	}
	            	}
	            	List<Requirement> req_list = new ArrayList<Requirement>();
	            	Node rely_node = trans_element.getElementsByTagName("ReliesOn").item(0);	            	
	            	if (rely_node != null) {
	            		Element rely_element = (Element) rely_node;
		            	NodeList req_nodes = rely_element.getElementsByTagName("Requirement");
		            	for(int i=0; i<req_nodes.getLength(); i++) {
		            		Node req_node = req_nodes.item(i);
		            		Element req_element = (Element) req_node;	 
		            		req_list.add(new Requirement(req_element.getAttribute("name")));	            	
		            	}
	            	}
	            	
	            	// extra loop for crash state
	            	
	            	boolean found_s = false;
	            	boolean found_t = false;
	            	for(int i=0; i<mp.getStates().size(); i++) {
	            		if(mp.getStates().get(i).getName().equals(source))
	            			found_s = true;
	            		if(mp.getStates().get(i).getName().equals(target))
	            			found_t = true;
	            	}
	            	if(!found_s)
	            		mp.getStates().add(new State(source));
	            	if(!found_t)
	            		mp.getStates().add(new State(target));
	            	
	            	boolean found_o = false;
	            	for(int i=0; i<mp.getOperations().size();i++) {
	            		if(mp.getOperations().get(i).getName().equals(operation))
	            			found_o = true;
	            	}
	            	if(!found_o)
	            		mp.getOperations().add(new Operation(operation));
	            	
	            	
	            	State sourceState = null;
	            	for(State src : stateList) {
	            		if(src.getName().equals(source))
	            			sourceState = src;
	            	}
	            	State targetState = null;
	            	for(State trg : stateList) {
	            		if(trg.getName().equals(target))
	            			targetState = trg;
	            	}
	            	mp.addTransition(new Operation(operation), sourceState, targetState, req_list, cap_list);
	            }
	         }
	         
	         // fetch fault-handlers
	         
	         List<FaultHandling> fhList = new ArrayList<FaultHandling>();
	         NodeList fhNode = doc.getElementsByTagName("FaultHandler");         
	         for (int temp = 0; temp < fhNode.getLength(); temp++) {
	            Node nNode = fhNode.item(temp);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	element = (Element) nNode;	            	
	            	String source = element.getAttribute("sourceState");
	            	String target = element.getAttribute("targetState");
	            	State sourceState = null;
	            	for(State src : stateList) {
	            		if(src.getName().equals(source))
	            			sourceState = src;
	            	}
	            	State faultState = null;
	            	for(State trg : stateList) {
	            		if(trg.getName().equals(target))
	            			faultState = trg;
	            	}
	            	fhList.add(new FaultHandling(sourceState, faultState));
	            }
	         }
	         
	         mp.setFaultHandler(fhList);
	         
	         mp.setName(mpName);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		
		return mp;
	}
}
