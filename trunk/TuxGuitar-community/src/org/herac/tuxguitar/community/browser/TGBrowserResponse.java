package org.herac.tuxguitar.community.browser;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TGBrowserResponse {
	
	private static final String TAG_STATUS = "status";
	private static final String TAG_ELEMENTS = "elements";
	private static final String TAG_ELEMENT = "element";
	private static final String TAG_SONG = "song";
	private static final String TAG_PARAMETERS = "parameters";
	private static final String TAG_PARAMETER = "parameter";
	private static final String ATTRIBUTE_CODE = "code";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_URL = "url";
	private static final String ATTRIBUTE_KEY = "key";
	private static final String ATTRIBUTE_VALUE = "value";
	
	private Document document;
	
	public TGBrowserResponse( InputStream stream ) throws Throwable {
		this.initialize( stream );
	}
	
	private void initialize(InputStream stream) throws Throwable {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		this.document = builder.parse(stream);
	}
	
	public String getStatus() {
		if ( this.document != null ){
			return getStatus(this.document.getFirstChild());
		}
		return null;
	}
	
	private String getStatus(Node rootNode){
		NodeList rootNodes = rootNode.getChildNodes();
		for (int i = 0; i < rootNodes.getLength(); i++) {
			Node statusNode = rootNodes.item(i);
			if (statusNode.getNodeName().equals( TAG_STATUS )) {
				return getAttributeValue( statusNode.getAttributes(), ATTRIBUTE_CODE);
			}
		}
		return null;
	}
	
	public void loadElements(List<TGBrowserElement> list){
		if ( this.document != null ){
			loadElements(list, this.document.getFirstChild());
		}
	}
	
	private void loadElements(List<TGBrowserElement> list,Node rootNode){
		NodeList rootNodes = rootNode.getChildNodes();
		for (int i = 0; i < rootNodes.getLength(); i++) {
			Node elementsNode = rootNodes.item(i);
			
			if (elementsNode.getNodeName().equals( TAG_ELEMENTS )) {
				
				NodeList elementList = elementsNode.getChildNodes();
				for (int e = 0; e < elementList.getLength(); e++) {
					Node elementNode = elementList.item(e);
					String nodeName = elementNode.getNodeName();
					
					if (nodeName.equals( TAG_ELEMENT )) {
						NamedNodeMap params = elementNode.getAttributes();
						
						String name = getAttributeValue( params, ATTRIBUTE_NAME);
						if (name != null && name.trim().length() > 0 ){
							TGBrowserElementImpl element = new TGBrowserElementImpl( name );
							
							NodeList nodeChildren = elementNode.getChildNodes();
							for (int c = 0; c < nodeChildren.getLength(); c++) {
								Node child = nodeChildren.item( c );
								if( child.getNodeName().equals( TAG_SONG ) ){
									element.setUrl( getAttributeValue( child.getAttributes() , ATTRIBUTE_URL) );
								}else if( child.getNodeName().equals( TAG_PARAMETERS ) ){
									
									NodeList parameters = child.getChildNodes();
									for (int p = 0; p < parameters.getLength(); p++) {
										
										Node parameter = parameters.item( p );
										if( parameter.getNodeName().equals( TAG_PARAMETER ) ){
											NamedNodeMap parameterAttributes = parameter.getAttributes();
											
											String key = getAttributeValue( parameterAttributes, ATTRIBUTE_KEY);
											String value = getAttributeValue( parameterAttributes, ATTRIBUTE_VALUE);
											
											if (key != null && value != null && key.trim().length() > 0 && value.trim().length() > 0 ){
												element.addProperty(key, value);
											}
										}
									}
								}
							}
							
							list.add( element );
						}
					}
				}
			}
		}
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
