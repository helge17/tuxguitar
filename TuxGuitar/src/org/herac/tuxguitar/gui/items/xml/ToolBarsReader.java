package org.herac.tuxguitar.gui.items.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.herac.tuxguitar.gui.items.ItemManager;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ToolBarsReader {
	private static final String ITEM_TAG = "toolbar";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_ENABLED = "enabled";
	
	public static void loadToolBars(ItemManager manager,File file){
		try {
			if (file.exists()){
				loadToolBars(manager,getDocument(file).getFirstChild());
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	private static Document getDocument(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		return document;
	}
	
	private static void loadToolBars(ItemManager manager,Node node){
		NodeList listNode = node.getChildNodes();
		int index = 0;
		for (int i = 0; i < listNode.getLength(); i++) {
			Node child = listNode.item(i);
			String nameNode = child.getNodeName();
			
			if (nameNode.equals(ITEM_TAG)) {
				NamedNodeMap params = child.getAttributes();
				
				Node name = params.getNamedItem(ATTR_NAME);
				Node enabled = params.getNamedItem(ATTR_ENABLED);
				
				if (name == null || enabled == null || name.getNodeValue() == null || enabled.getNodeValue() == null ){
					System.err.println("Invalid ToolBar Attributes.");
					continue;
				}
				manager.setToolBarStatus(name.getNodeValue(),Boolean.valueOf(enabled.getNodeValue().trim()).booleanValue(), index ++);
			}
		}
	}
}
