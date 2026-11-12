package app.tuxguitar.io.midi;

import static app.tuxguitar.io.midi.MidiBytes.file;
import static app.tuxguitar.io.midi.MidiBytes.track;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.resource.TGResourceLoader;
import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

/**
 * Bytes -> song: MIDI files built byte by byte are read into a song, and the song is described as text.
 *
 * Files use 24 ticks per quarter note, so a 4/4 measure is 96 ticks and every delta fits in one byte.
 * A beat is described as "start:duration content", the start relative to its measure in song ticks
 * (960 per quarter), the content "rest" or the note pitches, "~" marking a tied note.
 */
public class TestMidiSongReader {

	private static final int Q = 24;
	private static final int TEXT = 0x01;
	private static final int TRACK_NAME = 0x03;
	private static final int TEMPO = 0x51;
	private static final int TIME_SIGNATURE = 0x58;
	private static final int VOLUME = 7;
	private static final int BALANCE = 10;
	private static final int SUSTAIN = 64;

	// ---- notes and rests

	@Test
	public void readsNotesAndRestsIntoBeats() throws Exception {
		TGSong song = read(unquantized(), track(on(0, 0, 60), off(Q, 0, 60), on(Q, 0, 62), off(2 * Q, 0, 62)));

		assertEquals(1, song.countTracks());
		assertEquals(1, song.countMeasureHeaders());
		assertEquals(Arrays.asList("0:4 [60]", "960:4 rest", "1920:2 [62]"), beats(song, 0, 0));
	}

	@Test
	public void quantizesNoteLengthsByDefault() throws Exception {
		TGSong song = read(null, track(on(0, 0, 60), off(Q - 1, 0, 60), on(Q + 1, 0, 62), off(Q, 0, 62)));

		assertEquals(Arrays.asList("0:4 [60]", "960:4 rest", "1920:4 [62]", "2880:4 rest"), beats(song, 0, 0));
	}

	@Test
	public void keepsUnquantizedLengthsWhenQuantizationIsOff() throws Exception {
		TGSong song = read(unquantized(), track(
				on(0, 0, 60), off(Q / 2, 0, 60), on(Q / 2, 0, 62), off(Q, 0, 62), on(0, 0, 64), off(2 * Q, 0, 64)));

		assertEquals(Arrays.asList("0:8 [60]", "480:8 rest", "960:4 [62]", "1920:2 [64]"), beats(song, 0, 0));
	}

	// looks wrong: without quantization a gap is filled with the longest plain duration that fits (a half for
	// three quarters), so the quarter note on beat 4 starts on beat 3 and lasts a half
	@Test
	public void startsNoteEarlyAfterAGapThatIsNoPlainDuration() throws Exception {
		TGSong song = read(unquantized(), track(on(3 * Q, 0, 60), off(Q, 0, 60)));

		assertEquals(Arrays.asList("0:2 rest", "1920:2 [60]"), beats(song, 0, 0));
	}

	@Test
	public void playsSimultaneousNotesInOneBeat() throws Exception {
		TGSong song = read(unquantized(), track(on(0, 0, 60), on(0, 0, 64), off(2 * Q, 0, 60), off(0, 0, 64)));

		assertEquals(Arrays.asList("0:2 [60, 64]", "1920:2 rest"), beats(song, 0, 0));
	}

	@Test
	public void tiesNoteAcrossTheBarLine() throws Exception {
		TGSong song = read(unquantized(), track(on(2 * Q, 0, 60), off(4 * Q, 0, 60)));

		assertEquals(2, song.countMeasureHeaders());
		assertEquals(Arrays.asList("0:2 rest", "1920:2 [60]"), beats(song, 0, 0));
		assertEquals(Arrays.asList("0:2 [60~]", "1920:2 rest"), beats(song, 0, 1));
	}

	@Test
	public void restartsNoteEndedAndStartedAtTheSameTick() throws Exception {
		// the second "note on" comes before the first "note off" in the file, both at tick 24
		TGSong song = read(unquantized(), track(on(0, 0, 60), on(Q, 0, 60), off(0, 0, 60), off(Q, 0, 60)));

		assertEquals(Arrays.asList("0:4 [60]", "960:4 [60]", "1920:2 rest"), beats(song, 0, 0));
	}

	@Test
	public void noteOnWithoutVelocityEndsTheNote() throws Exception {
		TGSong song = read(unquantized(), track(on(0, 0, 60), noteOn(Q, 0, 60, 0)));

		assertEquals(Arrays.asList("0:4 [60]", "960:4 rest", "1920:4 rest", "2880:4 rest"), beats(song, 0, 0));
	}

	@Test
	public void transposesNotes() throws Exception {
		MidiSettings settings = unquantized();
		settings.setTranspose(12);
		TGSong song = read(settings, track(on(0, 0, 60), off(4 * Q, 0, 60)));

		assertEquals(Arrays.asList("0:1 [72]"), beats(song, 0, 0));
	}

	@Test
	public void ignoresOtherMessages() throws Exception {
		TGSong song = read(unquantized(), track(
				on(0, 0, 60), event(0, 0xe0, 0x00, 0x40), control(0, 0, SUSTAIN, 0x7f), meta(0, TEXT, 'x'),
				meta(0, 0x90, 60, 100), off(4 * Q, 0, 60)));

		assertEquals(Arrays.asList("0:1 [60]"), beats(song, 0, 0));
		assertEquals(127, song.getChannel(0).getVolume());
	}

	// ---- song structure

	@Test
	public void namesTheSongFromTheFirstTrackAndTracksFromTheOthers() throws Exception {
		TGSong song = read(unquantized(),
				track(meta(0, TRACK_NAME, 'S', 'o', 'n', 'g'), on(0, 0, 60), off(Q, 0, 60)),
				track(meta(0, TRACK_NAME, 'B', 'a', 's', 's'), on(0, 1, 40), off(Q, 1, 40)));

		assertEquals("Song", song.getName());
		assertEquals(Arrays.asList("1 '' channel 1", "2 'Bass' channel 2"), tracks(song));
	}

	@Test
	public void fillsTracksUpToTheLastMeasure() throws Exception {
		TGSong song = read(unquantized(),
				track(on(0, 0, 60), off(Q, 0, 60)),
				track(on(0, 1, 40), meta(5 * Q, TEXT), off(4 * Q, 1, 40)));

		assertEquals(3, song.countMeasureHeaders());
		assertEquals(3, song.getTrack(0).countMeasures());
		assertEquals(Arrays.asList("0:1 rest"), beats(song, 0, 2));
	}

	@Test
	public void keepsNamedTrackWithoutNotes() throws Exception {
		TGSong song = read(unquantized(),
				track(on(0, 0, 60), off(Q, 0, 60)),
				track(meta(0, TRACK_NAME, 'E', 'm', 'p', 't', 'y')));

		assertEquals(Arrays.asList("1 '' channel 1", "2 'Empty' channel -1"), tracks(song));
		// looks wrong: the fallback tuning of a track without notes is built from no note range
		assertEquals(Arrays.asList(-21, -17, -13, -9, -5, -1), strings(song.getTrack(1)));
	}

	@Test
	public void rejectsFileWithoutNotes() {
		assertThrows(TGFileFormatException.class, () -> read(unquantized(), track(meta(0, TEXT, 'x'))));
	}

	@Test
	public void rejectsBytesThatAreNotMidi() {
		assertThrows(TGFileFormatException.class, () -> read(new byte[] {1, 2, 3, 4}, handle(unquantized())));
	}

	@Test
	public void readsWithDefaultSettingsWhenTheHandleHasNoContext() throws Exception {
		TGSongReaderHandle handle = handle(null);
		handle.setContext(null);

		TGSong song = read(file(Q, track(on(0, 0, 60), off(Q - 1, 0, 60))), handle);

		// quantized by default: the note is a full quarter
		assertEquals(Arrays.asList("0:4 [60]", "960:4 rest", "1920:4 rest", "2880:4 rest"), beats(song, 0, 0));
	}

	// ---- measure headers

	@TestFactory
	public Stream<DynamicTest> readsTimeSignatureDenominator() {
		return Stream.of(
				denominator(0, TGDuration.WHOLE),
				denominator(1, TGDuration.HALF),
				denominator(2, TGDuration.QUARTER),
				denominator(3, TGDuration.EIGHTH),
				denominator(4, TGDuration.SIXTEENTH),
				denominator(5, TGDuration.THIRTY_SECOND),
				// 1/64 and anything else are read as quarters
				denominator(6, TGDuration.QUARTER),
				denominator(0xff, TGDuration.QUARTER));
	}

	@Test
	public void ignoresTimeSignatureWithoutDenominator() throws Exception {
		TGSong song = read(unquantized(), track(meta(0, TIME_SIGNATURE, 3), on(0, 0, 60), off(Q, 0, 60)));

		assertEquals(Arrays.asList("1 @960 4/4 120"), headers(song));
	}

	@Test
	public void readsTempo() throws Exception {
		// 1 000 000 microseconds per quarter note
		TGSong song = read(unquantized(), track(meta(0, TEMPO, 0x0f, 0x42, 0x40), on(0, 0, 60), off(Q, 0, 60)));

		assertEquals(Arrays.asList("1 @960 4/4 60"), headers(song));
	}

	@Test
	public void ignoresTempoWithoutThreeBytes() throws Exception {
		TGSong song = read(unquantized(), track(meta(0, TEMPO, 0x0f, 0x42), on(0, 0, 60), off(Q, 0, 60)));

		assertEquals(Arrays.asList("1 @960 4/4 120"), headers(song));
	}

	@Test
	public void newMeasuresFollowTheLastTimeSignatureAndTempo() throws Exception {
		TGSong song = read(unquantized(), track(
				meta(0, TIME_SIGNATURE, 3, 2, 24, 8), meta(0, TEMPO, 0x0f, 0x42, 0x40), on(0, 0, 60), meta(4 * Q, TEXT), off(3 * Q, 0, 60)));

		assertEquals(Arrays.asList("1 @960 3/4 60", "2 @3840 3/4 60", "3 @6720 3/4 60"), headers(song));
	}

	@Test
	public void changesTempoInALaterMeasure() throws Exception {
		TGSong song = read(unquantized(), track(on(0, 0, 60), off(Q, 0, 60), meta(4 * Q, TEXT), meta(3 * Q, TEMPO, 0x0f, 0x42, 0x40)));

		assertEquals(Arrays.asList("1 @960 4/4 120", "2 @4800 4/4 120", "3 @8640 4/4 60"), headers(song));
	}

	// looks wrong: a zero numerator makes zero-length measures, and the reader recurses until the stack overflows
	@Test
	public void failsOnZeroLengthMeasures() {
		assertThrows(TGFileFormatException.class,
				() -> read(unquantized(), track(meta(0, TIME_SIGNATURE, 0, 2, 24, 8), on(0, 0, 60), off(Q, 0, 60))));
	}

	// ---- channels

	@Test
	public void createsChannelWithProgramVolumeAndBalance() throws Exception {
		TGSong song = read(unquantized(), track(
				program(0, 3, 25), control(0, 3, VOLUME, 100), control(0, 3, BALANCE, 30), on(0, 3, 60), off(Q, 3, 60)));

		assertEquals(Arrays.asList("1 '#1' program 25 volume 100 balance 30 bank 0 gm 3/3"), channels(song));
	}

	@Test
	public void ignoresChannelWithoutNotes() throws Exception {
		TGSong song = read(unquantized(), track(program(0, 5, 25), on(0, 0, 60), off(Q, 0, 60)));

		assertEquals(Arrays.asList("1 '#1' program 0 volume 127 balance 64 bank 0 gm 0/0"), channels(song));
	}

	@Test
	public void usesPercussionBankAndStringsForChannelTen() throws Exception {
		TGSong song = read(unquantized(), track(on(0, 9, 36), off(Q, 9, 36)));

		assertEquals(Arrays.asList("1 '#1' program 0 volume 127 balance 64 bank 128 gm 9/9"), channels(song));
		assertEquals(Arrays.asList(0, 0, 0, 0, 0, 0), strings(song.getTrack(0)));
	}

	@Test
	public void routesTwoChannelsOfOneTrackToOneChannel() throws Exception {
		TGSong song = read(unquantized(), track(
				on(0, 0, 60), on(0, 1, 64), on(0, 2, 67), off(Q, 0, 60), off(0, 1, 64), off(0, 2, 67)));

		// the third MIDI channel is dropped
		assertEquals(Arrays.asList("1 '#1' program 0 volume 127 balance 64 bank 0 gm 0/1"), channels(song));
		assertEquals(Arrays.asList("1 '' channel 1"), tracks(song));
	}

	@Test
	public void sharesOneChannelBetweenTracks() throws Exception {
		TGSong song = read(unquantized(),
				track(on(0, 0, 60), off(Q, 0, 60)),
				track(on(0, 0, 64), off(Q, 0, 64)));

		assertEquals(Arrays.asList("1 '#1' program 0 volume 127 balance 64 bank 0 gm 0/0"), channels(song));
		assertEquals(Arrays.asList("1 '' channel 1", "2 '' channel 1"), tracks(song));
	}

	@Test
	public void givesEachTrackItsOwnChannel() throws Exception {
		TGSong song = read(unquantized(),
				track(on(0, 0, 60), off(Q, 0, 60)),
				track(on(0, 1, 64), off(Q, 1, 64), on(0, 0, 62), off(Q, 0, 62)));

		assertEquals(Arrays.asList(
				"1 '#1' program 0 volume 127 balance 64 bank 0 gm 0/0",
				"2 '#2' program 0 volume 127 balance 64 bank 0 gm 1/1"), channels(song));
		// the second track plays on both MIDI channels: the one created last wins
		assertEquals(Arrays.asList("1 '' channel 1", "2 '' channel 2"), tracks(song));
	}

	// ---- tunings

	@Test
	public void tunesTrackWithTheFirstPriorityTuningCoveringItsNotes() throws Exception {
		TGSong song = read(tunings(), unquantized(), track(on(0, 0, 45), off(Q, 0, 45), on(0, 0, 64), off(Q, 0, 64)));

		assertEquals(Arrays.asList(64, 59, 55, 50, 45, 40), strings(song.getTrack(0)));
	}

	@Test
	public void tunesTrackWithALowerPriorityTuningWhenTheFirstDoesNotCover() throws Exception {
		TGSong song = read(tunings(), unquantized(), track(on(0, 0, 30), off(Q, 0, 30)));

		assertEquals(Arrays.asList(43, 38, 33, 28), strings(song.getTrack(0)));
	}

	@Test
	public void spreadsSixStringsOverANarrowRangeWithoutTuning() throws Exception {
		TGSong song = read(tunings(), unquantized(), track(on(0, 0, 10), off(Q, 0, 10), on(0, 0, 70), off(Q, 0, 70)));

		// (70 - (29 - 4) - 10) / 6 = 5 semitones apart, from 10 up
		assertEquals(Arrays.asList(35, 30, 25, 20, 15, 10), strings(song.getTrack(0)));
	}

	@Test
	public void spreadsSevenStringsOverAWideRangeWithoutTuning() throws Exception {
		TGSong song = read(tunings(), unquantized(), track(on(0, 0, 10), off(Q, 0, 10), on(0, 0, 100), off(Q, 0, 100)));

		// (100 - 25 - 10) / 6 is over 5: 7 strings (100 - 25 - 10) / 7 = 9 semitones apart
		assertEquals(Arrays.asList(64, 55, 46, 37, 28, 19, 10), strings(song.getTrack(0)));
	}

	// ---- events

	private static int[] on(int delta, int channel, int key) {
		return noteOn(delta, channel, key, 100);
	}

	private static int[] noteOn(int delta, int channel, int key, int velocity) {
		return event(delta, 0x90 | channel, key, velocity);
	}

	private static int[] off(int delta, int channel, int key) {
		return event(delta, 0x80 | channel, key, 0);
	}

	private static int[] program(int delta, int channel, int program) {
		return event(delta, 0xc0 | channel, program);
	}

	private static int[] control(int delta, int channel, int control, int value) {
		return event(delta, 0xb0 | channel, control, value);
	}

	private static int[] meta(int delta, int type, int... data) {
		int[] message = new int[data.length + 3];
		message[0] = 0xff;
		message[1] = type;
		message[2] = data.length;
		System.arraycopy(data, 0, message, 3, data.length);
		return event(delta, message);
	}

	// deltas are kept below 128, a one-byte variable-length quantity
	private static int[] event(int delta, int... message) {
		if (delta > 0x7f) {
			throw new IllegalArgumentException("delta too large for one byte: " + delta);
		}
		int[] event = new int[message.length + 1];
		event[0] = delta;
		System.arraycopy(message, 0, event, 1, message.length);
		return event;
	}

	// a track chunk with the events, then "end of track"
	private static byte[] track(int[]... events) throws IOException {
		ByteArrayOutputStream body = new ByteArrayOutputStream();
		for (int[] event : events) {
			for (int value : event) {
				body.write(value);
			}
		}
		body.write(new byte[] {0, (byte) 0xff, 0x2f, 0});
		byte[] bytes = body.toByteArray();
		int[] values = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			values[i] = bytes[i] & 0xff;
		}
		return MidiBytes.track(values);
	}

	private static byte[] file(int division, byte[]... tracks) throws IOException {
		return MidiBytes.file(MidiFileHeader.HEADER_MAGIC, 6, 1, tracks.length, division, tracks);
	}

	// ---- reading

	private static MidiSettings unquantized() {
		MidiSettings settings = new MidiSettings();
		settings.setQuantization(false);
		return settings;
	}

	private static TGSong read(MidiSettings settings, byte[]... tracks) throws Exception {
		return read(new TGContext(), settings, tracks);
	}

	private static TGSong read(TGContext context, MidiSettings settings, byte[]... tracks) throws Exception {
		TGSongReaderHandle handle = handle(settings);
		handle.setInputStream(new ByteArrayInputStream(file(Q, tracks)));
		new MidiSongReader(context).read(handle);
		return handle.getSong();
	}

	private static TGSong read(byte[] file, TGSongReaderHandle handle) throws Exception {
		handle.setInputStream(new ByteArrayInputStream(file));
		new MidiSongReader(new TGContext()).read(handle);
		return handle.getSong();
	}

	private static TGSongReaderHandle handle(MidiSettings settings) {
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setContext(new TGSongStreamContext());
		handle.setFactory(new TGFactory());
		if (settings != null) {
			handle.getContext().setAttribute(MidiSettings.class.getName(), settings);
		}
		return handle;
	}

	// a context whose bundled tunings are a guitar (priority 1) and a bass (priority 2)
	private static TGContext tunings() {
		final String xml = "<?xml version=\"1.0\"?><tunings>"
			+ "<tuning name=\"Bass\" notes=\"43,38,33,28\" priority=\"2\"/>"
			+ "<tuning name=\"Guitar\" notes=\"64,59,55,50,45,40\" priority=\"1\"/>"
			+ "</tunings>";
		TGContext context = new TGContext();
		TGResourceManager.getInstance(context).setResourceLoader(new TGResourceLoader() {
			public <T> Class<T> loadClass(String name) {
				return null;
			}

			public InputStream getResourceAsStream(String name) {
				return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
			}

			public URL getResource(String name) {
				return null;
			}

			public Enumeration<URL> getResources(String name) {
				return null;
			}
		});
		return context;
	}

	// ---- describing the song

	private DynamicTest denominator(int exponent, int expected) {
		return DynamicTest.dynamicTest(exponent + " -> " + expected, () -> {
			TGSong song = read(unquantized(), track(meta(0, TIME_SIGNATURE, 2, exponent, 24, 8), on(0, 0, 60), off(Q / 4, 0, 60)));

			assertEquals("1 @960 2/" + expected + " 120", headers(song).get(0));
		});
	}

	private static List<String> beats(TGSong song, int track, int measure) {
		TGMeasure tgMeasure = song.getTrack(track).getMeasure(measure);
		List<String> beats = new ArrayList<String>();
		for (TGBeat beat : tgMeasure.getBeats()) {
			TGVoice voice = beat.getVoice(0);
			TGDuration duration = voice.getDuration();
			String content = voice.isRestVoice() ? "rest" : pitches(tgMeasure.getTrack(), voice).toString();
			beats.add((beat.getStart() - tgMeasure.getStart()) + ":" + duration.getValue() + (duration.isDotted() ? "." : "")
				+ " " + content);
		}
		return beats;
	}

	private static List<String> pitches(TGTrack track, TGVoice voice) {
		List<String> pitches = new ArrayList<String>();
		for (TGNote note : voice.getNotes()) {
			pitches.add((track.getString(note.getString()).getValue() + note.getValue()) + (note.isTiedNote() ? "~" : ""));
		}
		Collections.sort(pitches);
		return pitches;
	}

	private static List<String> tracks(TGSong song) {
		List<String> tracks = new ArrayList<String>();
		Iterator<TGTrack> it = song.getTracks();
		while (it.hasNext()) {
			TGTrack track = it.next();
			tracks.add(track.getNumber() + " '" + track.getName() + "' channel " + track.getChannelId());
		}
		return tracks;
	}

	private static List<String> headers(TGSong song) {
		List<String> headers = new ArrayList<String>();
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while (it.hasNext()) {
			TGMeasureHeader header = it.next();
			headers.add(header.getNumber() + " @" + header.getStart() + " " + header.getTimeSignature().getNumerator() + "/"
				+ header.getTimeSignature().getDenominator().getValue() + " " + header.getTempo().getQuarterValue());
		}
		return headers;
	}

	private static List<String> channels(TGSong song) {
		List<String> channels = new ArrayList<String>();
		Iterator<TGChannel> it = song.getChannels();
		while (it.hasNext()) {
			TGChannel channel = it.next();
			channels.add(channel.getChannelId() + " '" + channel.getName() + "' program " + channel.getProgram()
				+ " volume " + channel.getVolume() + " balance " + channel.getBalance() + " bank " + channel.getBank()
				+ " gm " + parameter(channel, 0) + "/" + parameter(channel, 1));
		}
		return channels;
	}

	private static String parameter(TGChannel channel, int index) {
		TGChannelParameter parameter = channel.getParameter(index);
		return parameter.getValue();
	}

	private static List<Integer> strings(TGTrack track) {
		List<Integer> strings = new ArrayList<Integer>();
		for (TGString string : track.getStrings()) {
			strings.add(string.getValue());
		}
		return strings;
	}
}
