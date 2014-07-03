package org.processmining.plugins.bpmn;

import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;

public class BpmnEndEvent extends BpmnEvent {

	public BpmnEndEvent(String tag) {
		super(tag, EventType.END);
	}
}
