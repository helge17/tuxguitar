package org.herac.tuxguitar.community.io;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TGShareSongResponse {
	
	private static final String TAG_STATUS = "status";
	private static final String TAG_MESSAGES = "messages";
	private static final String TAG_MESSAGE = "message";
	private static final String ATTRIBUTE_CODE = "code";
	private static final String ATTRIBUTE_VALUE = "value";
	
	private Document document;
	
	public TGShareSongResponse( InputStream stream ) throws Throwable {
		this.initialize( stream );
	}
	
	private void initialize(InputStream stream) throws Throwable {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		this.document = builder.parse(stream);
	}
	
	public String getStatus() throws Throwable {
		if ( this.document != null ){
			return getStatus(this.document.getFirstChild());
		}
		return null;
	}
	
	private String getStatus(Node rootNode) throws Throwable {
		NodeList rootNodes = rootNode.getChildNodes();
		for (int i = 0; i < rootNodes.getLength(); i++) {
			Node statusNode = rootNodes.item(i);
			if (statusNode.getNodeName().equals( TAG_STATUS )) {
				return getAttributeValue( statusNode.getAttributes(), ATTRIBUTE_CODE);
			}
		}
		return null;
	}
	
	public String loadMessages(List<String> list) throws Throwable {
		if ( this.document != null ){
			return loadMessages(list , this.document.getFirstChild());
		}
		return null;
	}
	
	private String loadMessages(List<String> list, Node rootNode) throws Throwable {
		NodeList rootNodes = rootNode.getChildNodes();
		for (int i = 0; i < rootNodes.getLength(); i++) {
			Node messagesNode = rootNodes.item(i);
			if (messagesNode.getNodeName().equals( TAG_MESSAGES )) {
				NodeList messageList = messagesNode.getChildNodes();
				for (int e = 0; e < messageList.getLength(); e++) {
					Node messageNode = messageList.item(e);
					if (messageNode.getNodeName().equals( TAG_MESSAGE )) {
						list.add( getAttributeValue( messageNode.getAttributes(), ATTRIBUTE_VALUE) );
					}
				}
			}
		}
		return null;
	}
	
	private String getAttributeValue( NamedNodeMap node , String attribute ){
		try{
			if( node != null && attribute != null ){
				Node namedItem = node.getNamedItem( attribute );
				if( namedItem != null ){
					return URLDecoder.decode(namedItem.getNodeValue(), "UTF-8");
				}
			}
		} catch ( Throwable throwable ){
			throwable.printStackTrace();
		}
		return null;
	}
}
