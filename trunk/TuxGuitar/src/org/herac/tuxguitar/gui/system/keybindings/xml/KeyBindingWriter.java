package org.herac.tuxguitar.gui.system.keybindings.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.herac.tuxguitar.gui.system.keybindings.KeyBindingAction;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class KeyBindingWriter {
	
	private static final String SHORTCUTS_TAG = "shortcuts";
	private static final String SHORTCUT_TAG = "shortcut";
	private static final String ACTION_ATTRIBUTE = "action";
	private static final String KEYS_ATTRIBUTE = "keys";
	
	public static void setBindings(List list,String fileName) {
		try{
			File file = new File(fileName);
			Document doc = createDocument();
			setBindings(list,doc);
			saveDocument(doc,file);
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	public static Document createDocument() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			return document;
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
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
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	/**
	 * Write shortcuts to xml file
	 * 
	 * @param shortcutsNode
	 * @return
	 */
	private static void setBindings(List list,Document document){
		Node shortcutsNode = document.createElement(SHORTCUTS_TAG);
		
		Iterator it = list.iterator();
		while(it.hasNext()){
			KeyBindingAction keyBindingAction = (KeyBindingAction) it.next();
			
			Node node = document.createElement(SHORTCUT_TAG);
			shortcutsNode.appendChild(node);
			
			Attr keyAttr = document.createAttribute(KEYS_ATTRIBUTE);
			Attr ActionAttr = document.createAttribute(ACTION_ATTRIBUTE);
			
			keyAttr.setNodeValue(keyBindingAction.getKeyBinding().toString());
			ActionAttr.setNodeValue(keyBindingAction.getAction());
			
			node.getAttributes().setNamedItem(keyAttr);
			node.getAttributes().setNamedItem(ActionAttr);
		}
		document.appendChild(shortcutsNode);
	}
	
}
