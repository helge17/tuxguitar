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
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGVoice;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MusicXMLWriter {
	
	private static final String[] NOTE_NAMES = new String[]{"C","D","E","F","G","A","B"};
	
	private static final int NOTE_SHARPS[] = new int[]{0,0,1,1,2,3,3,4,4,5,5,6};
	
	private static final int NOTE_FLATS[] = new int[]{0,1,1,2,2,3,4,4,5,5,6,6};
	
	private static final boolean[] NOTE_ALTERATIONS = new boolean[]{false,true,false,true,false,false,true,false,true,false,true,false};
	
	private static final String[] DURATION_NAMES = new String[]{ "whole", "half", "quarter", "eighth", "16th", "32nd", "64th", };
	
	private static final int DURATION_DIVISIONS = (int)TGDuration.QUARTER_TIME;
	
	private static final int[] DURATION_VALUES = new int[]{
		DURATION_DIVISIONS * 4, // WHOLE
		DURATION_DIVISIONS * 2, // HALF
		DURATION_DIVISIONS * 1, // QUARTER
		DURATION_DIVISIONS / 2, // EIGHTH
		DURATION_DIVISIONS / 4, // SIXTEENTH
		DURATION_DIVISIONS / 8, // THIRTY_SECOND
		DURATION_DIVISIONS / 16, // SIXTY_FOURTH
	};
	
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
				// TODO: Add multivoice support.
				TGMeasure srcMeasure = (TGMeasure)measures.next();
				TGMeasure measure = new TGVoiceJoiner(this.manager.getFactory(),srcMeasure).process();
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
		boolean tuningChanges = (measure.getNumber() == 1);
		if(divisionChanges || keyChanges || clefChanges || timeSignatureChanges){
			Node measureAttributes = this.addNode(parent,"attributes");
			if(divisionChanges){
				this.addNode(measureAttributes,"divisions",Integer.toString(DURATION_DIVISIONS));
			}
			if(keyChanges){
				this.writeKeySignature(measureAttributes, measure.getKeySignature());
			}
			if(clefChanges){
				this.writeClef(measureAttributes,measure.getClef());
			}
			if(timeSignatureChanges){
				this.writeTimeSignature(measureAttributes,measure.getTimeSignature());
			}
			if(tuningChanges){
				this.writeTuning(measureAttributes, measure.getTrack());
			}
		}
	}
	
	private void writeTuning(Node parent, TGTrack track){
		Node staffDetailsNode = this.addNode(parent,"staff-details");
		this.addNode(staffDetailsNode, "staff-lines", Integer.toString( track.stringCount() ));
		for( int i = track.stringCount() ; i > 0 ; i --){
			TGString string = track.getString( i );
			Node stringNode = this.addNode(staffDetailsNode, "staff-tuning");
			this.addAttribute(stringNode, "line", Integer.toString( (track.stringCount() - string.getNumber()) + 1 ) );
			this.addNode(stringNode, "tuning-step", NOTE_NAMES[ NOTE_SHARPS[ (string.getValue() % 12) ] ] );
			this.addNode(stringNode, "tuning-octave", Integer.toString(string.getValue() / 12) );
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
		boolean tempoChanges = (previous == null || measure.getTempo().getValue() != previous.getTempo().getValue());
		
		if(tempoChanges){
			Node direction = this.addAttribute(this.addNode(parent,"direction"),"placement","above");
			this.writeMeasureTempo(direction, measure.getTempo());
		}
	}
	
	private void writeMeasureTempo(Node parent,TGTempo tempo){
		this.addAttribute(this.addNode(parent,"sound"),"tempo",Integer.toString(tempo.getValue()));
	}
	
	private void writeBeats(Node parent, TGMeasure measure){
		int ks = measure.getKeySignature();
		int beatCount = measure.countBeats();
		for(int b = 0; b < beatCount; b ++){
			TGBeat beat = measure.getBeat( b );
			TGVoice voice = beat.getVoice(0);
			if(voice.isRestVoice()){
				Node noteNode = this.addNode(parent,"note");
				this.addNode(noteNode,"rest");
				this.addNode(noteNode,"voice","1");
				this.writeDuration(noteNode, voice.getDuration());
			}
			else{
				int noteCount = voice.countNotes();
				for(int n = 0; n < noteCount; n ++){
					TGNote note = voice.getNote( n );
					
					Node noteNode = this.addNode(parent,"note");
					int value = (beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue());
					
					Node pitchNode = this.addNode(noteNode,"pitch");
					this.addNode(pitchNode,"step",NOTE_NAMES[ (ks <= 7 ? NOTE_SHARPS[value % 12] : NOTE_FLATS[value % 12] )]);
					this.addNode(pitchNode,"octave",Integer.toString(value / 12));
					if(NOTE_ALTERATIONS[ value % 12 ]){
						this.addNode(pitchNode,"alter", ( ks <= 7 ? "1" : "-1" ) );
					}
					
					Node technicalNode = this.addNode(this.addNode(noteNode, "notations"), "technical");
					this.addNode(technicalNode,"fret", Integer.toString( note.getValue() ));
					this.addNode(technicalNode,"string", Integer.toString( note.getString() ));
					
					this.addNode(noteNode,"voice","1");
					this.writeDuration(noteNode, voice.getDuration());
					
					if(note.isTiedNote()){
						this.addAttribute(this.addNode(noteNode,"tie"),"type","stop");
					}
					if(n > 0){
						this.addNode(noteNode,"chord");
					}
				}
			}
		}
	}
	
	private void writeDuration(Node parent, TGDuration duration){
		int index = duration.getIndex();
		if( index >=0 && index <= 6 ){
			int value = (DURATION_VALUES[ index ] * duration.getDivision().getTimes() / duration.getDivision().getEnters());
			if(duration.isDotted()){
				value += (value / 2);
			}
			else if(duration.isDoubleDotted()){
				value += ((value / 4) * 3);
			}
			
			this.addNode(parent,"duration",Integer.toString(value));
			this.addNode(parent,"type",DURATION_NAMES[ index ]);
			
			if(duration.isDotted()){
				this.addNode(parent,"dot");
			}
			else if(duration.isDoubleDotted()){
				this.addNode(parent,"dot");
				this.addNode(parent,"dot");
			}
			
			if(!duration.getDivision().isEqual(TGDivisionType.NORMAL)){
				Node divisionType = this.addNode(parent,"time-modification");
				this.addNode(divisionType,"actual-notes",Integer.toString(duration.getDivision().getEnters()));
				this.addNode(divisionType,"normal-notes",Integer.toString(duration.getDivision().getTimes()));
			}
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
	
	private Document newDocument() {
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
	
	private void saveDocument() {
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
	
	private static class TGVoiceJoiner {
		private TGFactory factory;
		private TGMeasure measure;
		
		public TGVoiceJoiner(TGFactory factory,TGMeasure measure){
			this.factory = factory;
			this.measure = measure.clone(factory, measure.getHeader());
			this.measure.setTrack( measure.getTrack() );
		}
		
		public TGMeasure process(){
			this.orderBeats();
			this.joinBeats();
			return this.measure;
		}
		
		public void joinBeats(){
			TGBeat previous = null;
			boolean finish = true;
			
			long measureStart = this.measure.getStart();
			long measureEnd = (measureStart + this.measure.getLength());
			for(int i = 0;i < this.measure.countBeats();i++){
				TGBeat beat = this.measure.getBeat( i );
				TGVoice voice = beat.getVoice(0);
				for(int v = 1; v < beat.countVoices(); v++ ){
					TGVoice currentVoice = beat.getVoice(v);
					if(!currentVoice.isEmpty()){
						for(int n = 0 ; n < currentVoice.countNotes() ; n++ ){
							TGNote note = currentVoice.getNote( n );
							voice.addNote( note );
						}
					}
				}
				if( voice.isEmpty() ){
					this.measure.removeBeat(beat);
					finish = false;
					break;
				}
				
				long beatStart = beat.getStart();
				if(previous != null){
					long previousStart = previous.getStart();
					
					TGDuration previousBestDuration = null;
					for(int v = /*1*/0; v < previous.countVoices(); v++ ){
						TGVoice previousVoice = previous.getVoice(v);
						if(!previousVoice.isEmpty()){
							long length = previousVoice.getDuration().getTime();
							if( (previousStart + length) <= beatStart){
								if( previousBestDuration == null || length > previousBestDuration.getTime() ){
									previousBestDuration = previousVoice.getDuration();
								}
							}
						}
					}
					
					if(previousBestDuration != null){
						previousBestDuration.copy( previous.getVoice(0).getDuration() );
					}else{
						if(voice.isRestVoice()){
							this.measure.removeBeat(beat);
							finish = false;
							break;
						}
						TGDuration duration = TGDuration.fromTime(this.factory, (beatStart - previousStart) );
						duration.copy( previous.getVoice(0).getDuration() );
					}
				}
				
				TGDuration beatBestDuration = null;
				for(int v = /*1*/0; v < beat.countVoices(); v++ ){
					TGVoice currentVoice = beat.getVoice(v);
					if(!currentVoice.isEmpty()){
						long length = currentVoice.getDuration().getTime();
						if( (beatStart + length) <= measureEnd ){
							if( beatBestDuration == null || length > beatBestDuration.getTime() ){
								beatBestDuration = currentVoice.getDuration();
							}
						}
					}
				}
				
				if(beatBestDuration == null){
					if(voice.isRestVoice()){
						this.measure.removeBeat(beat);
						finish = false;
						break;
					}
					TGDuration duration = TGDuration.fromTime(this.factory, (measureEnd - beatStart) );
					duration.copy( voice.getDuration() );
				}
				previous = beat;
			}
			if(!finish){
				joinBeats();
			}
		}
		
		public void orderBeats(){
			for(int i = 0;i < this.measure.countBeats();i++){
				TGBeat minBeat = null;
				for(int j = i;j < this.measure.countBeats();j++){
					TGBeat beat = this.measure.getBeat(j);
					if(minBeat == null || beat.getStart() < minBeat.getStart()){
						minBeat = beat;
					}
				}
				this.measure.moveBeat(i, minBeat);
			}
		}
	}

}
