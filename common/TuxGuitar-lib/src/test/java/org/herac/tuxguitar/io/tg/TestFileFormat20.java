package org.herac.tuxguitar.io.tg;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatUtils;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;
import org.herac.tuxguitar.util.TGMessagesManager;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/* Objective of this class is to test file format 2.0: compressed xml
 * especially the absence of regression with binary file format (1.5)
 */

public class TestFileFormat20 {
	private static final String XSD_SCHEMA = "tuxguitar_20.xsd";
	
	@Test
	public void testRecognizeValidFile() throws FileNotFoundException {
		assertTrue(validatesSchema("Untitled_20.xml", false));
		assertTrue(validatesSchema("Untitled_20.tg", true));
		assertTrue(detectsFormat("Untitled_20.tg"));
		assertTrue(validatesSchema("test_20.xml", false));
		assertTrue(validatesSchema("test_20.tg", true));
		assertTrue(detectsFormat("test_20.tg"));
	}
	
	@Test
	public void testXmlCanBeExtended() throws FileNotFoundException {
		assertTrue(validatesSchema("test_extended_21.xml", false));
		assertTrue(validatesSchema("Untitled_extended_21.xml", false));
		assertTrue(detectsFormat("Untitled_extended_21.tg"));
	}
	
	@Test
	public void testCanOpenCompatibleExtendedFormat() throws IOException {
		// compressed xml + version file
		// check context attribute has been set to suggest app upgrade to user
		TGSongReaderHandle  handle = readSong("Untitled_extended_21.tg", true);
		assertNotNull(handle.getSong());
		assertTrue(handle.isNewerFileFormatDetected()); 
	}
	
	@Test
	public void testCanOpenValidFile() throws IOException {
		// test with expected format version, flag shall not be set
		TGSongReaderHandle handle = readSong("Untitled_20.tg", true);
		assertNotNull(handle.getSong());
		assertFalse(handle.isNewerFileFormatDetected()); 
	}
	
	@Test
	public void testInvalidFile() throws FileNotFoundException {
		// invalid version
		assertFalse(validatesSchema("Untitled_extended_30.tg", true));
		
		// not even an xml file (just random bytes)
		assertFalse(validatesSchema("randomBytes.xml", false));
		
		// valid xml, but not TuxGuitar
		assertFalse(validatesSchema("notTuxGuitar.xml", false));
		
		// correct xml, but no version file
		assertFalse(detectsFormat("noVersion.tg"));
		
		// correct xml, but invalid version file
		assertFalse(detectsFormat("invalidVersion.tg"));
	}
	
	@Test
	public void testNewMajorVersionIsDetected () throws IOException {
		// positive test: new major version detected
		boolean exceptionCaught = false;
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setContext(new TGSongStreamContext());
		handle.setInputStream(getClass().getClassLoader().getResource("Untitled_extended_30.tg").openStream());
		handle.setFactory(new TGFactory());
		TGSongReaderImpl songReader = new TGSongReaderImpl();
		try {
			songReader.read(handle);
		} catch(TGFileFormatException e) {
			assertEquals(TGMessagesManager.getProperty("error.new-major-version"), e.getMessage());
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
		
		// negative test: new major version NOT detected
		exceptionCaught = false;
		handle = new TGSongReaderHandle();
		handle.setSong(null);
		handle.setContext(new TGSongStreamContext());
		handle.setInputStream(getClass().getClassLoader().getResource("notTuxGuitar.tg").openStream());
		handle.setFactory(new TGFactory());
		songReader = new TGSongReaderImpl();
		try {
			songReader.read(handle);
		} catch(TGFileFormatException e) {
			assertFalse(TGMessagesManager.getProperty("error.new-major-version").equals(e.getMessage()));
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
	}
	
	// manually written xml file to check syntax
	@Test
	public void testOpenValidXMLFile() throws IOException {
		TGSongReaderHandle handle = readSong("test_20.xml", false);
		TGSong song = handle.getSong();
		assertEquals("TGSong.name", song.getName());
		assertEquals("TGSong.artist", song.getArtist());
		assertEquals("TGSong.album", song.getAlbum());
		assertEquals("TGSong.author", song.getAuthor());
		assertEquals("TGSong.date", song.getDate());
		assertEquals("TGSong.copyright", song.getCopyright());
		assertEquals("TGSong.writer", song.getWriter());
		assertEquals("TGSong.transcriber", song.getTranscriber());
		assertEquals("TGSong.comments\n2nd line", song.getComments());
		assertEquals(2, song.countChannels());

		TGChannel channel = song.getChannel(0);
		assertEquals(1, channel.getChannelId());
		assertEquals(0, channel.getBank());
		assertEquals(25, channel.getProgram());
		assertEquals(127, channel.getVolume());
		assertEquals(64, channel.getBalance());
		assertEquals(10, channel.getChorus());
		assertEquals(20, channel.getReverb());
		assertEquals(30, channel.getPhaser());
		assertEquals(40, channel.getTremolo());
		assertEquals("ChannelName", channel.getName());
		Iterator<TGChannelParameter> parameters = channel.getParameters();
		assertTrue(parameters.hasNext());
		TGChannelParameter parameter = parameters.next();
		assertEquals("param1.key", parameter.getKey());
		assertEquals("param1.value", parameter.getValue());
		assertTrue(parameters.hasNext());
		parameter = parameters.next();
		assertEquals("param2.key", parameter.getKey());
		assertEquals("param2.value", parameter.getValue());
		assertFalse(parameters.hasNext());

		channel = song.getChannel(1);
		assertEquals("ChannelName2", channel.getName());
		parameters = channel.getParameters();
		assertFalse(parameters.hasNext());
		
		// measure headers
		Iterator<TGMeasureHeader> headers = song.getMeasureHeaders();
		assertTrue(headers.hasNext());
		// header 1
		TGMeasureHeader header = headers.next();
		assertEquals(song, header.getSong());
		assertEquals(110, header.getTempo().getValue());
		assertEquals(3, header.getTimeSignature().getNumerator());
		assertEquals(8, header.getTimeSignature().getDenominator().getValue());
		assertTrue(header.isRepeatOpen());
		assertEquals(0, header.getRepeatClose());
		assertEquals(0, header.getRepeatAlternative());
		assertNull(header.getMarker());
		assertEquals(TGMeasureHeader.TRIPLET_FEEL_NONE,header.getTripletFeel());
		// header 2
		assertTrue(headers.hasNext());
		header = headers.next();
		assertEquals(110, header.getTempo().getValue());
		assertEquals(3, header.getTimeSignature().getNumerator());
		assertEquals(8, header.getTimeSignature().getDenominator().getValue());
		assertFalse(header.isRepeatOpen());
		assertEquals(0, header.getRepeatClose());
		assertEquals(5, header.getRepeatAlternative());
		TGMarker marker = header.getMarker();
		assertNotNull(marker);
		assertEquals(2, marker.getMeasure());
		assertEquals(marker.getTitle(), "header2.marker");
		assertEquals(255, marker.getColor().getR());
		assertEquals(1, marker.getColor().getG());
		assertEquals(2, marker.getColor().getB());
		assertEquals(2, marker.getMeasure());
		assertEquals(TGMeasureHeader.TRIPLET_FEEL_NONE,header.getTripletFeel());
		// header 3
		assertTrue(headers.hasNext());
		header = headers.next();
		assertEquals(40, header.getTempo().getValue());
		assertEquals(1, header.getRepeatClose());
		assertEquals(TGMeasureHeader.TRIPLET_FEEL_EIGHTH,header.getTripletFeel());
		// header 4
		assertTrue(headers.hasNext());
		header = headers.next();
		// no more
		assertFalse(headers.hasNext());
		
		// Track1
		Iterator<TGTrack> tracks = song.getTracks();
		assertTrue(tracks.hasNext());
		TGTrack track = tracks.next();
		assertEquals(song, track.getSong());
		assertEquals(1, track.getNumber());
		assertEquals(track.getName(), "Track1.name");
		assertTrue(track.isSolo());
		assertFalse(track.isMute());
		assertEquals(1, track.getChannelId());
		assertEquals(-1, track.getOffset());
		assertEquals(10, track.getColor().getR());
		assertEquals(20, track.getColor().getG());
		assertEquals(30, track.getColor().getB());
		assertEquals(3, track.stringCount());
		assertEquals(200, track.getString(1).getValue());
		assertEquals(100, track.getString(2).getValue());
		assertEquals(50, track.getString(3).getValue());
		assertEquals("test lyrics", track.getLyrics().getLyrics());
		// track 1 measures
		Iterator<TGMeasure> measures = track.getMeasures();
		assertTrue(measures.hasNext());
		TGMeasure measure = measures.next();
		assertEquals(song.getMeasureHeader(0), measure.getHeader());
		assertEquals(track, measure.getTrack());
		assertEquals(1, measure.getNumber());
		assertEquals(TGMeasure.CLEF_TREBLE, measure.getClef());
		assertEquals(8, measure.getKeySignature());
		// track1, measure 1, beat 1
		List<TGBeat> beats = measure.getBeats();
		assertEquals(1, beats.size());
		TGBeat beat = beats.get(0);
		assertEquals(960, beat.getStart());
		assertEquals(measure, beat.getMeasure());
		assertEquals(TGStroke.STROKE_UP, beat.getStroke().getDirection());
		assertEquals(2, beat.getStroke().getValue());
		TGChord chord = beat.getChord();
		assertNotNull(chord);
		assertEquals(beat, chord.getBeat());
		assertEquals("Bm", chord.getName());
		assertEquals(2, chord.getFirstFret());
		assertEquals(6, chord.countStrings());
		assertEquals(2, chord.getFretValue(0));
		assertEquals(3, chord.getFretValue(1));
		assertEquals(4, chord.getFretValue(2));
		assertEquals(4, chord.getFretValue(3));
		assertEquals(2, chord.getFretValue(4));
		assertEquals(-1, chord.getFretValue(5));
		assertEquals("Beat1.text", beat.getText().getValue());
		// track1, measure 1, beat1, voice 1
		assertEquals(2, beat.countVoices());
		TGVoice voice = beat.getVoice(0);
		assertEquals(beat, voice.getBeat());
		assertEquals(0, voice.getIndex());
		assertEquals(TGDuration.EIGHTH, voice.getDuration().getValue());
		assertFalse(voice.getDuration().isDotted());
		assertTrue(voice.getDuration().isDoubleDotted());
		assertEquals(TGDivisionType.NORMAL.getEnters(), voice.getDuration().getDivision().getEnters());
		assertEquals(TGDivisionType.NORMAL.getTimes(), voice.getDuration().getDivision().getTimes());
		// track1, measure 1, beat1, voice 1, note 1
		assertFalse(voice.isEmpty());
		List<TGNote> notes = voice.getNotes();
		assertEquals(6, notes.size());
		TGNote note = notes.get(0);
		assertEquals(voice, note.getVoice());
		assertEquals(12, note.getValue());
		assertEquals(90, note.getVelocity());
		assertEquals(1, note.getString());
		assertFalse(note.isTiedNote());
		TGNoteEffect effect = note.getEffect();
		assertTrue(effect.isVibrato());
		assertTrue(effect.isDeadNote());
		assertFalse(effect.isSlide());
		assertFalse(effect.isHammer());
		assertTrue(effect.isGhostNote());
		assertFalse(effect.isAccentuatedNote());
		assertFalse(effect.isHeavyAccentuatedNote());
		assertFalse(effect.isPalmMute());
		assertFalse(effect.isStaccato());
		assertFalse(effect.isTapping());
		assertFalse(effect.isSlapping());
		assertFalse(effect.isPopping());
		assertFalse(effect.isFadeIn());
		assertFalse(effect.isLetRing());
		assertFalse(effect.isBend());
		// track1, measure 1, beat1, voice 1, note 2
		note = notes.get(1);
		assertEquals(voice, note.getVoice());
		assertEquals(1, note.getValue());
		assertEquals(90, note.getVelocity());
		assertEquals(2, note.getString());
		assertTrue(note.isTiedNote());
		effect = note.getEffect();
		assertFalse(effect.isVibrato());
		assertFalse(effect.isDeadNote());
		assertFalse(effect.isGhostNote());
		assertFalse(effect.isSlide());
		assertFalse(effect.isHammer());
		assertFalse(effect.isAccentuatedNote());
		assertTrue(effect.isHeavyAccentuatedNote());
		assertFalse(effect.isPalmMute());
		assertFalse(effect.isStaccato());
		assertFalse(effect.isTapping());
		assertFalse(effect.isSlapping());
		assertFalse(effect.isPopping());
		assertFalse(effect.isFadeIn());
		assertFalse(effect.isLetRing());
		assertFalse(effect.isBend());
		// track1, measure 1, beat1, voice 1, note 3
		note = notes.get(2);
		effect = note.getEffect();
		assertTrue(effect.isBend());
		TGEffectBend bend = note.getEffect().getBend();
		assertNotNull(bend);
		assertEquals(2, bend.getPoints().size());
		assertEquals(1, bend.getPoints().get(0).getPosition());
		assertEquals(2, bend.getPoints().get(0).getValue());
		assertEquals(3, bend.getPoints().get(1).getPosition());
		assertEquals(4, bend.getPoints().get(1).getValue());
		// track1, measure 1, beat1, voice 1, note 4
		note = notes.get(3);
		effect = note.getEffect();
		assertTrue(effect.isTremoloBar());
		TGEffectTremoloBar tremoloBar = effect.getTremoloBar();
		assertNotNull(tremoloBar);
		assertEquals(2, tremoloBar.getPoints().size());
		assertEquals(5, tremoloBar.getPoints().get(0).getPosition());
		assertEquals(6, tremoloBar.getPoints().get(0).getValue());
		assertEquals(7, tremoloBar.getPoints().get(1).getPosition());
		assertEquals(8, tremoloBar.getPoints().get(1).getValue());
		// track1, measure 1, beat1, voice 1, note 5
		note = notes.get(4);
		effect = note.getEffect();
		assertTrue(effect.isHarmonic());
		TGEffectHarmonic harmonic = effect.getHarmonic();
		assertNotNull(harmonic);
		assertEquals(TGEffectHarmonic.TYPE_ARTIFICIAL, harmonic.getType());
		assertEquals(12, harmonic.getData());
		// track1, measure 1, beat1, voice 1, note 6
		note = notes.get(5);
		effect = note.getEffect();
		assertTrue(effect.isGrace());
		TGEffectGrace grace = effect.getGrace();
		assertNotNull(grace);
		assertEquals(5, grace.getFret());
		assertEquals(1, grace.getDuration());
		assertEquals(50, grace.getDynamic());
		assertEquals(TGEffectGrace.TRANSITION_SLIDE, grace.getTransition());
		assertTrue(grace.isOnBeat());
		assertFalse(grace.isDead());
		
		// track1, measure 1, beat1, voice 2
		voice = beat.getVoice(1);
		assertEquals(beat, voice.getBeat());
		assertEquals(1, voice.getIndex());
		assertEquals(TGDuration.HALF, voice.getDuration().getValue());
		assertFalse(voice.getDuration().isDotted());
		assertFalse(voice.getDuration().isDoubleDotted());
		assertEquals(TGDivisionType.DIVISION_TYPES[1].getEnters(), voice.getDuration().getDivision().getEnters());
		assertEquals(TGDivisionType.DIVISION_TYPES[1].getTimes(), voice.getDuration().getDivision().getTimes());
		assertTrue(voice.isEmpty());
		
		// track 1, other measures
		for (int i=0; i<3; i++) {
			assertTrue(measures.hasNext());
			measure = measures.next();
			assertEquals(i+2, measure.getNumber());
		}
		assertFalse(measures.hasNext());
		
		// Track2
		assertTrue(tracks.hasNext());
		track = tracks.next();
		assertEquals(2, track.getNumber());
		assertEquals(track.getName(), "Track2.name");
		assertFalse(track.isSolo());
		assertFalse(track.isMute());
		assertEquals(2, track.getChannelId());
		assertEquals(0, track.getOffset());
		assertEquals(21, track.getColor().getR());
		assertEquals(22, track.getColor().getG());
		assertEquals(23, track.getColor().getB());
		effect = track.getMeasure(0).getBeat(0).getVoice(0).getNote(0).getEffect();
		assertTrue(effect.isTrill());
		TGEffectTrill trill = effect.getTrill();
		assertNotNull(trill);
		assertEquals(3, trill.getFret());
		assertEquals(32, trill.getDuration().getValue());
		effect = track.getMeasure(0).getBeat(0).getVoice(0).getNote(1).getEffect();
		assertTrue(effect.isTremoloPicking());
		TGEffectTremoloPicking tremoloPicking = effect.getTremoloPicking();
		assertNotNull(tremoloPicking);
		assertEquals(16, tremoloPicking.getDuration().getValue());
		
		// no more tracks
		assertFalse(tracks.hasNext());
	}

	@Test
	public void testWrittenFileValidatesSchema() throws FileNotFoundException, Throwable {
		// without compression
		assertTrue(validatesSchema(new ByteArrayInputStream(tg20ToXml("Untitled_20.tg", false)), false));
		assertTrue(validatesSchema(new ByteArrayInputStream(tg20ToXml("reference_20.tg", false)), false));
		// with compression
		assertTrue(detectsFormat(new ByteArrayInputStream(tg20ToXml("Untitled_20.tg", true))));
		assertTrue(validatesSchema(new ByteArrayInputStream(tg20ToXml("Untitled_20.tg", true)), true));
		assertTrue(detectsFormat(new ByteArrayInputStream(tg20ToXml("reference_20.tg", true))));
		assertTrue(validatesSchema(new ByteArrayInputStream(tg20ToXml("reference_20.tg", true)), true));
	}
	
	// new feature introduced in format version 2.0
	@Test
	public void testCheckLineBreak() throws IOException {
		TGFactory factory = new TGFactory();
		
		// read ref file (no line break)
		TGSongReaderHandle handle = readSong("reference_20.tg", true);
		TGSong song = handle.getSong();
		Iterator<TGMeasureHeader> headers = song.getMeasureHeaders();
		while (headers.hasNext()) {
			assertFalse(headers.next().isLineBreak());
		}
		// add one line break
		song.getMeasureHeader(4).setLineBreak(true);
		// save song under xml format in byte buffer
		byte[] bufferXml = saveToXml(song, factory);
		// check file validates xsd schema
		assertTrue(validatesSchema(new ByteArrayInputStream(bufferXml), false));
		// re-read, check lineBreak is here
		song = readFromXml(bufferXml, factory);
		headers = song.getMeasureHeaders();
		TGMeasureHeader header = null;
		while (headers.hasNext()) {
			header = headers.next();
			assertEquals(header.isLineBreak(), (header.getNumber() == 5));
		}
		assertTrue(header.getNumber() > 5);
	}
	
	@Test
	public void testMaxFret() throws IOException {
		TGFactory factory = new TGFactory();
		TGSongReaderHandle handle = readSong("reference_20.tg", true);
		TGSong song = handle.getSong();
		assertEquals(TGTrack.DEFAULT_MAX_FRET, song.getTrack(0).getMaxFret());
		assertEquals(TGTrack.DEFAULT_MAX_FRET, song.getTrack(2).getMaxFret());
		song.getTrack(0).setMaxFret(TGTrack.DEFAULT_MAX_FRET+1);
		song.getTrack(1).setMaxFret(TGTrack.DEFAULT_MAX_FRET+2); // shall be discarded: percussion track
		song.getTrack(2).setMaxFret(TGTrack.DEFAULT_MAX_FRET+3);
		// save, and re-read
		byte[] bufferXml = saveToXml(song, factory);
		assertTrue(validatesSchema(new ByteArrayInputStream(bufferXml), false));
		song = readFromXml(bufferXml, factory);
		assertEquals(TGTrack.DEFAULT_MAX_FRET+1, song.getTrack(0).getMaxFret());
		assertEquals(TGTrack.DEFAULT_MAX_FRET, song.getTrack(1).getMaxFret());
		assertEquals(TGTrack.DEFAULT_MAX_FRET+3, song.getTrack(2).getMaxFret());
	}

	
	private byte[] saveToXml(TGSong song, TGFactory factory) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TGSongWriterHandle handleWrite = new TGSongWriterHandle();
		handleWrite.setFactory(factory);
		handleWrite.setSong(song);
		handleWrite.setOutputStream(outputStream);
		TGSongWriterImpl writer = new TGSongWriterImpl();
		writer.writeContent(handleWrite);
		return outputStream.toByteArray();
	}
	
	private TGSong readFromXml(byte[] bufferXml, TGFactory factory) {
		TGSongReaderHandle handleRead = new TGSongReaderHandle();
		handleRead.setFactory(factory);
		handleRead.setInputStream(new ByteArrayInputStream(bufferXml));
		TGSongReaderImpl reader = new TGSongReaderImpl();
		reader.readContent(handleRead, handleRead.getInputStream());
		return handleRead.getSong();
	}
	
	private boolean validatesSchema(InputStream inputStream, boolean compressed) {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			URL urlXsd = getClass().getClassLoader().getResource(XSD_SCHEMA);
			schema = factory.newSchema(urlXsd);
			InputStream streamToValidate = (compressed ? new TGStream().getDecompressedContent(inputStream) : inputStream);
			schema.newValidator().validate(new StreamSource(streamToValidate));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean validatesSchema(String resourceFileName, boolean compressed) throws FileNotFoundException {
		File xml = new File(getClass().getClassLoader().getResource(resourceFileName).getFile());
		return validatesSchema(new FileInputStream(xml), compressed);
	}
	
	private boolean detectsFormat(InputStream inputStream) {
		TGFileFormatDetectorImpl formatDetector = new TGFileFormatDetectorImpl();
		TGFileFormat format = null;
		format = formatDetector.getFileFormat(inputStream);
		return (format != null);
	}
	
	private boolean detectsFormat(String resourceFileName) throws FileNotFoundException {
		File xml = new File(getClass().getClassLoader().getResource(resourceFileName).getFile());
		return detectsFormat(new FileInputStream(xml));
	}
	
	public TGSongReaderHandle readSong(String resourceFileName, boolean compressed) throws IOException {
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setContext(new TGSongStreamContext());
		handle.setInputStream(getClass().getClassLoader().getResource(resourceFileName).openStream());
		handle.setFactory(new TGFactory());
		TGSongReaderImpl songReader = new TGSongReaderImpl();
		if (compressed) {
			songReader.read(handle);
		} else {
			songReader.readContent(handle, handle.getInputStream());
		}
		return handle;
	}
	
	private byte[] tg20ToXml(String resourceFileName, boolean compressed) throws FileNotFoundException, Throwable {
		TGFactory factory = new TGFactory();
		// load original tg file
		File original = new File(getClass().getClassLoader().getResource(resourceFileName).getFile());
		byte[] bufferOriginal = TGFileFormatUtils.getBytes(new FileInputStream(original));
		TGSongReaderHandle handleRead = new TGSongReaderHandle();
		handleRead.setFactory(factory);
		handleRead.setInputStream(new ByteArrayInputStream(bufferOriginal));
		new TGSongReaderImpl().read(handleRead);
		
		// save song under xml format in byte buffer
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TGSongWriterHandle handleWrite = new TGSongWriterHandle();
		handleWrite.setFactory(factory);
		handleWrite.setSong(handleRead.getSong());
		handleWrite.setOutputStream(outputStream);
		TGSongWriterImpl writer = new TGSongWriterImpl();
		if (compressed) {
			writer.write(handleWrite);
		} else {
			writer.writeContent(handleWrite);
		}
		return outputStream.toByteArray();
	}

}
