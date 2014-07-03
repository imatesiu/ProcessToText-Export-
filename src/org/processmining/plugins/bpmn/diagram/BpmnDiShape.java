package org.processmining.plugins.bpmn.diagram;

import java.util.Collection;

import org.processmining.plugins.bpmn.Bpmn;
import org.processmining.plugins.bpmn.BpmnElement;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDiShape extends BpmnElement {

	private String bpmnElement;
	private BpmnDcBounds bounds;
	
	public BpmnDiShape(String tag) {
		super(tag);			
	}
	
	public BpmnDiShape(String tag, String bpmnElement, BpmnDcBounds bounds) {
		super(tag);		
		this.bpmnElement = bpmnElement;
		this.bounds = bounds;	
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "bpmnElement");
		if (value != null) {
			bpmnElement = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (bpmnElement != null) {
			s += exportAttribute("bpmnElement", bpmnElement);
		}
		return s;
	}
	
	/**
	 * Exports all elements.
	 */
	protected String exportElements() {
		String s = "";
		if (bounds != null) {
			s += bounds.exportElement();
		}
		return s;
	}

	public void addElement(Collection<String> elements) {
		elements.add(bpmnElement);
	}
}