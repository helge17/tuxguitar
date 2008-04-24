package org.herac.tuxguitar.gui.system.keybindings.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.herac.tuxguitar.gui.system.keybindings.KeyBindingAction;
import org.herac.tuxguitar.gui.system.keybindings.KeyBinding;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KeyBindingReader {
	private static final String SHORTCUT_TAG = "shortcut";
	private static final String ACTION_ATTRIBUTE = "action";
	private static final String KEYS_ATTRIBUTE = "keys";
	
	public static List getKeyBindings(String fileName) {
		try{
			File file = new File(fileName);
			if (file.exists()){
				return getBindings(getDocument(file).getFirstChild());
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	private static Document getDocument(File file) {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(file);
		} catch (SAXException sxe) {
			sxe.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return document;
	}
	
	/**
	 * Read shortcuts from xml file
	 * 
	 * @param shortcutsNode
	 * @return
	 */
	private static List getBindings(Node shortcutsNode){
		List list = new ArrayList();
		
		NodeList nodeList = shortcutsNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();
			
			if (nodeName.equals(SHORTCUT_TAG)) {
				NamedNodeMap params = child.getAttributes();
				
				String key = params.getNamedItem(KEYS_ATTRIBUTE).getNodeValue();
				String action = params.getNamedItem(ACTION_ATTRIBUTE).getNodeValue();
				
				if (key == null || action == null || key.trim().equals("") || action.trim().equals("")){
					throw new RuntimeException("Invalid KeyBinding file format.");
				}
				
				list.add(new KeyBindingAction(action, KeyBinding.parse(key)));
			}
		}
		return list;
	}
}
