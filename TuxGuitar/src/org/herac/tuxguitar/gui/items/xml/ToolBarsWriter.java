package org.herac.tuxguitar.gui.items.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.herac.tuxguitar.gui.items.ToolItems;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ToolBarsWriter {
	
	private static final String ITEM_LIST_TAG = "toolbars";
	private static final String ITEM_TAG = "toolbar";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_ENABLED = "enabled";
	
	public static void saveToolBars(ToolItems[] items,File file) {
		try {
			Document doc = createDocument();
			setToolBars(items,doc);
			saveDocument(doc,file);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public static Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		return document;
	}
	
	public static void saveDocument(Document document,File file) throws FileNotFoundException, TransformerException {
		FileOutputStream fs = new FileOutputStream(file);
		
		// Write it out again
		TransformerFactory xformFactory = TransformerFactory.newInstance();
		Transformer idTransform = xformFactory.newTransformer();
		Source input = new DOMSource(document);
		Result output = new StreamResult(fs);
		idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
		idTransform.transform(input, output);
	}
	
	private static void setToolBars(ToolItems[] items,Document document){
		//chords tag
		Node listNode = document.createElement(ITEM_LIST_TAG);
		
		for(int i = 0; i < items.length ; i ++){
			
			//chord tag
			Node node = document.createElement(ITEM_TAG);
			listNode.appendChild(node);
			
			//name attribute
			Attr attribute = document.createAttribute(ATTR_NAME);
			attribute.setNodeValue( items[i].getName());
			node.getAttributes().setNamedItem(attribute);
			
			//enabled attribute
			attribute = document.createAttribute(ATTR_ENABLED);
			attribute.setNodeValue( Boolean.toString(items[i].isEnabled()));
			node.getAttributes().setNamedItem(attribute);
		}
		
		document.appendChild(listNode);
	}
}
