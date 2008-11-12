package org.herac.tuxguitar.gui.help.doc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DocItemReader {
	private static final String ITEM_TAG = "item";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_URL = "url";
	
	public void loadHelpItems(List items, InputStream stream){
		if (stream != null){
			try {
				Document doc = getDocument(stream);
				loadHelpItems(items,doc.getFirstChild());
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}
	
	private void loadHelpItems(List items,Node node){
		NodeList listNode = node.getChildNodes();
		for (int i = 0; i < listNode.getLength(); i++) {
			Node child = listNode.item(i);
			String nameNode = child.getNodeName();
			if (nameNode.equals(ITEM_TAG)) {
				NamedNodeMap attributes = child.getAttributes();
				DocItem item = new DocItem(getAttribute(ATTRIBUTE_NAME,attributes),getAttribute(ATTRIBUTE_URL,attributes));
				loadHelpItems(item.getChildren(),child);
				items.add(item);
			}
		}
	}
	
	private String getAttribute(String attribute,NamedNodeMap map){
		Node node = map.getNamedItem(attribute);
		if(node != null){
			return node.getNodeValue();
		}
		return null;
	}
	
	private Document getDocument(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(stream);
		return document;
	}
}
