package org.processmining.plugins.bpmn;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;




import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.MessageFlow;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.elements.ProMGraphEdge;
import org.processmining.models.jgraph.views.JGraphPortView;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.plugins.bpmn.diagram.BpmnDcBounds;
import org.processmining.plugins.bpmn.diagram.BpmnDiEdge;
import org.processmining.plugins.bpmn.diagram.BpmnDiPlane;
import org.processmining.plugins.bpmn.diagram.BpmnDiShape;
import org.processmining.plugins.bpmn.diagram.BpmnDiWaypoint;
import org.processmining.plugins.bpmn.diagram.BpmnDiagram;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDefinitions extends BpmnElement {

	private Collection<BpmnProcess> processes;
	private Collection<BpmnCollaboration> collaborations;
	private Collection<BpmnMessage> messages;
	private Collection<BpmnDiagram> diagrams;
	
	public BpmnDefinitions(String tag) {
		super(tag);
		processes = new HashSet<BpmnProcess>();
		collaborations = new HashSet<BpmnCollaboration>();
		messages = new HashSet<BpmnMessage>();
		diagrams = new HashSet<BpmnDiagram>();
	}
	
	public BpmnDefinitions(String tag, BpmnDefinitionsBuilder builder) {
		super(tag);
		processes = builder.processes;
		collaborations = builder.collaborations;
		messages = new HashSet<BpmnMessage>();
		diagrams = builder.diagrams;
	}
	
	/**
	 * Builds a BPMN model (BpmnDefinitions) from BPMN diagram
	 *
	 * @author Anna Kalenkova
	 * Sep 22, 2013
	 */
	public static class BpmnDefinitionsBuilder {

		private Collection<BpmnProcess> processes;
		private Collection<BpmnCollaboration> collaborations;
		private Collection<BpmnDiagram> diagrams;

		public BpmnDefinitionsBuilder(UIPluginContext context, BPMNDiagram diagram) {
			processes = new HashSet<BpmnProcess>();
			collaborations = new HashSet<BpmnCollaboration>();
			diagrams = new HashSet<BpmnDiagram>();

			buildFromDiagram(context, diagram);
		}

		/**
		 * Build BpmnDefinitions from BPMNDiagram (BPMN picture)
		 * 
		 * @param context
		 * @param diagram
		 */
		private void buildFromDiagram(UIPluginContext context, BPMNDiagram diagram) {
			BpmnCollaboration bpmnCollaboration = new BpmnCollaboration("collaboration");
			bpmnCollaboration.setId("col_" + bpmnCollaboration.hashCode());

			// Build pools and participants
			for (Swimlane pool : diagram.getPools()) {
				BpmnParticipant bpmnParticipant = new BpmnParticipant("participant");
				bpmnParticipant.id = pool.getId().toString().replace(' ', '_');
				bpmnParticipant.name = pool.getLabel();
				// If pool is not a "black box", create a process
				if (!pool.getChildren().isEmpty()) {
					BpmnProcess bpmnProcess = new BpmnProcess("process");
					bpmnProcess.marshall(diagram, pool);
					bpmnProcess.setId("proc_" + bpmnProcess.hashCode());
					processes.add(bpmnProcess);
					bpmnParticipant.setProcessRef(bpmnProcess.getId());
				}
				bpmnCollaboration.addParticipant(bpmnParticipant);
			}

			// Discover "internal" process
			BpmnProcess intBpmnProcess = new BpmnProcess("process");
			intBpmnProcess.setId("proc_" + intBpmnProcess.hashCode());
			// If there are elements without parent pool, add process
			if (intBpmnProcess.marshall(diagram, null)) {
				processes.add(intBpmnProcess);
			}

			// Build message flows
			Set<MessageFlow> messageFlows = diagram.getMessageFlows();
			for (MessageFlow messageFlow : messageFlows) {
				BpmnMessageFlow bpmnMessageFlow = new BpmnMessageFlow("messageFlow");
				bpmnMessageFlow.marshall(messageFlow);
				bpmnCollaboration.addMessageFlow(bpmnMessageFlow);
			}
			
			// Build graphics info
			BpmnDiagram bpmnDiagram = new BpmnDiagram("bpmndi:BPMNDiagram");
			bpmnDiagram.setId("id_" + diagram.hashCode());
			BpmnDiPlane plane = new BpmnDiPlane("bpmndi:BPMNPlane");
			if (diagram.getPools().size() > 0) {
				collaborations.add(bpmnCollaboration);
				plane.setBpmnElement(bpmnCollaboration.id);
			}
			else {
				plane.setBpmnElement(intBpmnProcess.id);
			}
			bpmnDiagram.addPlane(plane);
			fillGraphicsInfo(context, diagram, bpmnDiagram, plane);

			diagrams.add(bpmnDiagram);
		}

		/**
		 * Fill graphics info
		 * 
		 * @param context
		 * @param diagram
		 * @param bpmnDiagram
		 * @param plane
		 */
		private void fillGraphicsInfo(UIPluginContext context, BPMNDiagram diagram,
				BpmnDiagram bpmnDiagram, BpmnDiPlane plane) {

			// Construct graph info
			ProMJGraphPanel graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(context, diagram);
			ProMGraphModel graphModel = graphPanel.getGraph().getModel();

			for (Object o : graphModel.getRoots()) {
				if (o instanceof ProMGraphCell) {
					ProMGraphCell graphCell = (ProMGraphCell) o;
					addCellGraphicsInfo(graphCell, plane);
				}
				if (o instanceof ProMGraphEdge) {
					ProMGraphEdge graphEdge = (ProMGraphEdge) o;
					addEdgeGraphInfo(graphEdge, plane);
				}
			}
		}
		
		/**
		 * Retrieve graphics info from graphCell
		 * 
		 * @param graphCell
		 * @param plane
		 */
		private void addCellGraphicsInfo(ProMGraphCell graphCell, BpmnDiPlane plane) {
			DirectedGraphNode graphNode = graphCell.getNode();
			// Create BPMNShape
			String bpmnElement = graphNode.getId().toString().replace(' ', '_');
			Rectangle2D rectangle = graphCell.getView().getBounds();
			double x = rectangle.getX();
			double y = rectangle.getY();
			double width = rectangle.getWidth();
			double height = rectangle.getHeight();
			BpmnDcBounds bounds = new BpmnDcBounds("dc:Bounds", x, y, width, height);
			BpmnDiShape shape = new BpmnDiShape("bpmndi:BPMNShape", bpmnElement, bounds);
			plane.addShape(shape);
			addChildGrapInfo(graphCell, plane);
		}
		
		/**
		 * Retrieve graphics info from graphEdge
		 * 
		 * @param graphEdge
		 * @param plane
		 */
		private void addEdgeGraphInfo(ProMGraphEdge graphEdge, BpmnDiPlane plane) {
			@SuppressWarnings("rawtypes")
			BPMNEdge bpmnEdge = (BPMNEdge)graphEdge.getEdge();
			// Create BPMNEdge
			String bpmnElement = bpmnEdge.getEdgeID().toString().replace(' ', '_');

			BpmnDiEdge edge = new BpmnDiEdge("bpmndi:BPMNEdge", bpmnElement);
			for (Object point : graphEdge.getView().getPoints()) {
				Point2D point2D;
				if(point instanceof JGraphPortView) {
					JGraphPortView portView = (JGraphPortView) point;
					point2D = portView.getLocation();
				} else if(point instanceof Point2D) {
					point2D = (Point2D)point;
				} else {
					continue;
				}
				double x = point2D.getX();
				double y = point2D.getY();
				BpmnDiWaypoint waypoint = new BpmnDiWaypoint("di:waypoint", x, y);
				edge.addWaypoint(waypoint);
			}
			plane.addEdge(edge);
		}
		
		/**
		 * Retrieve graphics info for child elements
		 * 
		 * @param graphCell
		 * @param plane
		 */
		private void addChildGrapInfo(ProMGraphCell graphCell, BpmnDiPlane plane){
			for (Object o : graphCell.getChildren()) {
				if (o instanceof ProMGraphCell) {
					ProMGraphCell childGraphCell = (ProMGraphCell) o;
					addCellGraphicsInfo(childGraphCell, plane);
				}
				if (o instanceof ProMGraphEdge) {
					ProMGraphEdge childGraphEdge = (ProMGraphEdge) o;
					addEdgeGraphInfo(childGraphEdge, plane);
				}
			}
		}
	}
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("process")) {
			BpmnProcess process = new BpmnProcess("process");
			process.importElement(xpp, bpmn);
			processes.add(process);
			return true;
		} else if (xpp.getName().equals("collaboration")) {
			BpmnCollaboration collaboration = new BpmnCollaboration("collaboration");
			collaboration.importElement(xpp, bpmn);
			collaborations.add(collaboration);
			return true;
		} else if (xpp.getName().equals("message")) {
			BpmnMessage message = new BpmnMessage("message");
			message.importElement(xpp, bpmn);
			messages.add(message);
			return true;
		} else if (xpp.getName().equals("BPMNDiagram")) {
			BpmnDiagram diagram = new BpmnDiagram("BPMNDiagram");
			diagram.importElement(xpp, bpmn);
			diagrams.add(diagram);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	public String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (BpmnProcess process : processes) {
			s += process.exportElement();
		}
		for (BpmnCollaboration collaboration : collaborations) {
			s += collaboration.exportElement();
		}
		for (BpmnMessage message : messages) {
			s += message.exportElement();
		}
		for (BpmnDiagram diagram : diagrams) {
			s += diagram.exportElement();
		}
		return s;
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallParticipants(diagram, id2node, id2lane);
		}
		for (BpmnProcess process : processes) {
			process.unmarshall(diagram, id2node, id2lane);
		}
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallMessageFlows(diagram, id2node);
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallParticipants(diagram, elements, id2node, id2lane);
		}
		for (BpmnProcess process : processes) {
			process.unmarshall(diagram, elements, id2node, id2lane);
		}
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallMessageFlows(diagram, elements, id2node);
		}
	}
	
	public Collection<BpmnDiagram> getDiagrams() {
		return diagrams;
	}
}
