package app.tuxguitar.song.helpers.tuning.xml;

import app.tuxguitar.song.helpers.tuning.TuningGroup;
import app.tuxguitar.song.helpers.tuning.TuningPreset;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

import static app.tuxguitar.song.helpers.tuning.xml.TuningReader.*;

public class TuningWriter {
	private static final String TUNINGS_ROOT = "tunings";

	public static void write(TuningGroup group, String fileName) {
		try {
			File file = new File(fileName);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Node root = document.createElement(TUNINGS_ROOT);
			write(group, root);
			document.appendChild(root);
			FileOutputStream fs = new FileOutputStream(file);
			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(document);
			Result output = new StreamResult(fs);
			idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
			idTransform.transform(input, output);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private static void write(TuningGroup group, Node parent) {
		Document document = parent.getOwnerDocument();
		if (group.getName() != null && !group.getName().isEmpty()) {
			Attr attrName = document.createAttribute(NAME_ATTRIBUTE);
			attrName.setNodeValue(group.getName());
			parent.getAttributes().setNamedItem(attrName);
		}
		for (TuningGroup child : group.getGroups()) {
			Node node = document.createElement(GROUP_TAG);
			write(child, node);
			parent.appendChild(node);
		}
		for (TuningPreset preset : group.getTunings()) {
			Node node = document.createElement(TUNING_TAG);
			Attr attrName = document.createAttribute(NAME_ATTRIBUTE);
			Attr attrNotes = document.createAttribute(NOTES_ATTRIBUTE);
			attrName.setNodeValue(preset.getName());
			attrNotes.setNodeValue(Arrays.stream(preset.getValues()).mapToObj(Integer::toString).collect(Collectors.joining(KEY_SEPARATOR)));
			node.getAttributes().setNamedItem(attrName);
			node.getAttributes().setNamedItem(attrNotes);
			parent.appendChild(node);
		}
	}
}
