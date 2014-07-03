package org.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public class BpmnParallelGateway extends BpmnIncomingOutgoing {

	private String gatewayDirection;
	
	public BpmnParallelGateway(String tag) {
		super(tag);
		
		gatewayDirection = null;
	}
	
	public void marshall(BPMNDiagram diagram, Gateway gateway){
		super.marshall(gateway);
		int incoming = 0;
		int outgoing = 0;
		for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e: diagram.getEdges()){
			if(e.getTarget().equals(gateway)){
				BpmnIncoming in = new BpmnIncoming("incoming");
				in.setText(e.getEdgeID().toString().replace(" ", "_"));
				incomings.add(in);
				incoming++;
			}
			if(e.getSource().equals(gateway)){
				BpmnOutgoing out = new BpmnOutgoing("outgoing");
				out.setText(e.getEdgeID().toString().replace(" ", "_"));
				outgoings.add(out);
				outgoing++;
			}
		}
		if(incoming > 1 && outgoing > 1){
			gatewayDirection = "Mixed";
		}
		else if(incoming == 1 && outgoing > 1){
			gatewayDirection = "Diverging";
		}
		else if(incoming > 1 && outgoing == 1){
			gatewayDirection = "Converging";
		}
		else{
			gatewayDirection = "Unspecified";
		}
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
		Gateway gateway = diagram.addGateway(name, GatewayType.PARALLEL, lane);
		gateway.getAttributeMap().put("Original id", id);
		id2node.put(id, gateway);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
		if (elements.contains(id)) {
			Gateway gateway = diagram.addGateway(name, GatewayType.PARALLEL, lane);
			gateway.getAttributeMap().put("Original id", id);
			id2node.put(id, gateway);
		}
	}
}
