package org.processmining.models.graphbased.directed.bpmn.elements;


import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;






//import org.processmining.framework.util.ui.scalableview.VerticalLabelUI;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.shapes.Decorated;
import org.processmining.models.shapes.Rectangle;

public class Swimlane extends BPMNNode implements  ContainingDirectedGraphNode {
	//com.jgraph.layout.hierarchical.JGraphHierarchicalLayout, interHierarchySpacing is decreased from 60 to 5. Thus the space between swimlanes decreawsed.
	//org.processmining.models.jgraph.renderers.ProMGroupShapeRenderer, Rectangle handle dimensions changed from 0,0,20,20 to 0,0,10,10.
	protected final static int COLLAPSED_WIDTH = 80;
	protected final static int COLLAPSED_HEIGHT = 40;

	protected final static int EXPANDED_WIDTH = 1000;
	protected final static int EXPANDED_HEIGHT = 250;

	public static final int PADDINGFROMBOXTOTEXT = 5;
	public static final int TEXTWIDTH = 20;
	private final Set<ContainableDirectedGraphElement> children;
	private SwimlaneType type;
	
	// A reference to a resource
	private String partitionElement;
	
	public Swimlane(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, Swimlane parentSwimlane) {
		super(bpmndiagram, parentSwimlane);
		children = new HashSet<ContainableDirectedGraphElement>();
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle(false));
		getAttributeMap().put(AttributeMap.SQUAREBB, false);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		
	}


	public Swimlane(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, Swimlane parentSwimlane, SwimlaneType type) {
		this(bpmndiagram,label, parentSwimlane);
		this.type = type;
	}

	public Set<ContainableDirectedGraphElement> getChildren() {
		return children;
	}

	public void addChild(ContainableDirectedGraphElement child) {
		children.add(child);
		if(child instanceof Swimlane) {
		//	getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.NORTH);
		}
	}

	
	
	public SwimlaneType getSwimlaneType() {
		return type;
	}

	
	
	public void setPartitionElement(String partitionElement) {
		this.partitionElement = partitionElement;
	}
	
	public String getPartitionElement() {
		return partitionElement;
	}


	@Override
	public Dimension getCollapsedSize() {
		// TODO Auto-generated method stub
		return null;
	}


	


	
}


