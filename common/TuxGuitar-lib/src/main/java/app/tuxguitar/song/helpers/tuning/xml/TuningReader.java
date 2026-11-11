package app.tuxguitar.song.helpers.tuning.xml;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import app.tuxguitar.song.helpers.tuning.TuningGroup;
import app.tuxguitar.song.helpers.tuning.TuningPreset;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TuningReader {
	static final String TUNING_TAG = "tuning";
	static final String GROUP_TAG = "group";
	static final String NAME_ATTRIBUTE = "name";
	static final String NOTES_ATTRIBUTE = "notes";
	static final String PRIORITY_ATTRIBUTE = "priority";
	static final String KEY_SEPARATOR = ",";
	private static final String INVALID_FORMAT = "Invalid Tuning file format.";

	public void loadTunings(TuningGroup group, InputStream stream){
		try{
			if ( stream != null ){
				Document doc = getDocument(stream);
				loadTunings(group, doc.getFirstChild());
			}
		}catch(Throwable e){
			e.printStackTrace();
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
		// fatal errors are thrown, without the default handler also printing them
		builder.setErrorHandler(new DefaultHandler());
		document = builder.parse(stream);

		return document;
	}

	private static void loadTunings(TuningGroup group, Node node){
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();

			if (nodeName.equals(TUNING_TAG)) {
				group.getTunings().add(readTuning(group, child.getAttributes()));
			} else if (nodeName.equals(GROUP_TAG)) {
				group.getGroups().add(readGroup(group, child));
			}
		}
	}

	private static TuningGroup readGroup(TuningGroup parent, Node node){
		String name = attributeValue(node.getAttributes(), NAME_ATTRIBUTE);
		TuningGroup group = new TuningGroup(parent, name);
		loadTunings(group, node);
		return group;
	}

	private static TuningPreset readTuning(TuningGroup group, NamedNodeMap params){
		String name = requiredValue(params, NAME_ATTRIBUTE);
		int[] notes = readNotes(requiredValue(params, NOTES_ATTRIBUTE));
		TuningPreset tuning = new TuningPreset(group, name, notes);
		readPriority(tuning, params.getNamedItem(PRIORITY_ATTRIBUTE));
		return tuning;
	}

	private static String attributeValue(NamedNodeMap params, String attribute){
		Node node = params.getNamedItem(attribute);
		if (node == null){
			throw new RuntimeException(INVALID_FORMAT);
		}
		return node.getNodeValue();
	}

	private static String requiredValue(NamedNodeMap params, String attribute){
		String value = attributeValue(params, attribute);
		if (value.trim().isEmpty()){
			throw new RuntimeException(INVALID_FORMAT);
		}
		return value;
	}

	private static int[] readNotes(String notes){
		String[] noteStrings = notes.split(KEY_SEPARATOR);
		int[] noteValues = new int[noteStrings.length];
		for (int i = 0; i < noteStrings.length; i++){
			noteValues[i] = readNote(noteStrings[i]);
		}
		return noteValues;
	}

	private static int readNote(String noteString){
		int note = Integer.parseInt(noteString);
		if( note < 0 || note >= 128 ){
			throw new RuntimeException("Invalid Tuning note: " + noteString);
		}
		return note;
	}

	// the priority is optional, and ignored unless it is made of digits
	private static void readPriority(TuningPreset tuning, Node priority){
		if (priority != null && priority.getNodeValue().matches("\\d+")) {
			tuning.setPriority(Integer.parseInt(priority.getNodeValue()));
		}
	}
}
