package org.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public class BpmnProcess extends BpmnIdName {

	private Collection<BpmnStartEvent> startEvents;
	private Collection<BpmnTask> tasks;
	private Collection<BpmnSubProcess> subprocess;
	private Collection<BpmnExclusiveGateway> exclusiveGateways;
	private Collection<BpmnParallelGateway> parallelGateways;
	private Collection<BpmnSequenceFlow> sequenceFlows;
	private Collection<BpmnEndEvent> endEvents;

	public BpmnProcess(String tag) {
		super(tag);

		startEvents = new HashSet<BpmnStartEvent>();
		tasks = new HashSet<BpmnTask>();
		exclusiveGateways = new HashSet<BpmnExclusiveGateway>();
		parallelGateways = new HashSet<BpmnParallelGateway>();
		sequenceFlows = new HashSet<BpmnSequenceFlow>();
		endEvents = new HashSet<BpmnEndEvent>();
		subprocess = new HashSet<BpmnSubProcess>();
	}
	
	public BpmnProcess(String tag, BPMNDiagram diagram) {
		super(tag);

		startEvents = new HashSet<BpmnStartEvent>();
		tasks = new HashSet<BpmnTask>();
		exclusiveGateways = new HashSet<BpmnExclusiveGateway>();
		parallelGateways = new HashSet<BpmnParallelGateway>();
		sequenceFlows = new HashSet<BpmnSequenceFlow>();
		endEvents = new HashSet<BpmnEndEvent>();
		subprocess = new HashSet<BpmnSubProcess>();
	}

	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("laneSet")) {
			
			return true;
		} else if (xpp.getName().equals("startEvent")) {
			BpmnStartEvent startEvent = new BpmnStartEvent("startEvent");
			startEvent.importElement(xpp, bpmn);
			startEvents.add(startEvent);
			return true;
		} else if (xpp.getName().equals("task")) {
			BpmnTask task = new BpmnTask("task");
			task.importElement(xpp, bpmn);
			tasks.add(task);
			return true;
		} else if (xpp.getName().equals("exclusiveGateway")) {
			BpmnExclusiveGateway exclusiveGateway = new BpmnExclusiveGateway("exclusiveGateway");
			exclusiveGateway.importElement(xpp, bpmn);
			exclusiveGateways.add(exclusiveGateway);
			return true;
		} else if (xpp.getName().equals("parallelGateway")) {
			BpmnParallelGateway parallelGateway = new BpmnParallelGateway("parallelGateway");
			parallelGateway.importElement(xpp, bpmn);
			parallelGateways.add(parallelGateway);
			return true;
		} else if (xpp.getName().equals("subProcess")) {
			BpmnSubProcess subPro = new BpmnSubProcess("subProcess");
			subPro.importElement(xpp, bpmn);
			subprocess.add(subPro);
			return true;
		} else if (xpp.getName().equals("sequenceFlow")) {
			BpmnSequenceFlow sequenceFlow = new BpmnSequenceFlow("sequenceFlow");
			sequenceFlow.importElement(xpp, bpmn);
			sequenceFlows.add(sequenceFlow);
			return true;
		} else if (xpp.getName().equals("endEvent")) {
			BpmnEndEvent endEvent = new BpmnEndEvent("endEvent");
			endEvent.importElement(xpp, bpmn);
			endEvents.add(endEvent);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (BpmnStartEvent startEvent : startEvents) {
			s += startEvent.exportElement();
		}
		for (BpmnTask task : tasks) {
			s += task.exportElement();
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			s += exclusiveGateway.exportElement();
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			s += parallelGateway.exportElement();
		}
		for (BpmnSubProcess subPro : subprocess) {
			s += subPro.exportElement();
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			s += sequenceFlow.exportElement();
		}
		for (BpmnEndEvent endEvent : endEvents) {
			s += endEvent.exportElement();
		}
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		Swimlane lane = id2lane.get(id);
		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, id2node, lane);
		}
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, id2node, lane);
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, id2node, lane);
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, id2node, lane);
		}
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, id2node, lane);
		}
		for (BpmnSubProcess subPro : subprocess) {
			subPro.unmarshall(diagram, id2node, lane);
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			sequenceFlow.unmarshall(diagram, id2node, lane);
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node,
			Map<String, Swimlane> id2lane) {
		Swimlane lane = id2lane.get(id);
		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, elements, id2node, lane);
		}
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, elements, id2node, lane);
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, elements, id2node, lane);
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, elements, id2node, lane);
		}
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, elements, id2node, lane);
		}
		for (BpmnSubProcess subPro : subprocess) {
			subPro.unmarshall(diagram, elements, id2node, lane);
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			sequenceFlow.unmarshall(diagram, elements, id2node, lane);
		}
	}
	
	/**
	 * Constructs a process model from diagram
	 * 
	 * @param diagram
	 * @param pool
	 * @return "true" if at least one element has been added
	 */
	public boolean marshall(BPMNDiagram diagram, Swimlane pool) {
		
		clearAll();
		
		// Marshall events
		for(Event event : diagram.getEvents(pool)) {
			if(event.getEventType() == EventType.START) {
				BpmnStartEvent startEvent = new BpmnStartEvent("startEvent");	
				startEvent.marshall(event);
				startEvents.add(startEvent);
				
			} else if(event.getEventType() == EventType.END) {
				BpmnEndEvent endEvent = new BpmnEndEvent("endEvent");	
				endEvent.marshall(event);
				endEvents.add(endEvent);
			}
		}
		
		// Marshall activities
		for (Activity activity : diagram.getActivities(pool)) {
			BpmnTask task = new BpmnTask("task");
			task.marshall(activity);
			tasks.add(task);
		}

		// Marshall gateways
		for (Gateway gateway : diagram.getGateways(pool)) {
			if(gateway.getGatewayType() == GatewayType.DATABASED) {
				BpmnExclusiveGateway exclusiveGateway = new BpmnExclusiveGateway("exclusiveGateway");
				exclusiveGateway.marshall(gateway);
				exclusiveGateways.add(exclusiveGateway);
				
			} else if(gateway.getGatewayType() == GatewayType.PARALLEL) {
				BpmnParallelGateway parallelGateway = new BpmnParallelGateway("parallelGateway");
				parallelGateway.marshall(gateway);
				parallelGateways.add(parallelGateway);
			}
		}
		
		// Marshall SubProcess
		for (SubProcess sub : diagram.getSubProcesses(pool)) {
			BpmnSubProcess subProcess = new BpmnSubProcess("subProcess");
			subProcess.marshall(sub);
			subprocess.add(subProcess);
		}
		
		// Marshall control flows
		for (Flow flow : diagram.getFlows(pool)) {
			BpmnSequenceFlow sequenceFlow = new BpmnSequenceFlow("sequenceFlow");
			sequenceFlow.marshall(flow);
			sequenceFlows.add(sequenceFlow);
		}
		
		return !(startEvents.isEmpty() && endEvents.isEmpty() && tasks.isEmpty() 
					&& exclusiveGateways.isEmpty() && parallelGateways.isEmpty());
	}
	
	/**
	 * Clear all process contents
	 * 
	 */
	private void clearAll() {
		
		startEvents.clear();
		tasks.clear();
		exclusiveGateways.clear();
		parallelGateways.clear();
		sequenceFlows.clear();
		subprocess.clear();
		endEvents.clear();
	}
}
