package org.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.shapes.Decorated;


public class SubProcess extends Activity implements Decorated,ContainingDirectedGraphNode {

	private final Set<ContainableDirectedGraphElement> children;

	public SubProcess(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean looped, boolean adhoc, boolean compensation, boolean multiinstance, boolean collapsed) {
		super(bpmndiagram, label, looped, adhoc, compensation, multiinstance, collapsed);
		children = new HashSet<ContainableDirectedGraphElement>();
		fillAttributes();
	}

	public SubProcess(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean looped, boolean adhoc, boolean compensation, boolean multiinstance,
			boolean collapsed, SubProcess parentSubProcess) {
		super(bpmndiagram, label, looped, adhoc, compensation, multiinstance, collapsed, parentSubProcess);
		children = new HashSet<ContainableDirectedGraphElement>();
		fillAttributes();
	}

	public SubProcess(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean looped, boolean adhoc, boolean compensation, boolean multiinstance,
			boolean collapsed, Swimlane parentSwimlane) {
		super(bpmndiagram, label, looped, adhoc, compensation, multiinstance, collapsed, parentSwimlane);
		children = new HashSet<ContainableDirectedGraphElement>();
		fillAttributes();
	}

	/**
	 * 
	 */
	private void fillAttributes() {
		getAttributeMap().put(AttributeMap.SIZE, null);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(.95F, .95F, .95F, .95F));
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.TOP);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
	}

	public Set<ContainableDirectedGraphElement> getChildren() {
		return children;
	}

	public void addChild(ContainableDirectedGraphElement child) {
		children.add(child);
	}

	public Dimension getCollapsedSize() {
		return new Dimension(stdWidth, stdHeight);
	}
	
	@Override
	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		super.decorate(g2d, x, y, width, height);
	}
}
