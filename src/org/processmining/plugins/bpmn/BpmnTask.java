package org.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public class BpmnTask extends BpmnIncomingOutgoing {

	private String startQuantity;
	private String completionQuantity;
	private String isForCompensation;
	
	public BpmnTask(String tag) {
		super(tag);
		
		startQuantity = null;
		completionQuantity = null;
		isForCompensation = null;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "startQuantity");
		if (value != null) {
			startQuantity = value;
		}
		value = xpp.getAttributeValue(null, "completionQuantity");
		if (value != null) {
			completionQuantity = value;
		}
		value = xpp.getAttributeValue(null, "isForCompensation");
		if (value != null) {
			isForCompensation = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (startQuantity != null) {
			s += exportAttribute("startQuantity", startQuantity);
		}
		if (completionQuantity != null) {
			s += exportAttribute("completionQuantity", completionQuantity);
		}
		if (isForCompensation != null) {
			s += exportAttribute("isForCompensation", isForCompensation);
		}
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane) {
		id2node.put(id, diagram.addActivity(name, false, false, false, false, false, lane));
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
		if (elements.contains(id)) {
			Activity activity = diagram.addActivity(name, false, false, false, false, false, lane);
			activity.getAttributeMap().put("Original id", id);
			id2node.put(id, activity);
		}
	}
}
