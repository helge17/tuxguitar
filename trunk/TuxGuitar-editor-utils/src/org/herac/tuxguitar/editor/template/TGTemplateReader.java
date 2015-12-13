package org.herac.tuxguitar.editor.template;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TGTemplateReader {
	
	private static final String TAG_TEMPLATE = "template";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_RESOURCE = "resource";
	
	public void loadTemplates(List<TGTemplate> templates,InputStream stream) throws Throwable{
		if( stream != null ){
			loadTemplates(templates,createDocument(stream).getFirstChild());
		}
	}
	
	private void loadTemplates(List<TGTemplate> templates,Node node) throws Throwable {
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();
			
			if (nodeName.equals(TAG_TEMPLATE)) {
				NamedNodeMap params = child.getAttributes();
				
				TGTemplate tgTemplate = new TGTemplate();
				tgTemplate.setName(params.getNamedItem(ATTRIBUTE_NAME).getNodeValue());
				tgTemplate.setResource(params.getNamedItem(ATTRIBUTE_RESOURCE).getNodeValue());
				
				if (tgTemplate.getName() == null || tgTemplate.getName().trim().length() == 0 ){
					throw new RuntimeException("Invalid template name.");
				}
				if (tgTemplate.getResource() == null || tgTemplate.getResource().trim().length() == 0 ){
					throw new RuntimeException("Invalid template resource.");
				}
				
				templates.add(tgTemplate);
			}
		}
	}
	
	private Document createDocument(InputStream stream) throws Throwable {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(stream);
		
		return document;
	}
}
