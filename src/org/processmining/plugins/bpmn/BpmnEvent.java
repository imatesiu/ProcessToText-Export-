package org.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public class BpmnEvent extends BpmnIncomingOutgoing {

	private String isInterrupting;
	private String parallelMultiple;
	private EventType eventType;
	
	public BpmnEvent(String tag, EventType eventType) {
		super(tag);
		
		isInterrupting = null;
		parallelMultiple = null;
		this.eventType = eventType;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "isInterrupting");
		if (value != null) {
			isInterrupting = value;
		}
		value = xpp.getAttributeValue(null, "parallelMultiple");
		if (value != null) {
			parallelMultiple = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (isInterrupting != null) {
			s += exportAttribute("isInterrupting", isInterrupting);
		}
		if (parallelMultiple != null) {
			s += exportAttribute("parallelMultiple", parallelMultiple);
		}
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane) {
		id2node.put(id, diagram.addEvent(name, eventType, null, EventUse.CATCH, lane, null));
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
		if (elements.contains(id)) {
			Event startEvent = diagram.addEvent(name, eventType, null, EventUse.CATCH, lane, null);
			startEvent.getAttributeMap().put("Original id", id);
			id2node.put(id, startEvent);
		}
	}
	
	public void marshall(Event bpmnEvent) {
		super.marshall(bpmnEvent);
			
		eventType = bpmnEvent.getEventType();
		isInterrupting = bpmnEvent.isInterrupting();
		parallelMultiple = bpmnEvent.getParallelMultiple();
	}
}
