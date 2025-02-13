package org.herac.tuxguitar.io.tg;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGPickStroke;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend.BendPoint;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar.TremoloBarPoint;
import org.herac.tuxguitar.util.TGVersion;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TGSongWriterImpl extends TGStream implements TGSongWriter {
	
	private Document document;
	
	public TGFileFormat getFileFormat(){
		return TG_FORMAT;
	}
	
	@Override
	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		new TGSongManager().updatePreciseStart(handle.getSong());
		this.writeXMLDocument(handle);
		ArchiveOutputStream<ZipArchiveEntry> outputStream;
		try {
			outputStream = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, handle.getOutputStream());
			// version
			ZipArchiveEntry zaeVersion = new ZipArchiveEntry(VERSION_FILE_NAME);
			outputStream.putArchiveEntry(zaeVersion);
			this.addVersion(outputStream);
			outputStream.closeArchiveEntry();
			// content
			ZipArchiveEntry zaeContent = new ZipArchiveEntry(CONTENT_FILE_NAME);
			outputStream.putArchiveEntry(zaeContent);
			this.saveDocument(outputStream);
			outputStream.closeArchiveEntry();
			outputStream.close();
		} catch (ArchiveException | IOException e) {
			e.printStackTrace();
			throw new TGFileFormatException(e);
		}
	}
	
	public void writeContent(TGSongWriterHandle handle) throws TGFileFormatException {
		new TGSongManager().updatePreciseStart(handle.getSong());
		this.writeXMLDocument(handle);
		this.saveDocument(handle.getOutputStream());
	}
	
	private void writeXMLDocument(TGSongWriterHandle handle) throws TGFileFormatException {
		try {
			this.document = newDocument();
			Node nodeRoot = this.addNode(this.document,TAG_TGFile);
			Node nodeVersion = this.addNode(nodeRoot, TAG_TG_VERSION);
			this.addAttributeInt(nodeVersion, "major", TGVersion.CURRENT.getMajor());
			this.addAttributeInt(nodeVersion, "minor", TGVersion.CURRENT.getMinor());
			this.addAttributeInt(nodeVersion, "revision", TGVersion.CURRENT.getRevision());
			this.writeSong(handle.getSong(), this.addNode(nodeRoot, TAG_TGSONG));
		}catch( Throwable throwable ){
			throw new TGFileFormatException(throwable);
		}
	}
	
	private void writeSong(TGSong song, Node nodeSong) throws IOException{
		// song attributes
		this.addNode(nodeSong, TAG_NAME, song.getName());
		this.addNode(nodeSong, TAG_ARTIST, song.getArtist());
		this.addNode(nodeSong, TAG_ALBUM, song.getAlbum());
		this.addNode(nodeSong, TAG_AUTHOR, song.getAuthor());
		this.addNode(nodeSong, TAG_DATE, song.getDate());
		this.addNode(nodeSong, TAG_COPYRIGHT, song.getCopyright());
		this.addNode(nodeSong, TAG_WRITER, song.getWriter());
		this.addNode(nodeSong, TAG_TRANSCRIBER, song.getTranscriber());
		this.addNode(nodeSong, TAG_COMMENTS, song.getComments());
		// channels
		Iterator<TGChannel> channels = song.getChannels();
		while (channels.hasNext()) {
			this.writeChannel(channels.next(), this.addNode(nodeSong, TAG_CHANNEL));
		}
		// measure headers
		Iterator<TGMeasureHeader> headers = song.getMeasureHeaders();
		while(headers.hasNext()) {
			this.writeMeasureHeader(headers.next(), this.addNode(nodeSong, TAG_MEASURE_HEADER));
		}
		Iterator<TGTrack> tracks = song.getTracks();
		while (tracks.hasNext()) {
			this.writeTrack(tracks.next(), this.addNode(nodeSong, TAG_TGTRACK));
		}
	}
	
	private void writeChannel(TGChannel channel, Node nodeChannel) {
		this.addNodeInt(nodeChannel, TAG_ID, channel.getChannelId());
		this.addNodeInt(nodeChannel, TAG_BANK, channel.getBank());
		this.addNodeInt(nodeChannel, TAG_PROGRAM, channel.getProgram());
		this.addNodeInt(nodeChannel, TAG_VOLUME, channel.getVolume());
		this.addNodeInt(nodeChannel, TAG_BALANCE, channel.getBalance());
		this.addNodeInt(nodeChannel, TAG_CHORUS, channel.getChorus());
		this.addNodeInt(nodeChannel, TAG_REVERB, channel.getReverb());
		this.addNodeInt(nodeChannel, TAG_PHASER, channel.getPhaser());
		this.addNodeInt(nodeChannel, TAG_TREMOLO, channel.getTremolo());
		this.addNode(nodeChannel, TAG_NAME, channel.getName());
		Iterator<TGChannelParameter> parameters =  channel.getParameters();
		while (parameters.hasNext()) {
			TGChannelParameter parameter = parameters.next();
			Node nodeParameter = this.addNode(nodeChannel, TAG_CHANNEL_PARAMETER);
			this.addAttribute(nodeParameter, TAG_KEY, parameter.getKey());
			this.addAttribute(nodeParameter, TAG_VALUE, parameter.getValue());
		}
	}
	
	private void writeMeasureHeader(TGMeasureHeader header, Node nodeMeasureHeader) {
		Node node = this.addNode(nodeMeasureHeader, TAG_TIME_SIGNATURE);
		this.addAttributeInt(node, TAG_NUMERATOR ,header.getTimeSignature().getNumerator());
		this.addAttributeInt(node, TAG_DENOMINATOR ,header.getTimeSignature().getDenominator().getValue());
		Node nodeTempo = this.addNodeInt(nodeMeasureHeader, TAG_TEMPO, header.getTempo().getRawValue());
		if (header.getTempo().getBase() != TGDuration.QUARTER) {
			this.addAttributeInt(nodeTempo, TAG_TEMPO_BASE, header.getTempo().getBase());
		}
		if (header.getTempo().isDotted()) {
			this.addAttribute(nodeTempo, TAG_TEMPO_DOTTED, "true");
		}
		if (header.isRepeatOpen()) {
			this.addNode(nodeMeasureHeader, TAG_REPEAT_OPEN);
		}
		if (header.getRepeatClose() != 0) {
			this.addNodeInt(nodeMeasureHeader, TAG_REPEAT_CLOSE, header.getRepeatClose());
		}
		if (header.getRepeatAlternative() != 0) {
			node = this.addNode(nodeMeasureHeader, TAG_REPEAT_ALTERNATIVE);
			// bit map
			int flag = header.getRepeatAlternative();
			int alternative = 1;
			while (flag != 0) {
				if ((flag & 0x01) != 0) {
					this.addNodeInt(node, TAG_ALTERNATIVE, alternative);
				}
				flag >>= 1;
				alternative ++;
			}
		}
		if (header.hasMarker()) {
			Node nodeMarker = this.addNode(nodeMeasureHeader, TAG_MARKER, header.getMarker().getTitle());
			this.addAttributeInt(nodeMarker, TAG_COLOR_R, header.getMarker().getColor().getR());
			this.addAttributeInt(nodeMarker, TAG_COLOR_G, header.getMarker().getColor().getG());
			this.addAttributeInt(nodeMarker, TAG_COLOR_B, header.getMarker().getColor().getB());
		}
		if (header.getTripletFeel() != TGMeasureHeader.TRIPLET_FEEL_NONE) {
			this.addNode(nodeMeasureHeader, TAG_TRIPLET_FEEL, this.tripletsWriteMap.get(header.getTripletFeel()));
		}
		if (header.isLineBreak()) {
			this.addNode(nodeMeasureHeader, TAG_LINE_BREAK);
		}
	}
	
	private void writeTrack(TGTrack track, Node nodeTrack) {
		this.addNode(nodeTrack, TAG_NAME, track.getName());
		if (!track.isPercussion()) {
			this.addAttributeInt(nodeTrack, TAG_MAXFRET, track.getMaxFret());
		}
		if (track.isMute()) {
			this.addNode(nodeTrack, TAG_SOLOMUTE, VAL_MUTE);
		} else if (track.isSolo()) {
			this.addNode(nodeTrack, TAG_SOLOMUTE, VAL_SOLO);
		}
		this.addNodeInt(nodeTrack, TAG_CHANNELID, track.getChannelId());
		if (track.getOffset() != 0) {
			this.addNodeInt(nodeTrack, TAG_OFFSET, track.getOffset());
		}
		Node nodeColor = this.addNode(nodeTrack, TAG_COLOR);
		this.addAttributeInt(nodeColor, TAG_COLOR_R, track.getColor().getR());
		this.addAttributeInt(nodeColor, TAG_COLOR_G, track.getColor().getG());
		this.addAttributeInt(nodeColor, TAG_COLOR_B, track.getColor().getB());
		for (TGString string : track.getStrings()) {
			this.addNodeInt(nodeTrack, TAG_TGSTRING, string.getValue());
		}
		Node nodeLyric = this.addNode(nodeTrack, TAG_TGLYRIC, track.getLyrics().getLyrics());
		this.addAttributeInt(nodeLyric, TAG_FROM, track.getLyrics().getFrom());
		this.writeMeasures(track.getMeasures(), nodeTrack);
		
	}
	
	private void writeMeasures(Iterator<TGMeasure> measures, Node nodeTrack) {
		TGMeasure precedingMeasure = null;
		while (measures.hasNext()) {
			TGMeasure measure = measures.next();
			Node nodeMeasure = this.addNode(nodeTrack, TAG_TGMEASURE);
			if ((precedingMeasure == null) || (precedingMeasure.getClef() != measure.getClef())) {
				this.addNode(nodeMeasure, TAG_CLEF, this.mapWriteClefs.get(measure.getClef()));
			}
			if ((precedingMeasure==null) || (precedingMeasure.getKeySignature() != measure.getKeySignature())) {
				this.addNodeInt(nodeMeasure, TAG_KEYSIGNATURE, measure.getKeySignature());
			}
			List<TGBeat> beats = measure.getBeats();
			for (TGBeat beat : beats) {
				this.writeBeat(beat, this.addNode(nodeMeasure, TAG_TGBEAT));
			}
			precedingMeasure = measure;
		}
	}
	
	private void writeBeat(TGBeat beat, Node nodeBeat) {
		this.addNodeLong(nodeBeat, TAG_PRECISE_START, beat.getPreciseStart());
		TGStroke stroke = beat.getStroke();
		if (stroke.getDirection() != TGStroke.STROKE_NONE) {
			Node nodeStroke = this.addNode(nodeBeat, TAG_STROKE);
			this.addAttribute(nodeStroke, TAG_DIRECTION , this.mapWriteStroke.get(stroke.getDirection()));
			this.addAttributeInt(nodeStroke, TAG_VALUE, stroke.getValue());
		}
		TGPickStroke pickStroke = beat.getPickStroke();
		if (pickStroke.getDirection() != TGPickStroke.PICK_STROKE_NONE) {
			this.addNode(nodeBeat, TAG_PICK_STROKE, this.mapWritePickStroke.get(pickStroke.getDirection()));
		}
		TGChord chord = beat.getChord();
		if (chord != null) {
			Node nodeChord = this.addNode(nodeBeat, TAG_CHORD);
			this.addNode(nodeChord, TAG_NAME, chord.getName());
			this.addNodeInt(nodeChord, TAG_FIRSTFRET, chord.getFirstFret());
			for (int string : chord.getStrings()) {
				if (string >=0) {
					this.addNodeInt(nodeChord, TAG_STRING, string);
				} else {
					this.addNode(nodeChord, TAG_STRING);
				}
			}
		}
		if ((beat.getText()!=null) && !beat.getText().getValue().equals("")) {
			this.addNode(nodeBeat, TAG_TEXT, beat.getText().getValue());
		}
		for (int i=0; i<TGBeat.MAX_VOICES; i++) {
			this.writeVoice(beat.getVoice(i), this.addNode(nodeBeat, TAG_VOICE));
		}
	}
	
	private void writeVoice (TGVoice voice, Node nodeVoice) {
		Node nodeDuration = this.addNode(nodeVoice, TAG_DURATION);
		TGDivisionType divisionType = voice.getDuration().getDivision();
		if (divisionType != TGDivisionType.NORMAL) {
			Node nodeDivisionType = this.addNode(nodeDuration, TAG_DIVISIONTYPE);
			this.addAttributeInt(nodeDivisionType, TAG_ENTERS, divisionType.getEnters());
			this.addAttributeInt(nodeDivisionType, TAG_TIMES, divisionType.getTimes());
		}
		this.addAttributeInt(nodeDuration, TAG_VALUE, voice.getDuration().getValue());
		if (voice.getDuration().isDotted()) {
			this.addAttribute(nodeDuration, TAG_DOTTED, VAL_DOTTED);
		} else if (voice.getDuration().isDoubleDotted()) {
			this.addAttribute(nodeDuration, TAG_DOTTED, VAL_DOUBLEDOTTED);
		}
		TGNote previousNote = null;
		for (TGNote note : voice.getNotes()) {
			this.writeNote(note, previousNote, this.addNode(nodeVoice, TAG_NOTE));
			previousNote = note;
		}
		if (voice.getNotes().size() == 0) {
			this.addAttributeBool(nodeVoice, TAG_EMPTY, voice.isEmpty());
		}
		if (voice.getDirection() != TGVoice.DIRECTION_NONE) {
			this.addAttribute(nodeVoice, TAG_DIRECTION, mapWriteDirection.get(voice.getDirection()));
		}
	}
	
	private void writeNote(TGNote note, TGNote previousNote, Node nodeNote) {
		TGNoteEffect effect = note.getEffect();
		if (effect.isVibrato()) { this.addNode(nodeNote, TAG_VIBRATO); }
		if (effect.isDeadNote()) { this.addNode(nodeNote, TAG_DEADNOTE); }
		if (effect.isSlide()) { this.addNode(nodeNote, TAG_SLIDE); }
		if (effect.isHammer()) { this.addNode(nodeNote, TAG_HAMMER); }
		if (effect.isGhostNote()) { this.addNode(nodeNote, TAG_GHOSTNOTE); }
		if (effect.isAccentuatedNote()) { this.addNode(nodeNote, TAG_ACCENTUATEDNOTE); }
		if (effect.isHeavyAccentuatedNote()) { this.addNode(nodeNote, TAG_HEAVYACCENTUATEDNOTE); }
		if (effect.isPalmMute()) { this.addNode(nodeNote, TAG_PALMMUTE); }
		if (effect.isStaccato()) { this.addNode(nodeNote, TAG_STACCATO); }
		if (effect.isTapping()) { this.addNode(nodeNote, TAG_TAPPING); }
		if (effect.isSlapping()) { this.addNode(nodeNote, TAG_SLAPPING); }
		if (effect.isPopping()) { this.addNode(nodeNote, TAG_POPPING); }
		if (effect.isFadeIn()) { this.addNode(nodeNote, TAG_FADEIN); }
		if (effect.isLetRing()) { this.addNode(nodeNote, TAG_LETRING); }
		if (effect.isBend()) {
			Node nodeBend = this.addNode(nodeNote, TAG_BEND);
			for (BendPoint point : effect.getBend().getPoints()) {
				Node nodePoint = this.addNode(nodeBend, TAG_POINT);
				this.addAttributeInt(nodePoint, TAG_POSITION, point.getPosition());
				this.addAttributeInt(nodePoint, TAG_VALUE, point.getValue());
			}
		}
		if (effect.isTremoloBar()) {
			Node nodeTremoloBar = this.addNode(nodeNote, TAG_TREMOLOBAR);
			for (TremoloBarPoint point : effect.getTremoloBar().getPoints()) {
				Node nodePoint = this.addNode(nodeTremoloBar, TAG_POINT);
				this.addAttributeInt(nodePoint, TAG_POSITION, point.getPosition());
				this.addAttributeInt(nodePoint, TAG_VALUE, point.getValue());
			}
		}
		if (effect.isHarmonic()) {
			Node nodeHarmonic = this.addNode(nodeNote, TAG_HARMONIC);
			this.addAttribute(nodeHarmonic, TAG_TYPE, this.harmonicWritedMap.get(effect.getHarmonic().getType()));
			this.addAttributeInt(nodeHarmonic, TAG_DATA, effect.getHarmonic().getData());
		}
		if (effect.isGrace()) {
			TGEffectGrace grace = effect.getGrace();
			Node nodeGrace = this.addNode(nodeNote, TAG_GRACE);
			this.addAttributeInt(nodeGrace, TAG_FRET, grace.getFret());
			this.addAttributeInt(nodeGrace, TAG_DURATION, this.mapWriteGraceDuration.get(grace.getDuration()));
			this.addAttributeInt(nodeGrace, TAG_DYNAMIC, grace.getDynamic());
			this.addAttribute(nodeGrace, TAG_TRANSITION, this.mapWriteTransition.get(grace.getTransition()));
			this.addAttributeBool(nodeGrace, TAG_ONBEAT, grace.isOnBeat());
			this.addAttributeBool(nodeGrace, TAG_DEAD, grace.isDead());
		}
		if (effect.isTrill()) {
			Node nodeTrill = this.addNode(nodeNote, TAG_TRILL);
			this.addAttributeInt(nodeTrill, TAG_FRET, effect.getTrill().getFret());
			this.addAttributeInt(nodeTrill, TAG_DURATION, effect.getTrill().getDuration().getValue());
		}
		if (effect.isTremoloPicking()) {
			Node nodeTremoloPicking = this.addNode(nodeNote, TAG_TREMOLOPICKING);
			this.addAttributeInt(nodeTremoloPicking, TAG_DURATION, effect.getTremoloPicking().getDuration().getValue());
		}
		if (note.isAltEnharmonic()) {
			this.addNode(nodeNote, TAG_ALT_ENHARMONIC);
		}
		this.addAttributeInt(nodeNote, TAG_VALUE, note.getValue());
		this.addAttributeInt(nodeNote, TAG_STRING, note.getString());
		if (note.isTiedNote()) {
			this.addAttributeBool(nodeNote, TAG_TIEDNOTE, true);
		}
		if ((previousNote == null) || (previousNote.getVelocity() != note.getVelocity())) {
			this.addAttributeInt(nodeNote, TAG_VELOCITY, note.getVelocity());
		}
	}
	
	private void addVersion(OutputStream stream) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
		writer.write(VERSION_PREFIX + VERSION_SEPARATOR + FILE_FORMAT_TGVERSION.toString());
		writer.flush();
	}
	
	private void saveDocument(OutputStream stream) {
		try {
			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(this.document);
			Result output = new StreamResult(stream);
			idTransform.setOutputProperty(OutputKeys.INDENT, "no");
			idTransform.transform(input, output);
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	// TODO: share with MusicXMLWriter (copy-pasted)
	private Node addAttribute(Node node, String name, String content){
		Attr attribute = this.document.createAttribute(name);
		attribute.setNodeValue(content);
		node.getAttributes().setNamedItem(attribute);
		return node;
	}
	
	private Node addAttributeInt(Node node, String name, int value){
		return this.addAttribute(node, name, String.valueOf(value));
	}
	
	private Node addAttributeBool(Node node, String name, boolean value){
		return this.addAttribute(node, name, value ? "true" : "false");
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
	
	private Node addNodeInt(Node parent, String name, int value){
		return addNode(parent, name, String.valueOf(value));
	}
	
	private Node addNodeLong(Node parent, String name, long value){
		return addNode(parent, name, String.valueOf(value));
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
	
}
