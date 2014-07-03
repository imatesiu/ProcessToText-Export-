package org.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public class BpmnExclusiveGateway extends BpmnIncomingOutgoing {

	private String gatewayDirection;
	
	public BpmnExclusiveGateway(String tag) {
		super(tag);
		
		gatewayDirection = null;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "gatewayDirection");
		if (value != null) {
			gatewayDirection = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (gatewayDirection != null) {
			s += exportAttribute("gatewayDirection", gatewayDirection);
		}
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane) {
		Gateway gateway = diagram.addGateway(name, GatewayType.DATABASED, lane);
		gateway.getAttributeMap().put("Original id", id);
		id2node.put(id, gateway);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
		if (elements.contains(id)) {
			Gateway gateway = diagram.addGateway(name, GatewayType.DATABASED, lane);
			gateway.getAttributeMap().put("Original id", id);
			id2node.put(id, gateway);
		}
	}
}
