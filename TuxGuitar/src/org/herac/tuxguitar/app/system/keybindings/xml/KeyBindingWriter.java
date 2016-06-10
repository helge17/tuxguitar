package org.herac.tuxguitar.app.system.keybindings.xml;

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

import org.herac.tuxguitar.app.system.keybindings.KeyBindingAction;
import org.herac.tuxguitar.ui.resource.UIKey;
import org.herac.tuxguitar.ui.resource.UIKeyMask;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class KeyBindingWriter {
	
	private static final String SHORTCUT_ROOT = "shortcuts";
	private static final String SHORTCUT_TAG = "shortcut";
	private static final String SHORTCUT_ATTRIBUTE_ACTION = "action";
	private static final String SHORTCUT_ATTRIBUTE_KEY = "key";
	private static final String SHORTCUT_ATTRIBUTE_MASK = "mask";
	private static final String MASK_SEPARATOR = ",";
	
	public static void setBindings(List<KeyBindingAction> list,String fileName) {
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
	private static void setBindings(List<KeyBindingAction> list,Document document){
		Node shortcutsNode = document.createElement(SHORTCUT_ROOT);
		
		Iterator<KeyBindingAction> it = list.iterator();
		while(it.hasNext()){
			KeyBindingAction keyBindingAction = (KeyBindingAction) it.next();
			
			Node node = document.createElement(SHORTCUT_TAG);
			shortcutsNode.appendChild(node);
			
			Attr attrKey = document.createAttribute(SHORTCUT_ATTRIBUTE_KEY);
			Attr attrAction = document.createAttribute(SHORTCUT_ATTRIBUTE_ACTION);
			
			attrKey.setNodeValue(Integer.toString(keyBindingAction.getConvination().getKey().getCode()));
			attrAction.setNodeValue(keyBindingAction.getAction());
			
			node.getAttributes().setNamedItem(attrKey);
			node.getAttributes().setNamedItem(attrAction);
			
			if( keyBindingAction.getConvination().getMask() != null && !keyBindingAction.getConvination().getMask().getKeys().isEmpty()) {
				Attr attrMask = document.createAttribute(SHORTCUT_ATTRIBUTE_MASK);
				attrMask.setNodeValue(toString(keyBindingAction.getConvination().getMask()));
				node.getAttributes().setNamedItem(attrMask);
			}
		}
		document.appendChild(shortcutsNode);
	}
	
	private static String toString(UIKeyMask mask) {
		StringBuffer sb = new StringBuffer();
		for(UIKey key : mask.getKeys()) {
			if( sb.length() > 0 ) {
				sb.append(MASK_SEPARATOR);
			}
			sb.append(key.getCode());
		}
		return sb.toString();
	}
}
