package org.herac.tuxguitar.gui.tools.browser.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.herac.tuxguitar.gui.tools.browser.TGBrowserCollection;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TGBrowserWriter {
	
	private static final String ITEM_LIST_TAG = "browser-collections";
	private static final String ITEM_TAG = "browser-collection";
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_DATA = "data";
	
	public void saveCollections(TGBrowserManager manager,String fileName) {
		try{
			Document doc = createDocument();
			saveCollections(manager,doc);
			saveDocument(doc,new File(fileName));
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	private static void saveCollections(TGBrowserManager manager,Document document){
		//chords tag
		Node listNode = document.createElement(ITEM_LIST_TAG);
		
		Iterator collections = manager.getCollections();
		while(collections.hasNext()){
			TGBrowserCollection collection = (TGBrowserCollection)collections.next();
			
			//chord tag
			Node node = document.createElement(ITEM_TAG);
			listNode.appendChild(node);
			
			//name attribute
			Attr typeAttr = document.createAttribute(ATTRIBUTE_TYPE);
			typeAttr.setNodeValue(collection.getType());
			
			//name attribute
			Attr dataAttr = document.createAttribute(ATTRIBUTE_DATA);
			dataAttr.setNodeValue(collection.getData().toString());
			
			node.getAttributes().setNamedItem(typeAttr);
			node.getAttributes().setNamedItem(dataAttr);
		}
		
		document.appendChild(listNode);
	}
	
	public static Document createDocument() {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		}catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		return document;
	}
	
	public static void saveDocument(Document document,File file) {
		try {
			FileOutputStream fs = new FileOutputStream(file);
			
			// Write it out again
			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(document);
			Result output = new StreamResult(fs);
			idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
			idTransform.transform(input, output);
			
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
