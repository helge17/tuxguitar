package org.herac.tuxguitar.app.view.dialog.chord.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

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

import org.herac.tuxguitar.song.models.TGChord;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TGChordXMLWriter {
	
	public static void setChords(List<TGChord> chords,String fileName) {
		File file = new File(fileName);
		
		Document doc = createDocument();
		setChords(chords,doc);
		saveDocument(doc,file);
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
	
	/**
	 * Write chords to xml file
	 */
	private static void setChords(List<TGChord> chords,Document document){
		//chords tag
		Node chordsNode = document.createElement(TGChordXML.CHORD_LIST_TAG);
		
		Iterator<TGChord> it = chords.iterator();
		while(it.hasNext()){
			TGChord chord = (TGChord)it.next();
			
			//chord tag
			Node chordNode = document.createElement(TGChordXML.CHORD_TAG);
			chordsNode.appendChild(chordNode);
			
			//name attribute
			Attr nameAttr = document.createAttribute(TGChordXML.CHORD_NAME_ATTRIBUTE);
			nameAttr.setNodeValue( chord.getName());
			chordNode.getAttributes().setNamedItem(nameAttr);
			
			//strings attribute
			Attr stringsAttr = document.createAttribute(TGChordXML.CHORD_STRINGS_ATTRIBUTE);
			stringsAttr.setNodeValue(Integer.toString(chord.getStrings().length));
			chordNode.getAttributes().setNamedItem(stringsAttr);
			
			//first fret attribute
			Attr firstFretAttr = document.createAttribute(TGChordXML.CHORD_FIRST_FRET_ATTRIBUTE);
			firstFretAttr.setNodeValue(Integer.toString(chord.getFirstFret()));
			chordNode.getAttributes().setNamedItem(firstFretAttr);
			
			for(int i = 0;i < chord.getStrings().length; i++){
				//string tag
				Node stringNode = document.createElement(TGChordXML.STRING_TAG);
				chordNode.appendChild(stringNode);
				
				//number attribute
				Attr numberAttr = document.createAttribute(TGChordXML.STRING_NUMBER_ATTRIBUTE);
				numberAttr.setNodeValue(Integer.toString(i));
				stringNode.getAttributes().setNamedItem(numberAttr);
				
				//fret attribute
				Attr fretAttr = document.createAttribute(TGChordXML.STRING_FRET_ATTRIBUTE);
				fretAttr.setNodeValue(Integer.toString(chord.getFretValue(i)));
				stringNode.getAttributes().setNamedItem(fretAttr);
			}
		}
		
		document.appendChild(chordsNode);
	}
}
