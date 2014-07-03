package org.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Association;
import org.processmining.models.graphbased.directed.bpmn.elements.DataObject;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.xmlpull.v1.XmlPullParser;

public class BpmnProcess extends BpmnIdName {

	private Collection<BpmnStartEvent> startEvents;
	private Collection<BpmnTask> tasks;
	private Collection<BpmnSubProcess> subprocess;
	private Collection<BpmnExclusiveGateway> exclusiveGateways;
	private Collection<BpmnParallelGateway> parallelGateways;
	private Collection<BpmnSequenceFlow> sequenceFlows;
	private Collection<BpmnEndEvent> endEvents;
	private Collection<BpmnIntermediateEvent> intermediateEvents;
	private Collection<BpmnDataObject> dataObjects;
	private Collection<BpmnDataObjectReference> dataObjectsRefs;
	private Collection<BpmnTextAnnotation> textAnnotations;
	private Collection<BpmnAssociation> associations;
	private BpmnLaneSet laneSet;

	public BpmnProcess(String tag) {
		super(tag);

		startEvents = new HashSet<BpmnStartEvent>();
		tasks = new HashSet<BpmnTask>();
		exclusiveGateways = new HashSet<BpmnExclusiveGateway>();
		parallelGateways = new HashSet<BpmnParallelGateway>();
		sequenceFlows = new HashSet<BpmnSequenceFlow>();
		endEvents = new HashSet<BpmnEndEvent>();
		intermediateEvents = new HashSet<BpmnIntermediateEvent>();
		subprocess = new HashSet<BpmnSubProcess>();
		dataObjects = new HashSet<BpmnDataObject>();
		dataObjectsRefs = new HashSet<BpmnDataObjectReference>();
		textAnnotations = new HashSet<BpmnTextAnnotation>();
		associations = new HashSet<BpmnAssociation>();
	}

	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
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
		} else if (xpp.getName().equals("intermediateCatchEvent")) {
			BpmnIntermediateEvent intCatchEvent = new BpmnIntermediateEvent("intermediateCatchEvent", 
					EventUse.CATCH);
			intCatchEvent.importElement(xpp, bpmn);
			intermediateEvents.add(intCatchEvent);
			return true;		
		} else if (xpp.getName().equals("boundaryEvent")) {
			BpmnIntermediateEvent intCatchEvent = new BpmnIntermediateEvent("boundaryEvent", 
					EventUse.CATCH);
			intCatchEvent.importElement(xpp, bpmn);
			intermediateEvents.add(intCatchEvent);
			return true;
		} else if (xpp.getName().equals("intermediateThrowEvent")) {
			BpmnIntermediateEvent intThrowEvent = new BpmnIntermediateEvent("intermediateThrowEvent", 
					EventUse.THROW);
			intThrowEvent.importElement(xpp, bpmn);
			intermediateEvents.add(intThrowEvent);
			return true;
		} else if (xpp.getName().equals("dataObject")) {
			BpmnDataObject dataObject = new BpmnDataObject("dataObject");
			dataObject.importElement(xpp, bpmn);
			dataObjects.add(dataObject);
			return true;
		} else if (xpp.getName().equals("dataObjectReference")) {
			BpmnDataObjectReference dataObjectRef = new BpmnDataObjectReference("dataObjectReference");
			dataObjectRef.importElement(xpp, bpmn);
			dataObjectsRefs.add(dataObjectRef);
			return true;
		} else if (xpp.getName().equals("laneSet")) {
			laneSet = new BpmnLaneSet("laneSet");
			laneSet.importElement(xpp, bpmn);
			return true;
		} else if (xpp.getName().equals("textAnnotation")) {
			BpmnTextAnnotation textAnnotation = new BpmnTextAnnotation("textAnnotation");
			textAnnotation.importElement(xpp, bpmn);
			textAnnotations.add(textAnnotation);
			return true;
		} else if (xpp.getName().equals("association")) {
			BpmnAssociation association = new BpmnAssociation("association");
			association.importElement(xpp, bpmn);
			associations.add(association);
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
		if (laneSet != null) {
			s += laneSet.exportElement();
		}
		for (BpmnStartEvent startEvent : startEvents) {
			s += startEvent.exportElement();
		}
		for (BpmnEndEvent endEvent : endEvents) {
				s += endEvent.exportElement();
		}
		for (BpmnIntermediateEvent intermediateEvent : intermediateEvents) {
			s += intermediateEvent.exportElement();
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
		for (BpmnDataObject dataObject : dataObjects) {
			s += dataObject.exportElement();
		}
		for (BpmnDataObjectReference dataObjectRef : dataObjectsRefs) {
			s += dataObjectRef.exportElement();
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			s += textAnnotation.exportElement();
		}
		for (BpmnAssociation association : associations) {
			s += association.exportElement();
		}
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		Swimlane lane = id2lane.get(id);
		if(laneSet != null) {
			laneSet.unmarshall(diagram, id2node,  id2lane, lane);
		}
		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, id2node, retrieveParentSwimlane(startEvent, id2lane));
		}
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, id2node, retrieveParentSwimlane(task, id2lane));
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, id2node, retrieveParentSwimlane(exclusiveGateway, id2lane));
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, id2node, retrieveParentSwimlane(parallelGateway, id2lane));
		}
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, id2node, retrieveParentSwimlane(endEvent, id2lane));
		}
		for (BpmnSubProcess subProcess : subprocess) {
			subProcess.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnSubProcess subPro : subprocess) {
			subPro.unmarshall(diagram, id2node, retrieveParentSwimlane(subPro, id2lane));
		}
		for (BpmnIntermediateEvent intermediateEvent : intermediateEvents) {
			intermediateEvent.unmarshall(diagram, id2node, retrieveParentSwimlane(intermediateEvent, id2lane));
		}
		for (BpmnDataObjectReference dataObjectRef : dataObjectsRefs) {
			dataObjectRef.unmarshall(diagram, id2node);
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			sequenceFlow.unmarshall(diagram, id2node, lane);
		}
		for (BpmnTask task : tasks) {
			task.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			textAnnotation.unmarshall(diagram, id2node);
		}
		for (BpmnAssociation association : associations) {
			association.unmarshall(diagram, id2node);
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node,
			Map<String, Swimlane> id2lane) {
		Swimlane lane = id2lane.get(id);
		if(laneSet != null) {
			laneSet.unmarshall(diagram, elements, id2node, id2lane, lane);
		}
		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(startEvent, id2lane));
		}
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(task, id2lane));
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(exclusiveGateway, id2lane));
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(parallelGateway, id2lane));
		}	
		for (BpmnSubProcess subPro : subprocess) {
			subPro.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(subPro, id2lane));
		}
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(endEvent, id2lane));
		}
		for (BpmnIntermediateEvent intEvent : intermediateEvents) {
			intEvent.unmarshall(diagram, id2node, retrieveParentSwimlane(intEvent, id2lane));
		}
		for (BpmnDataObjectReference dataObjectRef : dataObjectsRefs) {
			dataObjectRef.unmarshall(diagram, id2node);
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			sequenceFlow.unmarshall(diagram, elements, id2node, lane);
		}
		for (BpmnTask task : tasks) {
			task.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnSubProcess subProcess : subprocess) {
			subProcess.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			textAnnotation.unmarshall(diagram, elements, id2node);
		}
		for (BpmnAssociation association : associations) {
			association.unmarshall(diagram, elements, id2node);
		}
	}
	
	private Swimlane retrieveParentSwimlane(BpmnId bpmnFlow, Map<String, Swimlane> id2lane) {
		if(laneSet != null) {
			Collection<BpmnLane> lanes = laneSet.getAllChildLanes();	
			for(BpmnLane bpmnLane : lanes) {
				for(BpmnText flowNodeRef : bpmnLane.getFlowNodeRef()) {
					if((flowNodeRef.getText() != null) 
							&& flowNodeRef.getText().equals(bpmnFlow.getId())) {
						return id2lane.get(bpmnLane.getId());
					}
				}
			}
		}
		return id2lane.get(id);
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
		marshallEvents(diagram, pool);
		
		// Marshall activities
		marshallActivities(diagram, pool);

		// Marshall gateways
		marshallGateways(diagram, pool);
		
		// Marshall dataObjects
		marshallDataObjects(diagram, pool);
		
		// Marshall SubProcess
		marshallSubProcesses(diagram, pool);
		
		// Marshall control flows
		marshallControlFlows(diagram, pool);
		
		// Marshall lane set
		marshallLaneSet(diagram, pool);
		
		// Marshall artifacts
		marshallArtifacts(diagram, pool);
		
		return !(startEvents.isEmpty() && endEvents.isEmpty() && tasks.isEmpty() 
					&& exclusiveGateways.isEmpty() && parallelGateways.isEmpty() 
					&& textAnnotations.isEmpty() && associations.isEmpty() && (laneSet == null));
	}
	
	private void marshallEvents(BPMNDiagram diagram, Swimlane pool) {		
		for(Event event : diagram.getEvents(pool)) {
			if(event.getEventType() == EventType.START) {
				BpmnStartEvent startEvent = new BpmnStartEvent("startEvent");	
				startEvent.marshall(event);
				startEvents.add(startEvent);			
			} else if(event.getEventType() == EventType.END) {
				BpmnEndEvent endEvent = new BpmnEndEvent("endEvent");	
				endEvent.marshall(event);
				endEvents.add(endEvent);
			} else if(event.getEventType() == EventType.INTERMEDIATE) {
				BpmnIntermediateEvent intermediateEvent = null;
				if(event.getEventUse() == EventUse.CATCH) {
					if(event.getBoundingNode() != null) {
						intermediateEvent = new BpmnIntermediateEvent("boundaryEvent",
								EventUse.CATCH);
					} else {
					intermediateEvent = new BpmnIntermediateEvent("intermediateCatchEvent",
							EventUse.CATCH);
					}
				} else {
					intermediateEvent = new BpmnIntermediateEvent("intermediateThrowEvent",
							EventUse.THROW);
				}
				intermediateEvent.marshall(event);
				intermediateEvents.add(intermediateEvent);
			}
		}
	}
	
	private void marshallActivities(BPMNDiagram diagram, Swimlane pool) {
		for (Activity activity : diagram.getActivities(pool)) {
			BpmnTask task = new BpmnTask("task");
			task.marshall(activity, diagram);
			tasks.add(task);
		}
	}
	
	private void marshallGateways(BPMNDiagram diagram, Swimlane pool) {
		for (Gateway gateway : diagram.getGateways(pool)) {
			if(gateway.getGatewayType() == GatewayType.DATABASED) {
				BpmnExclusiveGateway exclusiveGateway = new BpmnExclusiveGateway("exclusiveGateway");
				exclusiveGateway.marshall(diagram, gateway);
				exclusiveGateways.add(exclusiveGateway);
				
			} else if(gateway.getGatewayType() == GatewayType.PARALLEL) {
				BpmnParallelGateway parallelGateway = new BpmnParallelGateway("parallelGateway");
				parallelGateway.marshall(diagram, gateway);
				parallelGateways.add(parallelGateway);
			}
		}
	}
	
	private void marshallDataObjects(BPMNDiagram diagram, Swimlane pool) {
		for (DataObject dataObject : diagram.getDataObjects()) {		
			BpmnDataObject bpmnDataObject = new BpmnDataObject("dataObject");
			bpmnDataObject.marshall(dataObject);
			bpmnDataObject.setId("dataobj_" + dataObject.getId().toString().replace(' ', '_'));
			dataObjects.add(bpmnDataObject);
			
			BpmnDataObjectReference bpmnDataObjectRef = new BpmnDataObjectReference("dataObjectReference");
			bpmnDataObjectRef.marshall(dataObject);
			dataObjectsRefs.add(bpmnDataObjectRef);
		}
	}
	
	private void marshallSubProcesses(BPMNDiagram diagram, Swimlane pool) {
		for (SubProcess sub : diagram.getSubProcesses(pool)) {
			if (sub.getParentSubProcess() == null) {
				BpmnSubProcess subProcess = new BpmnSubProcess("subProcess");
				subProcess.marshall(sub, diagram);
				subprocess.add(subProcess);
			}
		}
	}
	
	private void marshallControlFlows(BPMNDiagram diagram, Swimlane pool) {
		for (Flow flow : diagram.getFlows(pool)) {
			BpmnSequenceFlow sequenceFlow = new BpmnSequenceFlow("sequenceFlow");
			sequenceFlow.marshall(flow);
			sequenceFlows.add(sequenceFlow);
		}
	}
	
	private void marshallLaneSet(BPMNDiagram diagram, Swimlane pool) {
		if(diagram.getLanes(pool).size() > 0) {
			laneSet = new BpmnLaneSet("laneSet");
			laneSet.marshall(diagram, pool);
		}
	}
	
	private void marshallArtifacts(BPMNDiagram diagram, Swimlane pool) {
		for (TextAnnotation textAnnotation : diagram.getTextAnnotations(pool)) {
			BpmnTextAnnotation bpmnTextAnnotation = new BpmnTextAnnotation("textAnnotation");
			bpmnTextAnnotation.marshall(textAnnotation);
			textAnnotations.add(bpmnTextAnnotation);
		}
		for (Association association : diagram.getAssociations(pool)) {
			BpmnAssociation bpmnAssociation = new BpmnAssociation("association");
			bpmnAssociation.marshall(association);
			associations.add(bpmnAssociation);
		}
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
		intermediateEvents.clear();
		dataObjects.clear();
		textAnnotations.clear();
		associations.clear();
	}
}
