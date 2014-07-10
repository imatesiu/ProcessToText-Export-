import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.MessageFlow;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.plugins.bpmn.Bpmn;
import org.processmining.plugins.bpmn.diagram.BpmnDiagram;
import org.processmining.plugins.bpmn.parameters.BpmnSelectDiagramParameters;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import net.didion.jwnl.JWNLException;
import preprocessing.FormatConverter;
import sentencePlanning.DiscourseMarker;
import sentencePlanning.ReferringExpressionGenerator;
import sentencePlanning.SentenceAggregator;
import sentenceRealization.SurfaceRealizer;
import textPlanning.TextPlanner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.itextpdf.text.DocumentException;

import contentDetermination.labelAnalysis.EnglishLabelDeriver;
import contentDetermination.labelAnalysis.EnglishLabelHelper;
import dataModel.dsynt.DSynTConditionSentence;
import dataModel.dsynt.DSynTSentence;
import dataModel.intermediate.ConditionFragment;
import dataModel.intermediate.ExecutableFragment;
import dataModel.jsonIntermediate.JSONArc;
import dataModel.jsonIntermediate.JSONElem;
import dataModel.jsonIntermediate.JSONEvent;
import dataModel.jsonIntermediate.JSONGateway;
import dataModel.jsonIntermediate.JSONLane;
import dataModel.jsonIntermediate.JSONPool;
import dataModel.jsonIntermediate.JSONTask;
import dataModel.jsonReader.JSONReader;
import dataModel.jsonStructure.Doc;
import dataModel.process.Activity;
import dataModel.process.ActivityType;
import dataModel.process.Arc;
import dataModel.process.Element;
import dataModel.process.Event;
import dataModel.process.EventType;
import dataModel.process.Gateway;
import dataModel.process.GatewayType;
import dataModel.process.Lane;
import dataModel.process.Pool;
import dataModel.process.ProcessModel;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import edu.stanford.nlp.io.EncodingPrintWriter.out;

public class Main {

	private static EnglishLabelHelper lHelper;
	private static EnglishLabelDeriver lDeriver;

	/**
	 * Main function.
	 */
	public static void main(String[] args) throws Exception {

		String file = "/Users/isiu/Downloads/ProcessToText (Export)/BicycleManufacturing.json";
		String fileBP = "/Users/isiu/temp/test8.bpmn";// "/Users/isiu/github/prom_plugins/BPMNMeasures/tests/testfiles/Residency.bpmn";//"/Users/isiu/Dropbox/TPCS Share folder/TPCS/Modelli/BPMN/Export_TPCS_Complete_model.bpmn";
		// String file = "RigidTest.json";
		file = "/Users/isiu/Downloads/ProcessToText (Export)/RigidTest.json";
		// Set up label parsing classes
		lHelper = new EnglishLabelHelper();
		lDeriver = new EnglishLabelDeriver(lHelper);

		// Load and generate from JSON files in directory
		//createFromFile(file);
		//
			createFromFileFromBPMN(fileBP);
	}

	/**
	 * Function for generating text from a model. The according process model
	 * must be provided to the function.
	 */
	public static String toText(ProcessModel model, int counter)
			throws JWNLException, IOException, ClassNotFoundException,
			DocumentException {
		String imperativeRole = "";
		boolean imperative = false;

		// Annotate model
		model.annotateModel(0, lDeriver, lHelper);

		// Convert to RPST
		FormatConverter formatConverter = new FormatConverter();
		Process p = formatConverter.transformToRPSTFormat(model);
		RPST<ControlFlow, Node> rpst = new RPST<ControlFlow, Node>(p);

		// Convert to Text
		TextPlanner converter = new TextPlanner(rpst, model, lDeriver, lHelper,
				imperativeRole, imperative, false);
		converter.convertToText(rpst.getRoot(), 0);
		ArrayList<DSynTSentence> sentencePlan = converter.getSentencePlan();

		// Aggregation
		SentenceAggregator sentenceAggregator = new SentenceAggregator(lHelper);
		sentencePlan = sentenceAggregator.performRoleAggregation(sentencePlan,
				model);

		// Referring Expression
		ReferringExpressionGenerator refExpGenerator = new ReferringExpressionGenerator(
				lHelper);
		sentencePlan = refExpGenerator.insertReferringExpressions(sentencePlan,
				model, false);

		//System.out.print(sentencePlan);
		PrintArrayListDSynTSentence(sentencePlan);
		// Discourse Marker
		DiscourseMarker discourseMarker = new DiscourseMarker();
		sentencePlan = discourseMarker.insertSequenceConnectives(sentencePlan);

		PrintArrayListDSynTSentence(sentencePlan);

		// Realization
		SurfaceRealizer surfaceRealizer = new SurfaceRealizer();
		String surfaceText = surfaceRealizer.realizePlan(sentencePlan);

		// Cleaning
		if (imperative == true) {
			surfaceText = surfaceRealizer.cleanTextForImperativeStyle(
					surfaceText, imperativeRole, model.getLanes());
		}

		surfaceText = surfaceRealizer.postProcessText(surfaceText);
		return surfaceText;
	}

	private static void createFromFileFromBPMN(String file) throws IOException,
			Exception {

		InputStream is = new FileInputStream(file);
		Bpmn bpmn = importBpmnFromStream(is, file,
				Files.size(new File(file).toPath()));
		ProcessModel model = getProcessModel(bpmn);
		int counter = 0;
		// Multi Pool Model
		if (model.getPools().size() > 1) {
			long time = System.currentTimeMillis();
			System.out.println();
			System.out.print("The model contains " + model.getPools().size()
					+ " pools: ");
			int count = 0;
			for (String role : model.getPools()) {
				if (count > 0 && model.getPools().size() > 2) {
					System.out.print(", ");
				}
				if (count == model.getPools().size() - 1) {
					System.out.print(" and ");
				}
				System.out.print(role + " (" + (count + 1) + ")");
				count++;
			}

			HashMap<Integer, ProcessModel> newModels = model
					.getModelForEachPool();
			for (ProcessModel m : newModels.values()) {
				try {
					m.normalize();
					m.normalizeEndEvents();
				} catch (Exception e) {
					System.out.println("Error: Normalization impossible");
					e.printStackTrace();
				}
				String surfaceText = toText(m, counter);
				System.out.println(surfaceText.replaceAll(" process ", " "
						+ m.getPools().get(0) + " process "));
			}
		} else {
			try {
				model.normalize();
				model.normalizeEndEvents();
			} catch (Exception e) {
				System.out.println("Error: Normalization impossible");
				e.printStackTrace();
			}
			String surfaceText = toText(model, counter);
			System.out.println(surfaceText);
		}

	}

	private static void PrintArrayListDSynTSentence(ArrayList<DSynTSentence> AD) {
		String Out = "";
		for (DSynTSentence dSynTSentence : AD) {
			if(dSynTSentence instanceof DSynTConditionSentence){
				ConditionFragment cefrag = ((DSynTConditionSentence) dSynTSentence).getConditionFragment();
				ExecutableFragment eefrag = dSynTSentence.getExecutableFragment();
				Out += cefrag.getAction() + " " + cefrag.getAddition() + " "
						+ cefrag.getBo() + " ";
				Out += eefrag.getAction() + " " + eefrag.getAddition() + " "
						+ eefrag.getBo() + " ";
				
			}else{
			ExecutableFragment efrag = dSynTSentence.getExecutableFragment();
			Out += efrag.getAction() + " " + efrag.getAddition() + " "
					+ efrag.getBo() + " "+efrag.getAllMods()+" ";
			}
			String  f;
			
		}
		out.println("");
		out.println(Out);
	}

	private static BPMNDiagram BpmnSelectDiagram(Bpmn bpmn) {
		Collection<BpmnDiagram> cbpmn = bpmn.getDiagrams();
		String namebpd = "";
		for (BpmnDiagram bpmnDiagram : cbpmn) {
			// namebpd = bpmnDiagram.get
		}
		BpmnSelectDiagramParameters parameters = new BpmnSelectDiagramParameters();
		parameters.setDiagram(BpmnSelectDiagramParameters.NODIAGRAM);

		BPMNDiagram newDiagram = BPMNDiagramFactory.newBPMNDiagram(parameters
				.getDiagram().toString());
		Map<String, BPMNNode> id2node = new HashMap<String, BPMNNode>();
		Map<String, Swimlane> id2lane = new HashMap<String, Swimlane>();

		if (parameters.getDiagram() == BpmnSelectDiagramParameters.NODIAGRAM) {
			bpmn.unmarshall(newDiagram, id2node, id2lane);
		} else {
			Collection<String> elements = parameters.getDiagram().getElements();
			bpmn.unmarshall(newDiagram, elements, id2node, id2lane);
		}
		return newDiagram;
	}

	private static int taskType(
			org.processmining.models.graphbased.directed.bpmn.elements.Activity task) {
		if (task.isBAdhoc()) {
			return 0;
		} else {
			if (task.isBCollapsed()) {
				return 2;
			} else {
				return 0;

			}

		}
		/*
		 * put("None", 0); put("Manual", 1); put("User", 0);
		 * put("Subprocess",2); put("ExpandedSubprocess",3);
		 */
	}

	private static int getEventType(
			org.processmining.models.graphbased.directed.bpmn.elements.Event BEvent) {
		try {
			String Cevent = "";
			switch (BEvent.getEventType()) {
			case START:
				Cevent = "StartEvent";
				break;
			case END:
				Cevent = "EndEvent";
				break;
			default:
				break;
			}

			int type = EventType.TYPE_MAP.get(Cevent);
			return type;
		} catch (Exception e) {
			System.out.println("Error: Event Mapping (" + BEvent.getEventType()
					+ ")");
		}
		return 5;
	}

	private static int typegateway(
			org.processmining.models.graphbased.directed.bpmn.elements.Gateway gate) {

		switch (gate.getGatewayType()) {
		case INCLUSIVE:
			return 1;

		case PARALLEL:
			return 2;

		case DATABASED:
			return 0;

		case EVENTBASED:
			return 3;

		default:
			return 0;

		}

		/*
		 * put("Exclusive_Databased_Gateway", 0);
		 * put("Inclusive_Databased_Gateway", 1); put("ParallelGateway", 2);
		 * put("InclusiveGateway",1); put("AND_Gateway",2);
		 * put("EventbasedGateway",3);
		 */

	}

	private static ProcessModel getProcessModel(Bpmn BPModel) {
		ProcessModel model = new ProcessModel(-1, "Process Model");
		HashMap<Integer, Element> idMap = new HashMap<Integer, Element>();
		idMap = new HashMap<Integer, Element>();
		HashMap<Integer, Lane> laneMap = new HashMap<Integer, Lane>();
		HashMap<Integer, Pool> poolMap = new HashMap<Integer, Pool>();
		BPMNDiagram BPDiagram = BpmnSelectDiagram(BPModel);

		// Map Pools
		for (Swimlane jPool : BPDiagram.getPools()) {
			Pool pool = new Pool(jPool.getId().hashCode(), jPool.getLabel());
			model.addPool(jPool.getLabel());
			poolMap.put(jPool.getId().hashCode(), pool);

			// Map Lanes
			for (Swimlane jLane : BPDiagram.getLanes(jPool)) {
				Lane lane = new Lane(jLane.getId().hashCode(),
						jLane.getLabel(), pool);
				model.addLane(jLane.getLabel());
				laneMap.put(jLane.getId().hashCode(), lane);
			}
		}

		// Iterate over all elems to create the according model objects
		for (org.processmining.models.graphbased.directed.bpmn.elements.Activity task : BPDiagram
				.getActivities()) {
			Activity activity;
			if (task.getParentLane() == null && task.getParentPool() != null) {
				activity = new Activity(task.getId().hashCode(), task
						.getLabel().replaceAll("\n", " "), null,
						poolMap.get(task.getParentPool().hashCode()),
						taskType(task));

			} else {
				String label = task.getLabel().replaceAll("\n", " ");
				activity = new Activity(task.getId().hashCode(), label,
						laneMap.get(task.getParentLane().hashCode()),
						poolMap.get(task.getParentPool().hashCode()),
						taskType(task));
			}
			if (task.getParentSubProcess() != null) {
				activity.setSubProcessID(task.getId().hashCode());
			}

			model.addActivity(activity);
			idMap.put(task.getId().hashCode(), activity);

		}
		for (org.processmining.models.graphbased.directed.bpmn.elements.SubProcess task : BPDiagram
				.getSubProcesses()) {
			Activity activity;
			if (task.getParentLane() == null && task.getParentPool() != null) {
				activity = new Activity(task.getId().hashCode(), task
						.getLabel().replaceAll("\n", " "), null,
						poolMap.get(task.getParentPool().hashCode()), 2);

			} else {
				activity = new Activity(task.getId().hashCode(), task
						.getLabel().replaceAll("\n", " "), laneMap.get(task
						.getParentLane().hashCode()), poolMap.get(task
						.getParentPool().hashCode()), 2);
			}
			if (task.getParentSubProcess() != null) {
				activity.setSubProcessID(task.getId().hashCode());
			}
			model.addActivity(activity);
			idMap.put(task.getId().hashCode(), activity);

		}
		for (org.processmining.models.graphbased.directed.bpmn.elements.Event bevent : BPDiagram
				.getEvents()) {
			Event event;
			if (bevent.getParentLane() == null
					&& bevent.getParentPool() != null) {
				event = new Event(bevent.getId().hashCode(), bevent.getLabel(),
						null, poolMap.get(bevent.getParentPool().hashCode()),
						getEventType(bevent));

			} else {

				event = new Event(bevent.getId().hashCode(), bevent.getLabel(),
						laneMap.get(bevent.getParentLane().hashCode()),
						poolMap.get(bevent.getParentPool().hashCode()),
						getEventType(bevent));
			}
			if (bevent.getParentSubProcess() != null) {
				event.setSubProcessID(bevent.getParentSubProcess().hashCode());
			}
			model.addEvent(event);
			idMap.put(bevent.getId().hashCode(), event);

		}

		for (org.processmining.models.graphbased.directed.bpmn.elements.Gateway gate : BPDiagram
				.getGateways()) {
			Gateway gateway;
			if (gate.getParentLane() == null && gate.getParentPool() != null) {
				gateway = new Gateway(gate.getId().hashCode(), gate.getLabel(),
						null, poolMap.get(gate.getParentPool().hashCode()),
						typegateway(gate));
			} else {

				gateway = new Gateway(gate.getId().hashCode(), gate.getLabel(),
						laneMap.get(gate.getParentLane().hashCode()),
						poolMap.get(gate.getParentPool().hashCode()),
						typegateway(gate));
			}
			if (gate.getParentSubProcess() != null) {
				gateway.setSubProcessID(gate.getParentSubProcess().hashCode());
			}
			model.addGateway(gateway);
			idMap.put(gate.getId().hashCode(), gateway);

		}

		HashMap<Integer, Integer> externalPathInitiators = new HashMap<Integer, Integer>();

		// Iterate over all elems to create the according arcs

		for (Flow ff : BPDiagram.getFlows()) {

			int outId = ff.hashCode();
			Arc arc = new Arc(outId, ff.getLabel(), idMap.get(ff.getSource()
					.getId().hashCode()), idMap.get(ff.getTarget().getId()
					.hashCode()), "SequenceFlow");
			model.addArc(arc);
			// / Considered outgoing id exists as arc
			/*
			 * if (arcs.keySet().contains(outId)) { JSONArc jArc =
			 * arcs.get(outId); if (jArc.getType().equals("SequenceFlow")) { Arc
			 * arc = new Arc(outId, jArc.getLabel(), idMap.get(elem.getId()) ,
			 * idMap.get(jArc.getTarget()), "SequenceFlow"); model.addArc(arc);
			 * } else { Arc arc = new Arc(outId, jArc.getLabel(),
			 * idMap.get(elem.getId()) , idMap.get(jArc.getTarget()),
			 * "MessageFlow"); model.addArc(arc); } }
			 */
		}

		for (MessageFlow ff : BPDiagram.getMessageFlows()) {

			int outId = ff.hashCode();
			Arc arc = new Arc(outId, ff.getLabel(), idMap.get(ff.getSource()
					.getId().hashCode()), idMap.get(ff.getTarget().getId()
					.hashCode()), "MessageFlow");
			model.addArc(arc);

		}
		/*
		 * for (JSONElem elem: elems.values()) { for (int outId: elem.getArcs())
		 * {
		 * 
		 * // if considered outgoing id does not belong to an arc, create a new
		 * one (in order to connect attached event) if
		 * (elems.containsKey(outId)) { Activity activity = ((Activity)
		 * idMap.get(elem.getId())); activity.addAttachedEvent(outId);
		 * 
		 * // Attached event leads to alternative path if (elem.getArcs().size()
		 * > 1) {
		 * System.out.println("Attached Event with alternative Path detected: "
		 * + elem.getLabel()); ((Event)
		 * model.getElem(outId)).setIsAttachedTo(elem.getId()); ((Event)
		 * model.getElem(outId)).setAttached(true);
		 * externalPathInitiators.put(outId, elem.getId());
		 * 
		 * // Attached event goes back to standard path } else { Arc arc = new
		 * Arc(getId(), "", idMap.get(elem.getId()) , idMap.get(outId),
		 * "VirtualFlow"); Event attEvent = ((Event) idMap.get(outId));
		 * attEvent.setAttached(true); attEvent.setIsAttachedTo(elem.getId());
		 * model.addArc(arc); } // Considered outgoing id exists as arc } else
		 * if (arcs.keySet().contains(outId)) { JSONArc jArc = arcs.get(outId);
		 * if (jArc.getType().equals("SequenceFlow")) { Arc arc = new Arc(outId,
		 * jArc.getLabel(), idMap.get(elem.getId()) ,
		 * idMap.get(jArc.getTarget()), "SequenceFlow"); model.addArc(arc); }
		 * else { Arc arc = new Arc(outId, jArc.getLabel(),
		 * idMap.get(elem.getId()) , idMap.get(jArc.getTarget()),
		 * "MessageFlow"); model.addArc(arc); } } else {
		 * System.out.println("No according Arc found: " + outId); } } }
		 * 
		 * // remove all external path initiators for (int exPI:
		 * externalPathInitiators.keySet()) { ProcessModel alternativePathModel
		 * = new ProcessModel(exPI, "");
		 * 
		 * // Create start event Event startEvent = new Event(getId(), "",
		 * model.getElem(exPI).getLane(), model.getElem(exPI).getPool(),
		 * EventType.START_EVENT); alternativePathModel.addEvent(startEvent);
		 * 
		 * // Reallocate elems to alternative path
		 * buildAlternativePathModel(exPI, true, model, alternativePathModel,
		 * exPI);
		 * 
		 * // Add arc from artifical start to real start elem Event realStart =
		 * (Event) alternativePathModel.getElem(exPI);
		 * alternativePathModel.addArc(new Arc(getId(), "", startEvent,
		 * realStart));
		 * 
		 * // Add path to model model.addAlternativePath(alternativePathModel,
		 * exPI);
		 * 
		 * }
		 * 
		 * // Connect inner of subproess to process model for (Activity a:
		 * model.getActivites().values()) { if (a.getType() ==
		 * ActivityType.SUBPROCESS) { int subProcesID = a.getId(); Element out =
		 * null; int removeout = -1;
		 * 
		 * // Remove arcs from subprocess activity for (Arc arc:
		 * model.getArcs().values()) { if (arc.getSource() == a) { out =
		 * arc.getTarget(); removeout = arc.getId(); } }
		 * model.removeArc(removeout);
		 * 
		 * // Check all activities belonging to subprocess for (Event subE:
		 * model.getEvents().values()) { if (subE.getSubProcessID() ==
		 * subProcesID) { boolean hasInput = false; boolean hasOutput = false;
		 * for (Arc arc: model.getArcs().values()) { if (arc.getSource() ==
		 * subE) { hasOutput = true; } if (arc.getTarget() == subE) { hasInput =
		 * true; } } if (!hasInput) { model.addArc(new Arc(getId(), "", a, subE,
		 * "SequenceFlow")); } if (!hasOutput) { model.addArc(new Arc(getId(),
		 * "", subE, out, "SequenceFlow")); } } } } }
		 */
		// Connect inner of subproess to process model
		for (Activity a : model.getActivites().values()) {
			if (a.getType() == ActivityType.SUBPROCESS) {
				int subProcesID = a.getId();
				Element out = null;
				int removeout = -1;

				// Remove arcs from subprocess activity
				for (Arc arc : model.getArcs().values()) {
					if (arc.getSource() == a) {
						out = arc.getTarget();
						removeout = arc.getId();
					}
				}
				model.removeArc(removeout);

				// Check all activities belonging to subprocess
				for (Event subE : model.getEvents().values()) {
					if (subE.getSubProcessID() == subProcesID) {
						boolean hasInput = false;
						boolean hasOutput = false;
						for (Arc arc : model.getArcs().values()) {
							if (arc.getSource() == subE) {
								hasOutput = true;
							}
							if (arc.getTarget() == subE) {
								hasInput = true;
							}
						}
						if (!hasInput) {
							model.addArc(new Arc(java.util.UUID.randomUUID()
									.hashCode(), "", a, subE, "SequenceFlow"));
						}
						if (!hasOutput) {
							model.addArc(new Arc(java.util.UUID.randomUUID()
									.hashCode(), "", subE, out, "SequenceFlow"));
						}
					}
				}
			}
		}
		model.print();
		return model;
	}

	private static Bpmn importBpmnFromStream(InputStream input,
			String filename, long fileSizeInBytes) throws Exception {
		/*
		 * Get an XML pull parser.
		 */
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		/*
		 * Initialize the parser on the provided input.
		 */
		xpp.setInput(input, null);
		/*
		 * Get the first event type.
		 */
		int eventType = xpp.getEventType();
		/*
		 * Create a fresh PNML object.
		 */
		Bpmn bpmn = new Bpmn();
		out.println(xpp.getName());
		/*
		 * Skip whatever we find until we've found a start tag.
		 */
		while (eventType != XmlPullParser.START_TAG) {
			eventType = xpp.next();
		}
		/*
		 * Check whether start tag corresponds to PNML start tag.
		 */
		if (xpp.getName().equals(bpmn.tag)) {
			/*
			 * Yes it does. Import the PNML element.
			 */
			bpmn.importElement(xpp, bpmn);
		} else {
			/*
			 * No it does not. Return null to signal failure.
			 */
			bpmn.log(bpmn.tag, xpp.getLineNumber(), "Expected " + bpmn.tag
					+ ", got " + xpp.getName());
		}
		if (bpmn.hasErrors()) {
			out.print(bpmn.getLog().toString());
			return null;
		}
		return bpmn;
	}

	/**
	 * Loads JSON files from directory and writes generated texts
	 */
	private static void createFromFile(String file) throws JsonSyntaxException,
			IOException {

		JSONReader reader = new JSONReader();
		Gson gson = new Gson();
		int counter = 0;

		Doc modelDoc = gson.fromJson(reader.getJSONStringFromFile(file),
				Doc.class);
		if (modelDoc.getChildShapes() != null) {
			try {
				reader.init();
				reader.getIntermediateProcessFromFile(modelDoc);
				ProcessModel model = reader.getProcessModelFromIntermediate();

				// Multi Pool Model
				if (model.getPools().size() > 1) {
					long time = System.currentTimeMillis();
					System.out.println();
					System.out.print("The model contains "
							+ model.getPools().size() + " pools: ");
					int count = 0;
					for (String role : model.getPools()) {
						if (count > 0 && model.getPools().size() > 2) {
							System.out.print(", ");
						}
						if (count == model.getPools().size() - 1) {
							System.out.print(" and ");
						}
						System.out.print(role + " (" + (count + 1) + ")");
						count++;
					}

					HashMap<Integer, ProcessModel> newModels = model
							.getModelForEachPool();
					for (ProcessModel m : newModels.values()) {
						try {
							m.normalize();
							m.normalizeEndEvents();
						} catch (Exception e) {
							System.out
									.println("Error: Normalization impossible");
							e.printStackTrace();
						}
						String surfaceText = toText(m, counter);
						System.out.println(surfaceText.replaceAll(" process ",
								" " + m.getPools().get(0) + " process "));
					}
				} else {
					try {
						model.normalize();
						model.normalizeEndEvents();
					} catch (Exception e) {
						System.out.println("Error: Normalization impossible");
						e.printStackTrace();
					}
					String surfaceText = toText(model, counter);
					System.out.println(surfaceText);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
