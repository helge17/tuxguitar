package org.herac.tuxguitar.io.musicxml;

import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGTupleto;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MusicXMLWriter {
	
	private static final String[] NATURAL_NOTES = new String[]{"C","C","D","D","E","F","F","G","G","A","A","B"};
	
	private static final boolean[] ACCIDENTAL_NOTES = new boolean[]{false,true,false,true,false,false,true,false,true,false,true,false};
	
	private TGSongManager manager;
	
	private OutputStream stream;
	
	private Document document;
	
	public MusicXMLWriter(OutputStream stream){
		this.stream = stream;
	}
	
	public void writeSong(TGSong song) throws TGFileFormatException{
		try {
			this.manager = new TGSongManager();
			this.manager.setSong(song);
			this.document = newDocument();
			
			Node node = this.addNode(this.document,"score-partwise");
			this.writeHeaders(node);
			this.writeSong(node);
			this.saveDocument();
			
			this.stream.flush();
			this.stream.close();
		}catch(Throwable throwable){
			throw new TGFileFormatException("Could not write song!.",throwable);
		}
	}
	
	public Document newDocument() {
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
	
	public void saveDocument() {
		try {
			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(this.document);
			Result output = new StreamResult(this.stream);
			idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
			idTransform.transform(input, output);
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	private Node addAttribute(Node node, String name, String value){
		Attr attribute = this.document.createAttribute(name);
		attribute.setNodeValue(value);
		node.getAttributes().setNamedItem(attribute);
		return node;
	}
	
	private Node addNode(Node parent, String name){
		Node node = this.document.createElement(name);
		parent.appendChild(node);
		return node;
	}
	
	private Node addNode(Node parent, String name, String content){
		Node node = this.addNode(parent, name);
		node.setTextContent(content);
		return node;
	}
	
	private void writeHeaders(Node parent){
		this.writeWork(parent);
		this.writeIdentification(parent);
	}
	
	private void writeWork(Node parent){
		this.addNode(this.addNode(parent,"work"),"work-title",this.manager.getSong().getName());
	}
	
	private void writeIdentification(Node parent){
		Node identification = this.addNode(parent,"identification");
		this.addNode(this.addNode(identification,"encoding"), "software", "TuxGuitar");
		this.addAttribute(this.addNode(identification,"creator",this.manager.getSong().getAuthor()),"type","composer");
	}
	
	private void writeSong(Node parent){
		this.writePartList(parent);
		this.writeParts(parent);
	}
	
	private void writePartList(Node parent){
		Node partList = this.addNode(parent,"part-list");
		
		Iterator tracks = this.manager.getSong().getTracks();
		while(tracks.hasNext()){
			TGTrack track = (TGTrack)tracks.next();
			
			Node scoreParts = this.addNode(partList,"score-part");
			this.addAttribute(scoreParts, "id", "P" + track.getNumber());
			
			this.addNode(scoreParts, "part-name", track.getName());
			
			Node scoreInstrument = this.addAttribute(this.addNode(scoreParts, "score-instrument"), "id", "P" + track.getNumber() + "-I1");
			this.addNode(scoreInstrument, "instrument-name",MidiInstrument.INSTRUMENT_LIST[track.getChannel().getInstrument()].getName());
			
			Node midiInstrument = this.addAttribute(this.addNode(scoreParts, "midi-instrument"), "id", "P" + track.getNumber() + "-I1");
			this.addNode(midiInstrument, "midi-channel",Integer.toString(track.getChannel().getChannel() + 1));
			this.addNode(midiInstrument, "midi-program",Integer.toString(track.getChannel().getInstrument() + 1));
		}
	}
	
	private void writeParts(Node parent){
		Iterator tracks = this.manager.getSong().getTracks();
		while(tracks.hasNext()){
			TGTrack track = (TGTrack)tracks.next();
			Node part = this.addAttribute(this.addNode(parent,"part"), "id", "P" + track.getNumber());
			
			TGMeasure previous = null;
			
			Iterator measures = track.getMeasures();
			while(measures.hasNext()){
				TGMeasure measure = (TGMeasure)measures.next();
				Node measureNode = this.addAttribute(this.addNode(part,"measure"), "number",Integer.toString(measure.getNumber()));
				
				this.writeMeasureAttributes(measureNode, measure, previous);
				this.writeDirection(measureNode, measure, previous);
				this.writeBeats(measureNode, measure);
				
				previous = measure;
			}
		}
	}
	
	private void writeMeasureAttributes(Node parent,TGMeasure measure, TGMeasure previous){
		boolean divisionChanges = (previous == null);
		boolean keyChanges = (previous == null || measure.getKeySignature() != previous.getKeySignature());
		boolean clefChanges = (previous == null || measure.getClef() != previous.getClef());
		boolean timeSignatureChanges = (previous == null || !measure.getTimeSignature().isEqual(previous.getTimeSignature()));
		
		if(divisionChanges || keyChanges || clefChanges || timeSignatureChanges){
			Node measureAttributes = this.addNode(parent,"attributes");
			if(divisionChanges){
				this.addNode(measureAttributes,"divisions",Integer.toString(TGDuration.SIXTY_FOURTH));
			}
			if(keyChanges){
				this.writeKeySignature(measureAttributes, /*measure.getKeySignature()*/ 0);
			}
			if(clefChanges){
				this.writeClef(measureAttributes,measure.getClef());
			}
			if(timeSignatureChanges){
				this.writeTimeSignature(measureAttributes,measure.getTimeSignature());
			}
		}
	}
	
	private void writeTimeSignature(Node parent, TGTimeSignature ts){
		Node node = this.addNode(parent,"time");
		this.addNode(node,"beats",Integer.toString(ts.getNumerator()));
		this.addNode(node,"beat-type",Integer.toString(ts.getDenominator().getValue()));
	}
	
	private void writeKeySignature(Node parent, int ks){
		int value = ks;
		if(value != 0){
			value = ( (((ks - 1) % 7) + 1) * ( ks > 7?-1:1));
		}
		Node key = this.addNode(parent,"key");
		this.addNode(key,"fifths",Integer.toString( value ));
		this.addNode(key,"mode","major");
	}
	
	private void writeClef(Node parent, int clef){
		Node node = this.addNode(parent,"clef");
		if(clef == TGMeasure.CLEF_TREBLE){
			this.addNode(node,"sign","G");
			this.addNode(node,"line","2");
		}
		else if(clef == TGMeasure.CLEF_BASS){
			this.addNode(node,"sign","F");
			this.addNode(node,"line","4");
		}
		else if(clef == TGMeasure.CLEF_TENOR){
			this.addNode(node,"sign","G");
			this.addNode(node,"line","2");
		}
		else if(clef == TGMeasure.CLEF_ALTO){
			this.addNode(node,"sign","G");
			this.addNode(node,"line","2");
		}
	}
	
	private void writeDirection(Node parent, TGMeasure measure, TGMeasure previous){
		boolean tempoChanges = (previous == null || measure.getTempo().getValue() != measure.getTempo().getValue());
		
		if(tempoChanges){
			Node direction = this.addAttribute(this.addNode(parent,"direction"),"placement","above");
			this.writeMeasureTempo(direction, measure.getTempo());
		}
	}
	
	private void writeMeasureTempo(Node parent,TGTempo tempo){
		this.addAttribute(this.addNode(parent,"sound"),"tempo",Integer.toString(tempo.getValue()));
	}
	
	private void writeBeats(Node parent, TGMeasure measure){
		int beatCount = measure.countBeats();
		for(int b = 0; b < beatCount; b ++){
			TGBeat beat = measure.getBeat( b );
			
			if(beat.isRestBeat()){
				Node noteNode = this.addNode(parent,"note");
				this.addNode(noteNode,"rest");
				this.addNode(noteNode,"voice","1");
				this.writeDuration(noteNode, beat.getDuration());
			}
			else{
				int noteCount = beat.countNotes();
				for(int n = 0; n < noteCount; n ++){
					TGNote note = beat.getNote( n );
					
					Node noteNode = this.addNode(parent,"note");
					int value = (note.getBeat().getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue());
					Node pitchNode = this.addNode(noteNode,"pitch");
					this.addNode(pitchNode,"step",NATURAL_NOTES[value % 12]);
					this.addNode(pitchNode,"octave",Integer.toString(value / 12));
					
					if(ACCIDENTAL_NOTES[value % 12]){
						this.addNode(noteNode,"accidental","sharp");
					}
					
					this.addNode(noteNode,"voice","1");
					this.writeDuration(noteNode, beat.getDuration());
					
					if(n > 0){
						this.addNode(noteNode,"chord");
					}
				}
			}
		}
	}
	
	private void writeDuration(Node parent, TGDuration duration){
		this.addNode(parent,"duration",Integer.toString(duration.getValue()));
		if(duration.getValue() == TGDuration.WHOLE){
			this.addNode(parent,"type","quarter");
		}
		else if(duration.getValue() == TGDuration.WHOLE){
			this.addNode(parent,"type","whole");
		}
		else if(duration.getValue() == TGDuration.HALF){
			this.addNode(parent,"type","half");
		}
		else if(duration.getValue() == TGDuration.QUARTER){
			this.addNode(parent,"type","quarter");
		}
		else if(duration.getValue() == TGDuration.EIGHTH){
			this.addNode(parent,"type","eighth");
		}
		else if(duration.getValue() == TGDuration.SIXTEENTH){
			this.addNode(parent,"type","16th");
		}
		else if(duration.getValue() == TGDuration.THIRTY_SECOND){
			this.addNode(parent,"type","32nd");
		}
		else if(duration.getValue() == TGDuration.SIXTY_FOURTH){
			this.addNode(parent,"type","64th");
		}
		
		if(duration.isDotted()){
			this.addNode(parent,"dot");
		}
		else if(duration.isDoubleDotted()){
			this.addNode(parent,"dot");
			this.addNode(parent,"dot");
		}
		
		if(!duration.getTupleto().isEqual(TGTupleto.NORMAL)){
			Node tupleto = this.addNode(parent,"time-modification");
			this.addNode(tupleto,"actual-notes",Integer.toString(duration.getTupleto().getEnters()));
			this.addNode(tupleto,"normal-notes",Integer.toString(duration.getTupleto().getTimes()));
		}
	}
}
