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

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.gm.GMChannelRouterConfigurator;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.musicxml.MusicXMLLyricWriter.MusicXMLMeasureLyric;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGMusicKeyUtils;
import org.herac.tuxguitar.util.TGVersion;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;


public class MusicXMLWriter {
	
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
	
	private static final String TABLATURE_SUFFIX = "-tab";
	
	private TGSongManager manager;
	
	private OutputStream stream;
	
	private Document document;
	
	public MusicXMLWriter(OutputStream stream){
		this.stream = stream;
	}
	
	public void writeSong(TGSong song) throws TGFileFormatException{
		try {
			this.manager = new TGSongManager();
			this.document = newDocument();
			
			Node node = this.addNode(this.document,"score-partwise");
			this.addAttribute(node, "version", "4.0");
			this.writeHeaders(song, node);
			this.writeSong(song, node);
			this.saveDocument();
			
			this.stream.flush();
			this.stream.close();
		}catch(Throwable throwable){
			throw new TGFileFormatException("Could not write song!.",throwable);
		}
	}
	
	private void writeHeaders(TGSong song, Node parent){
		this.writeWork(song, parent);
		this.writeIdentification(song, parent);
	}
	
	private void writeWork(TGSong song, Node parent){
		this.addNode(this.addNode(parent,"work"),"work-title", song.getName());
	}
	
	private void writeIdentification(TGSong song, Node parent){
		Node identification = this.addNode(parent,"identification");
		this.addAttribute(this.addNode(identification,"creator",song.getAuthor()),"type","composer");
		this.addNode(this.addNode(identification,"encoding"), "software", "TuxGuitar " + TGVersion.CURRENT.getVersion());
	}
	
	private void writeSong(TGSong song, Node parent){
		this.writePartList(song, parent);
		this.writeParts(song, parent);
	}
	
	private void writePartList(TGSong song, Node parent){
		Node partList = this.addNode(parent,"part-list");
		
		GMChannelRouter gmChannelRouter = new GMChannelRouter();
		GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(gmChannelRouter);
		gmChannelRouterConfigurator.configureRouter(song.getChannels());
		
		Iterator<TGTrack> tracks = song.getTracks();
		while(tracks.hasNext()){
			TGTrack track = (TGTrack)tracks.next();
			TGChannel channel = this.manager.getChannel(song, track.getChannelId());
			
			// score
			Node scoreParts = this.addNode(partList,"score-part");
			this.addAttribute(scoreParts, "id", "P" + track.getNumber());
			
			this.addNode(scoreParts, "part-name", track.getName());
			
			if( channel != null ){
				GMChannelRoute gmChannelRoute = gmChannelRouter.getRoute(channel.getChannelId());
				
				Node scoreInstrument = this.addAttribute(this.addNode(scoreParts, "score-instrument"), "id", "P" + track.getNumber() + "-I1");
				this.addNode(scoreInstrument, "instrument-name",channel.getName());
			
				Node midiInstrument = this.addAttribute(this.addNode(scoreParts, "midi-instrument"), "id", "P" + track.getNumber() + "-I1");
				this.addNode(midiInstrument, "midi-channel",Integer.toString(gmChannelRoute != null ? gmChannelRoute.getChannel1() + 1 : 16));
				this.addNode(midiInstrument, "midi-program",Integer.toString(channel.getProgram() + 1));
			}
			
			// tab
			scoreParts = this.addNode(partList,"score-part");
			this.addAttribute(scoreParts, "id", "P" + track.getNumber() + TABLATURE_SUFFIX);
			this.addNode(scoreParts, "part-name", track.getName());
		}
	}
	
	private void writeParts(TGSong song, Node parent){
		Iterator<TGTrack> tracks = song.getTracks();
		while(tracks.hasNext()){
			TGTrack track = (TGTrack)tracks.next();
			this.writeTrack(track, parent, false);	// score
			this.writeTrack(track, parent, true);	// tablature
		}
	}
	
	private void writeTrack(TGTrack track, Node parent, boolean isTablature){
		Node part = this.addAttribute(this.addNode(parent,"part"), "id", "P" + track.getNumber() + (isTablature ? TABLATURE_SUFFIX : ""));
		
		TGMeasure previous = null;
		
		MusicXMLLyricWriter lyricWriter = new MusicXMLLyricWriter(track);
		
		Iterator<TGMeasure> measures = track.getMeasures();
		while(measures.hasNext()){
			// TODO: Add multivoice support.
			TGMeasure srcMeasure = (TGMeasure)measures.next();
			TGMeasure measure = new TGVoiceJoiner(this.manager.getFactory(),srcMeasure).process();
			
			Node measureNode = this.addAttribute(this.addNode(part,"measure"), "number",Integer.toString(measure.getNumber()));
			
			this.writeMeasureAttributes(measureNode, measure, previous, isTablature);
			if (!isTablature) {
				this.writeDirection(measureNode, measure, previous);
			}
			
			this.writeBarline(measureNode, measure);

			TGMeasureImpl measureImpl = (TGMeasureImpl) srcMeasure;
			
			MusicXMLMeasureLyric[] measureLyrics = lyricWriter.generateLyricList(measureImpl);
			this.writeBeats(measureNode, measure, isTablature, measureLyrics);
			
			previous = measure;
		}
	}
	
	private void writeBarline(Node parent, TGMeasure measure) {
		boolean needBarline = measure.isRepeatOpen() || (measure.getRepeatClose() > 0);
		
		// TODO: 
		// add, when available in tuxguitar; alternate repeat, bar-style, coda, wavy-line etc..
		// https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/barline/
		
		if (needBarline) {
			Node barLine = this.addNode(parent,"barline");
			
			if (measure.isRepeatOpen()) {
				Node repeat = this.addNode(barLine,"repeat");
				this.addAttribute(repeat, "direction", "forward");				
			}
			
			if (measure.getRepeatClose() > 0) {
				Node repeat = this.addNode(barLine,"repeat");
				this.addAttribute(repeat, "direction", "backward");
				this.addAttribute(repeat, "times", Integer.toString(measure.getRepeatClose()));							
			}

			// TODO:
			// add, when available in tuxguitar, winged, after-jump
			// https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/repeat/

		}
	}

	private void writeMeasureAttributes(Node parent, TGMeasure measure, TGMeasure previous, boolean isTablature){
		boolean divisionChanges = (previous == null);
		boolean keyChanges = (previous == null || measure.getKeySignature() != previous.getKeySignature());
		boolean clefChanges = (previous == null || measure.getClef() != previous.getClef());
		boolean timeSignatureChanges = (previous == null || !measure.getTimeSignature().isEqual(previous.getTimeSignature()));
		if ((!isTablature) && (divisionChanges || keyChanges || clefChanges || timeSignatureChanges)){
			Node measureAttributes = this.addNode(parent,"attributes");
			if(divisionChanges){
				this.addNode(measureAttributes,"divisions",Integer.toString(DURATION_DIVISIONS));
			}
			if(keyChanges){
				this.writeKeySignature(measureAttributes, measure.getKeySignature());
			}
			if(timeSignatureChanges){
				this.writeTimeSignature(measureAttributes,measure.getTimeSignature());
			}
			if(clefChanges){
				this.writeClef(measureAttributes,measure.getClef());
			}
		}
		if (isTablature && (previous==null || measure.getNumber() == 1)) {
			Node measureAttributes = this.addNode(parent,"attributes");
			if (previous==null) {
				this.writeTabClef(measureAttributes);
			}
			if (measure.getNumber() == 1) {
				this.writeTuning(measureAttributes, measure.getTrack(), measure.getKeySignature());
			}
		}
	}
	
	private void writeTuning(Node parent, TGTrack track, int keySignature){
		Node staffDetailsNode = this.addNode(parent,"staff-details");
		this.addNode(staffDetailsNode, "staff-lines", Integer.toString( track.stringCount() ));
		for( int i = track.stringCount() ; i > 0 ; i --){
			TGString string = track.getString( i );
			Node stringNode = this.addNode(staffDetailsNode, "staff-tuning");
			this.addAttribute(stringNode, "line", Integer.toString( (track.stringCount() - string.getNumber()) + 1 ) );
			this.writeNote(stringNode, "tuning-", string.getValue(), keySignature);
		}
	}
	
	private void writeNote(Node parent, String prefix, int value, int keySignature) {
		this.addNode(parent,prefix+"step", TGMusicKeyUtils.noteShortName(value,keySignature));
		int alteration = TGMusicKeyUtils.noteAlteration(value, keySignature);
		if(alteration != TGMusicKeyUtils.NATURAL){
			this.addNode(parent,prefix+"alter", ( alteration == TGMusicKeyUtils.SHARP ? "1" : "-1" ) );
		}
		this.addNode(parent,prefix+"octave", String.valueOf(TGMusicKeyUtils.noteOctave(value, keySignature)));
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
	}
	
	private void writeClef(Node parent, int clef){
		Node node = this.addNode(parent,"clef");
		if(clef == TGMeasure.CLEF_TREBLE){
			this.addNode(node,"sign","G");
			this.addNode(node,"line","2");
			this.addNode(node, "clef-octave-change", String.valueOf(-1));
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
	
	private void writeTabClef(Node parent){
		Node node = this.addNode(parent,"clef");
		this.addNode(node, "sign", "TAB");
	}
	
	private void writeDirection(Node parent, TGMeasure measure, TGMeasure previous){
		boolean tempoChanges = (previous == null || measure.getTempo().getValue() != previous.getTempo().getValue());
		
		if(tempoChanges){
			Node direction = this.addAttribute(this.addNode(parent,"direction"),"placement","above");
			Node directionType = this.addNode(direction, "direction-type");
			Node metronome = this.addNode(directionType, "metronome");
			this.addNode(metronome, "beat-unit", "quarter");
			this.addNode(metronome, "per-minute", String.valueOf(measure.getTempo().getValue()));
		}
	}
	

	private void writeBeats(Node parent, TGMeasure measure, boolean isTablature, MusicXMLMeasureLyric[] lyrics){
		int ks = measure.getKeySignature();
		int beatCount = measure.countBeats();
		
		int lyricIndex = 0;
		
		for(int b = 0; b < beatCount; b ++){
			TGBeat beat = measure.getBeat( b );
			TGVoice voice = beat.getVoice(0);
			
			if(voice.isRestVoice()){
				Node noteNode = this.addNode(parent,"note");
				this.addNode(noteNode,"rest");
				this.writeDuration(noteNode, voice.getDuration(), false);
			
			} else {
				int noteCount = voice.countNotes();

				// Note: The order of elements is important 
				// see https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/note/
				for(int n = 0; n < noteCount; n ++){
					TGNote note = voice.getNote( n );
					
					Node noteNode = this.addNode(parent,"note");
					
					int value = (beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue());
					
					if(n > 0){
						this.addNode(noteNode,"chord");
					}
										
					Node pitchNode = this.addNode(noteNode,"pitch");
					this.writeNote(pitchNode, "", value, ks);
					this.writeDuration(noteNode, voice.getDuration(), note.isTiedNote());
					
					if (isTablature) {
						Node technicalNode = this.addNode(this.addNode(noteNode, "notations"), "technical");
						this.addNode(technicalNode,"fret", Integer.toString( note.getValue() ));
						this.addNode(technicalNode,"string", Integer.toString( note.getString() ));
					
					} else if(n==0) {
						// Attach lyric to the first note
						try {
							MusicXMLMeasureLyric measureLyric = lyrics[lyricIndex++];
							writeLyric(noteNode, measureLyric);
						} catch (Exception e) {
							// ignore
							// can be out of bound? when there are more lyrics than text
							// can be null if there is an offset
						}											
					}
				}
			}
		}
	}
	
	private void writeLyric(Node parent, MusicXMLMeasureLyric measureLyric) {
		if (measureLyric.text.length() > 0) {
			Node lyricNode = this.addNode(parent, "lyric");

			this.addNode(lyricNode,"syllabic", measureLyric.syllabic.toString());
			this.addNode(lyricNode,"text", measureLyric.text);
		}
	}
	
	private void writeDuration(Node parent, TGDuration duration, boolean isTiedNote){
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
			if(isTiedNote){
				this.addAttribute(this.addNode(parent,"tie"),"type","stop");
			}

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
			DOMImplementation domImpl = this.document.getImplementation();
			DocumentType docType = domImpl.createDocumentType("doctype", 
					"-//Recordare//DTD MusicXML 4.0 Partwise//EN",
					"http://www.musicxml.org/dtds/partwise.dtd");
			idTransform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType.getPublicId());
			idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
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
						previous.getVoice(0).getDuration().copyFrom( previousBestDuration );
					}else{
						if(voice.isRestVoice()){
							this.measure.removeBeat(beat);
							finish = false;
							break;
						}
						TGDuration duration = TGDuration.fromTime(this.factory, (beatStart - previousStart) );
						previous.getVoice(0).getDuration().copyFrom( duration );
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
					voice.getDuration().copyFrom( duration );
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
