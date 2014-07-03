package org.processmining.models.graphbased.directed.bpmn;

import java.util.Collection;
import java.util.Set;

import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.processmining.models.graphbased.directed.bpmn.elements.MessageFlow;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;

public interface BPMNDiagram extends DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> {

	String getLabel();

	//Activities
	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed);

	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed, SubProcess parentSubProcess);

	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed, Swimlane parentSwimlane);

	Activity removeActivity(Activity activity);

	Collection<Activity> getActivities();
	
	Collection<Activity> getActivities(Swimlane pool);

	//SubProcesses
	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, SubProcess parentSubProcess);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, Swimlane parentSwimlane);

	Activity removeSubProcess(SubProcess subprocess);

	Collection<SubProcess> getSubProcesses();
	
	Collection<SubProcess> getSubProcesses(Swimlane pool);

	//Events
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Activity exceptionFor);

	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, Activity exceptionFor);

	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Swimlane parentSwimlane, Activity exceptionFor);

	Event removeEvent(Event event);

	Collection<Event> getEvents();
	
	Collection<Event> getEvents(Swimlane pool);

	//Gateways
	Gateway addGateway(String label, GatewayType gatewayType);

	Gateway addGateway(String label, GatewayType gatewayType, SubProcess parentSubProcess);

	Gateway addGateway(String label, GatewayType gatewayType, Swimlane parentSwimlane);

	Gateway removeGateway(Gateway gateway);

	Collection<Gateway> getGateways();
	
	Collection<Gateway> getGateways(Swimlane pool);

	//Flows
	Flow addFlow(BPMNNode source, BPMNNode target, String label);

	Flow addFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label);

	Flow addFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label);

	Set<Flow> getFlows();
	
	Set<Flow> getFlows(Swimlane pool);

	//MessageFlows
	MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, String label);

	MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label);

	MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label);

	Set<MessageFlow> getMessageFlows();

	/**
	 * @deprecated use {@link  addSwimlane(String label, Swimlane parentSwimlane, 
	 * SwimlaneType type)} instead
	 */
	@Deprecated
	Swimlane addSwimlane(String label, Swimlane parentSwimlane);
	
	Swimlane addSwimlane(String label, Swimlane parentSwimlane, SwimlaneType type);

	Swimlane removeSwimlane(Swimlane swimlane);

	Collection<Swimlane> getSwimlanes();
	
	Collection<Swimlane> getPools();
	
	Collection<Swimlane> getLanes(Swimlane pool);
}
