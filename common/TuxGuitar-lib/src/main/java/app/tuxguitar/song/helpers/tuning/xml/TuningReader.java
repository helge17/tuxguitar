package app.tuxguitar.song.helpers.tuning.xml;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import app.tuxguitar.song.helpers.tuning.InstrumentTuning;
import app.tuxguitar.song.helpers.tuning.TuningGroup;
import app.tuxguitar.song.helpers.tuning.TuningPreset;
import app.tuxguitar.song.models.TGClef;
import app.tuxguitar.song.models.TGTuning;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TuningReader {
	static final String TUNING_TAG = "tuning";
	static final String GROUP_TAG = "group";
	static final String NAME_ATTRIBUTE = "name";
	static final String NOTES_ATTRIBUTE = "notes";
	static final String PRIORITY_ATTRIBUTE = "priority";
	static final String KEY_SEPARATOR = ",";

	static final String INSTRUMENTS_TAG = "instruments";
	static final String INSTRUMENT_TAG = "instrument";
	static final String CLEF_TAG = "clef";
	static final String TUNING_GROUP_TAG = "tuningGroup";
	static final String TUNING_SUBGROUP_TAG = "tuningSubGroup";
	static final String INSTRUMENT_TUNING_TAG = "tuning";
	static final String ATTRIBUTE_FROM = "from";
	static final String ATTRIBUTE_TO = "to";


	public void loadTunings(TuningGroup group, Map<Integer, InstrumentTuning> instruments, InputStream stream) throws ParserConfigurationException, SAXException, IOException{
		if ( stream != null ){
			Document doc = getDocument(stream);
			loadTunings(group, instruments, doc.getFirstChild());
		}
	}

	private static Document getDocument(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// CVE-2020-14940
		try {
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setXIncludeAware(false);
		} catch (Throwable throwable) {
		}
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(stream);

		return document;
	}

	private static void loadTunings(TuningGroup group, Map<Integer, InstrumentTuning> instruments, Node node){
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();

			if (nodeName.equals(TUNING_TAG)) {
				NamedNodeMap params = child.getAttributes();
				String name = params.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
				String notes = params.getNamedItem(NOTES_ATTRIBUTE).getNodeValue();

				if (name == null || notes == null || name.trim().equals("") || notes.trim().equals("")){
					throw new RuntimeException("Invalid Tuning file format.");
				}
				String[] noteStrings = notes.split(KEY_SEPARATOR);
				int[] noteValues = new int[noteStrings.length];
				for (int j = 0; j < noteStrings.length; j++){
					int note = Integer.parseInt(noteStrings[j]);
					if( note >= 0 && note < 128 ){
						noteValues[j] = note;
					} else {
						throw new RuntimeException("Invalid Tuning note: " + noteStrings[j]);
					}
				}

				TuningPreset tuning = new TuningPreset(group, name, noteValues);

				// Add priority attribute if available
				if (params.getNamedItem(PRIORITY_ATTRIBUTE) != null) {
					String prioStr = params.getNamedItem(PRIORITY_ATTRIBUTE).getNodeValue();
					// make sure priority string is valid
					if (prioStr != null && !prioStr.equals("") && prioStr.matches("\\d+"))
						tuning.setPriority(Integer.parseInt(prioStr));
				}

				group.getTunings().add(tuning);
			} else if (nodeName.equals(GROUP_TAG)) {
				NamedNodeMap params = child.getAttributes();
				String name = params.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
				TuningGroup subGroup = new TuningGroup(group, name);
				loadTunings(subGroup, instruments, child);
				group.getGroups().add(subGroup);
			} else if (nodeName.equals(INSTRUMENTS_TAG)) {
				loadInstrumentTunings(child, instruments, group);
			}
		}
	}

	private static void loadInstrumentTunings(Node node, Map<Integer, InstrumentTuning> instrumentsMap, TuningGroup group) {
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();

			if (nodeName.equals(INSTRUMENT_TAG)) {
				NamedNodeMap attributes = child.getAttributes();
				int from = Integer.valueOf(attributes.getNamedItem(ATTRIBUTE_FROM).getNodeValue());
				int to = Integer.valueOf(attributes.getNamedItem(ATTRIBUTE_TO).getNodeValue());
				InstrumentTuning instrumentTuning = loadInstrumentTuning(child, group);
				for (int instr=from; instr<=to; instr++) {
					instrumentsMap.put(instr, instrumentTuning);
				}
			}
		}
	}

	private static InstrumentTuning loadInstrumentTuning(Node node, TuningGroup tgGroup) {
		TGClef clef = null;
		TGTuning tuning = null;
		String groupName = null;
		String subGroupName = null;
		String tuningName = null;

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String childName = child.getNodeName();

			if (childName.equals(CLEF_TAG)) {
				String clefName = child.getTextContent();
				switch(clefName) {
				case "CLEF_TREBLE_8":
					clef = TGClef.CLEF_TREBLE_8;
					break;
				case "CLEF_BASS_8":
					clef = TGClef.CLEF_BASS_8;
					break;
				case "CLEF_TREBLE_STANDARD":
					clef = TGClef.CLEF_TREBLE_STANDARD;
					break;
				case "CLEF_BASS_STANDARD":
					clef = TGClef.CLEF_BASS_STANDARD;
					break;
				case "CLEF_TENOR_8":
					clef = TGClef.CLEF_TENOR_8;
					break;
				case "CLEF_ALTO_8":
					clef = TGClef.CLEF_ALTO_8;
					break;
				case "CLEF_TENOR_STANDARD":
					clef = TGClef.CLEF_TENOR_STANDARD;
					break;
				case "CLEF_ALTO_STANDARD":
					clef = TGClef.CLEF_ALTO_STANDARD;
					break;
				default:
					throw new InvalidParameterException("unknown clef " + clefName);
				}
			}
			else if (childName.equals(TUNING_GROUP_TAG)) {
				groupName = child.getTextContent();
			}
			else if (childName.equals(TUNING_SUBGROUP_TAG)) {
				subGroupName = child.getTextContent();
			}
			else if (childName.equals(INSTRUMENT_TUNING_TAG)) {
				tuningName = child.getTextContent();
			}
		}
		// either all tunings attributes defined, or none of them
		if ( ((groupName == null) != (subGroupName == null)) || ((groupName == null) != (tuningName == null))) {
			throw new InvalidParameterException("instrument tuning, not all attributes defined");
		}
		if (tuningName != null) {
			TuningGroup instrumentGroup = null;
			TuningGroup subGroup = null;
			for (TuningGroup g : tgGroup.getGroups()) {
				if (groupName.equals(g.getName())) {
					instrumentGroup = g;
				}
			}
			if (instrumentGroup == null) {
				throw new InvalidParameterException("tuning group not found: " + groupName);
			}
			for (TuningGroup g : instrumentGroup.getGroups()) {
				if (subGroupName.equals(g.getName())) {
					subGroup = g;
				}
			}
			if (subGroup == null) {
				throw new InvalidParameterException("tuning subGroup not found: " + subGroupName);
			}
			for (TuningPreset preset : subGroup.getTunings()) {
				if (tuningName.equals(preset.getName())) {
					tuning = preset;
				}
			}
			if (tuning == null) {
				throw new InvalidParameterException("tuning not found: " + tuningName);
			}
		}
		return new InstrumentTuning(clef, tuning);
	}
}
