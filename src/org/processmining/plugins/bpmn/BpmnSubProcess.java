package org.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
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

public class BpmnSubProcess extends BpmnIncomingOutgoing {

	private String triggeredByEvent;
	private BpmnMultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics;

	private Collection<BpmnStartEvent> startEvents;
	private Collection<BpmnTask> tasks;
	private Collection<BpmnExclusiveGateway> exclusiveGateways;
	private Collection<BpmnParallelGateway> parallelGateways;
	private Collection<BpmnSequenceFlow> sequenceFlows;
	private Collection<BpmnEndEvent> endEvents;

	public BpmnSubProcess(String tag) {
		super(tag);

		triggeredByEvent = null;

		startEvents = new HashSet<BpmnStartEvent>();
		tasks = new HashSet<BpmnTask>();
		exclusiveGateways = new HashSet<BpmnExclusiveGateway>();
		parallelGateways = new HashSet<BpmnParallelGateway>();
		sequenceFlows = new HashSet<BpmnSequenceFlow>();
		endEvents = new HashSet<BpmnEndEvent>();

	}

	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("multiInstanceLoopCharacteristics")) {
			multiInstanceLoopCharacteristics = new BpmnMultiInstanceLoopCharacteristics(
					"multiInstanceLoopCharacteristics");
			multiInstanceLoopCharacteristics.importElement(xpp, bpmn);
			return true;
		}
		if (xpp.getName().equals("startEvent")) {
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
			BpmnExclusiveGateway exclusiveGateway = new BpmnExclusiveGateway(
					"exclusiveGateway");
			exclusiveGateway.importElement(xpp, bpmn);
			exclusiveGateways.add(exclusiveGateway);
			return true;
		} else if (xpp.getName().equals("parallelGateway")) {
			BpmnParallelGateway parallelGateway = new BpmnParallelGateway(
					"parallelGateway");
			parallelGateway.importElement(xpp, bpmn);
			parallelGateways.add(parallelGateway);
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
		if (multiInstanceLoopCharacteristics != null) {
			s += multiInstanceLoopCharacteristics.exportElement();
		}
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
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			s += sequenceFlow.exportElement();
		}
		for (BpmnEndEvent endEvent : endEvents) {
			s += endEvent.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "triggeredByEvent");
		if (value != null) {
			triggeredByEvent = value;
		}

	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (triggeredByEvent != null) {
			s += exportAttribute("triggeredByEvent", triggeredByEvent);
		}

		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node,
			Swimlane lane) {
		SubProcess subp;
		if (multiInstanceLoopCharacteristics != null) {
			subp = diagram.addSubProcess(name, false, false, false, true, true,
					lane);
			id2node.put(id, subp);
		} else {
			subp = diagram.addSubProcess(name, false, false, false, false,
					false, lane);
			id2node.put(id, subp);
		}

		Map<String, BPMNNode> id2nodeSubProcess = new HashMap<String, BPMNNode>();

		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, id2nodeSubProcess, lane);
		}
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, id2nodeSubProcess, lane);
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, id2nodeSubProcess, lane);
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, id2nodeSubProcess, lane);
		}
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, id2nodeSubProcess, lane);
		}

		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			Flow flow = sequenceFlow.unmarshall(diagram, id2nodeSubProcess,
					lane);
			flow.setParent(subp);
			subp.addChild(flow);
		}
		for (String NameNode : id2nodeSubProcess.keySet()) {
			BPMNNode node = id2nodeSubProcess.get(NameNode);
			node.setParentSubprocess(subp);
			subp.addChild(node);

		}

	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements,
			Map<String, BPMNNode> id2node, Swimlane lane) {
		SubProcess subProcess = null;
		if (elements.contains(id)) {
			if (multiInstanceLoopCharacteristics != null) {
				subProcess = diagram.addSubProcess(name, false, false, false,
						true, true, lane);
				subProcess.getAttributeMap().put("Original id", id);
				id2node.put(id, subProcess);
			} else {
				subProcess = diagram.addSubProcess(name, false, false, false,
						false, false, lane);
				subProcess.getAttributeMap().put("Original id", id);
				id2node.put(id, subProcess);

			}
		}
		Map<String, BPMNNode> id2nodeSubProcess = new HashMap<String, BPMNNode>();

		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, elements, id2nodeSubProcess, lane);
		}
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, elements, id2nodeSubProcess, lane);
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, elements, id2nodeSubProcess,
					lane);
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, elements, id2nodeSubProcess,
					lane);
		}
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, elements, id2nodeSubProcess, lane);
		}

		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			Flow flow = sequenceFlow.unmarshall(diagram, elements,
					id2nodeSubProcess, lane);
			if (subProcess != null & flow != null) {
				flow.setParent(subProcess);
				subProcess.addChild(flow);
			}
		}
		for (String NameNode : id2nodeSubProcess.keySet()) {
			BPMNNode node = id2nodeSubProcess.get(NameNode);
			if (subProcess != null) {
				node.setParentSubprocess(subProcess);
				subProcess.addChild(node);
			}

		}

	}

	public void marshall(SubProcess subProcess) {
		super.marshall(subProcess);
		
		for (ContainableDirectedGraphElement child : subProcess.getChildren()) {
			
			// Marshall child event
			if (child instanceof Event) {
				if (((Event) child).getEventType() == EventType.START) {
					BpmnStartEvent startEvent = new BpmnStartEvent("startEvent");
					startEvent.marshall((Event) child);
					startEvents.add(startEvent);

				} else if (((Event) child).getEventType() == EventType.END) {
					BpmnEndEvent endEvent = new BpmnEndEvent("endEvent");
					endEvent.marshall((Event) child);
					endEvents.add(endEvent);
				}
			}
			
			// Marshall child activity
			if (child instanceof Activity) {
				BpmnTask task = new BpmnTask("task");
				task.marshall((Activity) child);
				tasks.add(task);
			}
			
			// Marshall child gateway
			if (child instanceof Gateway) {
				if (((Gateway) child).getGatewayType() == GatewayType.DATABASED) {
					BpmnExclusiveGateway exclusiveGateway = new BpmnExclusiveGateway("exclusiveGateway");
					exclusiveGateway.marshall((Gateway) child);
					exclusiveGateways.add(exclusiveGateway);

				} else if (((Gateway) child).getGatewayType() == GatewayType.PARALLEL) {
					BpmnParallelGateway parallelGateway = new BpmnParallelGateway("parallelGateway");
					parallelGateway.marshall((Gateway) child);
					parallelGateways.add(parallelGateway);
				}
			}
			
			// Marshall child control flow
			if (child instanceof Flow) {
				BpmnSequenceFlow sequenceFlow = new BpmnSequenceFlow("sequenceFlow");
				sequenceFlow.marshall((Flow) child);
				sequenceFlows.add(sequenceFlow);
			}
		}
	}
}
