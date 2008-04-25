package org.herac.tuxguitar.gui.editors.chord.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.song.models.TGChord;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ChordXMLReader {
	
	public static List getChords(String fileName) {
		List chords = new ArrayList();
		try{
			File file = new File(fileName);
			if (file.exists()){
				Document doc = getDocument(file);
				loadChords(doc.getFirstChild(),chords);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return chords;
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
	private static void loadChords(Node chordsNode,List chords){
		try{
			NodeList chordList = chordsNode.getChildNodes();
			for (int i = 0; i < chordList.getLength(); i++) {
				Node chordItem = chordList.item(i);
				if (chordItem.getNodeName().equals(ChordXML.CHORD_TAG)) {
					NamedNodeMap chordAttributes = chordItem.getAttributes();
					
					String name = chordAttributes.getNamedItem(ChordXML.CHORD_NAME_ATTRIBUTE).getNodeValue();
					String strings = chordAttributes.getNamedItem(ChordXML.CHORD_STRINGS_ATTRIBUTE).getNodeValue();
					String firstFret = chordAttributes.getNamedItem(ChordXML.CHORD_FIRST_FRET_ATTRIBUTE).getNodeValue();
					
					TGChord chord = TuxGuitar.instance().getSongManager().getFactory().newChord(Integer.parseInt(strings));
					chord.setName(name);
					chord.setFirstFret(Integer.parseInt(firstFret));
					
					NodeList stringList = chordItem.getChildNodes();
					for (int j = 0; j < stringList.getLength(); j++) {
						Node stringItem = stringList.item(j);
						if (stringItem.getNodeName().equals(ChordXML.STRING_TAG)) {
							NamedNodeMap stringAttributes = stringItem.getAttributes();
							
							String number = stringAttributes.getNamedItem(ChordXML.STRING_NUMBER_ATTRIBUTE).getNodeValue();
							String fret = stringAttributes.getNamedItem(ChordXML.STRING_FRET_ATTRIBUTE).getNodeValue();
							
							chord.addFretValue(Integer.parseInt(number),Integer.parseInt(fret));
						}
					}
					chords.add(chord);
				}
			}
		}catch(Exception e){
			chords.clear();
			e.printStackTrace();
		}
	}
}
