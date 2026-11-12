package app.tuxguitar.io.tg;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToIntFunction;

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

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectBend.BendPoint;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar.TremoloBarPoint;
import app.tuxguitar.util.TGVersion;

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
		this.writeTempo(header.getTempo(), nodeMeasureHeader);
		this.writeRepeats(header, nodeMeasureHeader);
		if (header.hasMarker()) {
			Node nodeMarker = this.addNode(nodeMeasureHeader, TAG_MARKER, header.getMarker().getTitle());
			this.writeColor(header.getMarker().getColor(), nodeMarker);
		}
		if (header.getTripletFeel() != TGMeasureHeader.TRIPLET_FEEL_NONE) {
			this.addNode(nodeMeasureHeader, TAG_TRIPLET_FEEL, this.tripletsWriteMap.get(header.getTripletFeel()));
		}
		if (header.isLineBreak()) {
			this.addNode(nodeMeasureHeader, TAG_LINE_BREAK);
		}
	}

	private void writeTempo(TGTempo tempo, Node nodeMeasureHeader) {
		Node nodeTempo = this.addNodeInt(nodeMeasureHeader, TAG_TEMPO, tempo.getRawValue());
		if (tempo.getBase() != TGDuration.QUARTER) {
			this.addAttributeInt(nodeTempo, TAG_TEMPO_BASE, tempo.getBase());
		}
		if (tempo.isDotted()) {
			this.addAttribute(nodeTempo, TAG_TEMPO_DOTTED, "true");
		}
	}

	private void writeRepeats(TGMeasureHeader header, Node nodeMeasureHeader) {
		if (header.isRepeatOpen()) {
			this.addNode(nodeMeasureHeader, TAG_REPEAT_OPEN);
		}
		if (header.getRepeatClose() != 0) {
			this.addNodeInt(nodeMeasureHeader, TAG_REPEAT_CLOSE, header.getRepeatClose());
		}
		if (header.getRepeatAlternative() != 0) {
			this.writeRepeatAlternatives(header.getRepeatAlternative(), this.addNode(nodeMeasureHeader, TAG_REPEAT_ALTERNATIVE));
		}
	}

	// bit map: one alternative for each bit set, 1 for the lowest bit
	private void writeRepeatAlternatives(int repeatAlternative, Node node) {
		int flag = repeatAlternative;
		int alternative = 1;
		while (flag != 0) {
			if ((flag & 0x01) != 0) {
				this.addNodeInt(node, TAG_ALTERNATIVE, alternative);
			}
			flag >>= 1;
			alternative ++;
		}
	}

	private void writeColor(TGColor color, Node node) {
		this.addAttributeInt(node, TAG_COLOR_R, color.getR());
		this.addAttributeInt(node, TAG_COLOR_G, color.getG());
		this.addAttributeInt(node, TAG_COLOR_B, color.getB());
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
		this.writeColor(track.getColor(), this.addNode(nodeTrack, TAG_COLOR));
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
		for (NoteFlag flag : NOTE_FLAGS) {
			if (flag.isOn(effect)) {
				this.addNode(nodeNote, flag.getTag());
			}
		}
		this.writeNoteEffects(effect, nodeNote);
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

	// the effects of a note that have values, each an element of the note
	private void writeNoteEffects(TGNoteEffect effect, Node nodeNote) {
		if (effect.isBend()) {
			this.writePoints(effect.getBend().getPoints(), BendPoint::getPosition, BendPoint::getValue, this.addNode(nodeNote, TAG_BEND));
		}
		if (effect.isTremoloBar()) {
			this.writePoints(effect.getTremoloBar().getPoints(), TremoloBarPoint::getPosition, TremoloBarPoint::getValue, this.addNode(nodeNote, TAG_TREMOLOBAR));
		}
		if (effect.isHarmonic()) {
			Node nodeHarmonic = this.addNode(nodeNote, TAG_HARMONIC);
			this.addAttribute(nodeHarmonic, TAG_TYPE, this.harmonicWritedMap.get(effect.getHarmonic().getType()));
			this.addAttributeInt(nodeHarmonic, TAG_DATA, effect.getHarmonic().getData());
		}
		if (effect.isGrace()) {
			this.writeGrace(effect.getGrace(), this.addNode(nodeNote, TAG_GRACE));
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
	}

	private <P> void writePoints(List<P> points, ToIntFunction<P> position, ToIntFunction<P> value, Node nodePoints) {
		for (P point : points) {
			Node nodePoint = this.addNode(nodePoints, TAG_POINT);
			this.addAttributeInt(nodePoint, TAG_POSITION, position.applyAsInt(point));
			this.addAttributeInt(nodePoint, TAG_VALUE, value.applyAsInt(point));
		}
	}

	private void writeGrace(TGEffectGrace grace, Node nodeGrace) {
		this.addAttributeInt(nodeGrace, TAG_FRET, grace.getFret());
		this.addAttributeInt(nodeGrace, TAG_DURATION, this.mapWriteGraceDuration.get(grace.getDuration()));
		this.addAttributeInt(nodeGrace, TAG_DYNAMIC, grace.getDynamic());
		this.addAttribute(nodeGrace, TAG_TRANSITION, this.mapWriteTransition.get(grace.getTransition()));
		this.addAttributeBool(nodeGrace, TAG_ONBEAT, grace.isOnBeat());
		this.addAttributeBool(nodeGrace, TAG_DEAD, grace.isDead());
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
