package app.tuxguitar.io.tg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGMessagesManager;

/**
 * File format 3.0: a song with an audio channel keeps its takes (each one an entry of the file, encoded by the take
 * codec TuxGuitar's audio support gives), where they play, the takes chosen, each audio track's next take number and
 * each audio channel's input channels. A reader without a take codec (format 2.0: today's reader, Android's) refuses
 * such a file as one of a newer version.
 *
 * The song has 3 measures of 4/4; track 2 records input channel 1 (mono), track 3 the pair 2 and 1 (stereo).
 */
public class TestFileFormat30 {

	// the message keys stand for the messages: none are loaded here
	private static final String PLEASE_UPGRADE = "error.new-major-version";

	// a take's samples as text: its rate, then each channel's samples
	private static final class TextCodec implements TGTakeCodec {
		public String getExtension() {
			return "take";
		}

		public void write(TGTake take, OutputStream out) throws IOException {
			DataOutputStream data = new DataOutputStream(out);
			data.writeInt(take.getSampleRate());
			data.writeInt(take.countChannels());
			data.writeInt(take.countSamples());
			for (int channel = 0; channel < take.countChannels(); channel++) {
				write(take, channel, data);
			}
			data.flush();
		}

		private static void write(TGTake take, int channel, DataOutputStream data) throws IOException {
			for (int i = 0; i < take.countSamples(); i++) {
				data.writeFloat(take.getSample(channel, i));
			}
		}

		public TGTake read(InputStream in) throws IOException {
			DataInputStream data = new DataInputStream(in);
			int rate = data.readInt();
			float[][] samples = new float[data.readInt()][data.readInt()];
			for (float[] channel : samples) {
				read(channel, data);
			}
			return TGTake.adopt(0, samples, rate);
		}

		private static void read(float[] channel, DataInputStream data) throws IOException {
			for (int i = 0; i < channel.length; i++) {
				channel[i] = data.readFloat();
			}
		}
	}

	private final TGFactory factory = new TGFactory();
	private final TGSongManager songManager = new TGSongManager(this.factory);
	private final TGSong song = this.songManager.newSong();
	private final TGTake first = new TGTake(1, new float[] {0.25f, -0.5f, 0.125f}, 44100);
	private final TGTake second = new TGTake(2, new float[][] {{0.5f, 0.75f}, {-1.5f, 2f}}, 48000);

	public TestFileFormat30() {
		this.songManager.addNewMeasureBeforeEnd(this.song);
		this.songManager.addNewMeasureBeforeEnd(this.song);
		this.songManager.addAudioTrack(this.song, TGAudioInput.mono(1));
		this.songManager.addAudioTrack(this.song, TGAudioInput.stereo(2, 1));
	}

	private TGTrack track(TGSong song, int number) {
		return song.getTrack(number - 1);
	}

	private TGMeasure measure(TGSong song, int track, int number) {
		return track(song, track).getMeasure(number - 1);
	}

	private byte[] write(TGSong song, TGTakeCodec codec) throws TGFileFormatException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		TGSongWriterHandle handle = new TGSongWriterHandle();
		handle.setFactory(this.factory);
		handle.setSong(song);
		handle.setOutputStream(bytes);
		TGSongWriterImpl writer = new TGSongWriterImpl();
		writer.setTakeCodec(codec);
		writer.write(handle);
		return bytes.toByteArray();
	}

	private TGSong read(byte[] file, TGTakeCodec codec) throws TGFileFormatException {
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setFactory(this.factory);
		handle.setInputStream(new ByteArrayInputStream(file));
		TGSongReaderImpl reader = new TGSongReaderImpl();
		reader.setTakeCodec(codec);
		reader.read(handle);
		return handle.getSong();
	}

	private TGSong reopened() throws TGFileFormatException {
		return read(write(this.song, new TextCodec()), new TextCodec());
	}

	// the entries of the file, in order, then what version.txt says
	private static String entries(byte[] file) throws IOException {
		Map<String, byte[]> entries = unzip(file);
		return String.join(" ", entries.keySet()) + " | " + new String(entries.get("version.txt"), StandardCharsets.UTF_8);
	}

	private static String samples(TGTake take) {
		List<String> channels = IntStream.range(0, take.countChannels()).mapToObj(channel -> IntStream.range(0, take.countSamples())
			.mapToObj(i -> Float.toString(take.getSample(channel, i))).collect(Collectors.joining(" "))).collect(Collectors.toList());
		return "take " + take.getNumber() + " at " + take.getSampleRate() + ": " + channels;
	}

	private static String clips(TGMeasure measure) {
		StringJoiner clips = new StringJoiner(", ");
		for (TGTakeClip clip : measure.getTakeClips()) {
			clips.add(clip.getTake().getNumber() + "/" + clip.getSource().getNumber() + " " + clip.getStart() + "-" + clip.getEnd() + " from " + clip.getFirstSample() + " at " + clip.getTempo());
		}
		return clips.toString();
	}

	private static String innermostMessage(Throwable thrown) {
		Throwable cause = thrown;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause.getMessage();
	}

	@Test
	public void aSongWithAnAudioChannelIsFormat30WithAnEntryPerTake() throws Exception {
		measure(this.song, 2, 1).addTakeClip(new TGTakeClip(this.first, 0, 3840, 0, 120.0));
		measure(this.song, 3, 2).addTakeClip(new TGTakeClip(this.second, 960, 1920, 0, 96.5));

		assertEquals("version.txt content.xml takes/1.take takes/2.take | TuxGuitar_file_format 3.0", entries(write(this.song, new TextCodec())));
	}

	@Test
	public void anAudioTrackWithoutTakesIsFormat30Too() throws Exception {
		assertEquals("version.txt content.xml | TuxGuitar_file_format 3.0", entries(write(this.song, new TextCodec())));
	}

	@Test
	public void aSongWithoutAudioChannelsStaysFormat20() throws Exception {
		TGSong plain = this.songManager.newSong();

		assertEquals("version.txt content.xml | TuxGuitar_file_format 2.0", entries(write(plain, new TextCodec())));
	}

	@Test
	public void takesComeBackSampleForSampleAtTheirRates() throws Exception {
		measure(this.song, 2, 1).addTakeClip(new TGTakeClip(this.first, 0, 3840, 0, 120.0));
		measure(this.song, 3, 2).addTakeClip(new TGTakeClip(this.second, 960, 1920, 0, 96.5));
		TGSong reopened = reopened();

		assertEquals("take 1 at 44100: [0.25 -0.5 0.125] | take 2 at 48000: [0.5 0.75, -1.5 2.0]",
			samples(measure(reopened, 2, 1).getTakeClips().get(0).getTake()) + " | " + samples(measure(reopened, 3, 2).getTakeClips().get(0).getTake()));
	}

	// a pasted take plays the samples of another (its source); measure 3 chose take 1
	@Test
	public void clipsTheirSourcesAndTheChosenTakesComeBack() throws Exception {
		TGTake pasted = new TGTake(3, new float[0], 44100);
		measure(this.song, 2, 1).addTakeClip(new TGTakeClip(this.first, 0, 3840, 0, 120.0));
		measure(this.song, 2, 3).addTakeClip(new TGTakeClip(this.first, 0, 1920, 7, 90.25));
		measure(this.song, 2, 3).addTakeClip(new TGTakeClip(pasted, this.second, 1920, 3840, 1, 96.5));
		measure(this.song, 2, 3).setChosenTake(this.first);
		TGSong reopened = reopened();

		assertEquals("1/1 0-3840 from 0 at 120.0 | 1/1 0-1920 from 7 at 90.25, 3/2 1920-3840 from 1 at 96.5 | take 1",
			clips(measure(reopened, 2, 1)) + " | " + clips(measure(reopened, 2, 3)) + " | take " + measure(reopened, 2, 3).getChosenTake().getNumber());
	}

	// takes are shared, never copied: one take in two measures, and in a cloned track, is one take after reopening
	@Test
	public void aTakeSharedByMeasuresAndTracksStaysShared() throws Exception {
		measure(this.song, 2, 1).addTakeClip(new TGTakeClip(this.first, 0, 3840, 0, 120.0));
		measure(this.song, 2, 2).addTakeClip(new TGTakeClip(this.first, 0, 3840, 88200, 120.0));
		measure(this.song, 3, 1).addTakeClip(new TGTakeClip(this.first, 0, 3840, 0, 120.0));
		TGSong reopened = reopened();
		TGTake take = measure(reopened, 2, 1).getTakeClips().get(0).getTake();

		assertSame(take, measure(reopened, 2, 2).getTakeClips().get(0).getTake());
		assertSame(take, measure(reopened, 3, 1).getTakeClips().get(0).getSource());
	}

	@Test
	public void eachAudioTracksInputAndNextTakeNumberComeBack() throws Exception {
		track(this.song, 2).setNextTakeNumber(4);
		track(this.song, 3).setNextTakeNumber(2);
		TGSong reopened = reopened();
		TGAudioInput mono = this.songManager.getChannel(reopened, track(reopened, 2).getChannelId()).getAudioInput();
		TGAudioInput stereo = this.songManager.getChannel(reopened, track(reopened, 3).getChannelId()).getAudioInput();

		String inputs = mono.countChannels() + ":" + mono.getChannel(0) + " " + stereo.countChannels() + ":" + stereo.getChannel(0) + "," + stereo.getChannel(1);
		String numbers = track(reopened, 2).getNextTakeNumber() + " " + track(reopened, 3).getNextTakeNumber();

		assertEquals("1:1 2:2,1 | 4 2 | false", inputs + " | " + numbers + " | " + track(reopened, 1).isAudio());
	}

	// format 2.0's reader (no take codec) refuses a format 3.0 file as one of a newer version, though it holds entries
	// format 2.0 does not have
	@Test
	public void aReaderWithoutTakeCodecRefusesFormat30AsANewerVersion() throws Exception {
		measure(this.song, 2, 1).addTakeClip(new TGTakeClip(this.first, 0, 3840, 0, 120.0));
		byte[] file = write(this.song, new TextCodec());

		TGFileFormatException refused = assertThrows(TGFileFormatException.class, () -> read(file, null));
		assertEquals(TGMessagesManager.getProperty(PLEASE_UPGRADE), innermostMessage(refused));
	}

	// with a take codec: format 4.0 is newer, whatever it holds; an entry neither format knows is refused, and so are
	// takes in format 2.0, which has none
	@Test
	public void aReaderWithATakeCodecRefusesNewerVersionsAndEntriesTheVersionDoesNotHave() throws Exception {
		String newer = refusal("TuxGuitar_file_format 4.0", "version.txt", "content.xml", "takes/1.take");
		String unknown = refusal("TuxGuitar_file_format 3.0", "version.txt", "content.xml", "other.bin");
		String takesIn20 = refusal("TuxGuitar_file_format 2.0", "version.txt", "content.xml", "takes/1.take");

		String unsupported = TGMessagesManager.getProperty("error.unsupported-format");
		assertEquals(TGMessagesManager.getProperty(PLEASE_UPGRADE) + " | " + unsupported + " | " + unsupported, newer + " | " + unknown + " | " + takesIn20);
	}

	// without version.txt or content.xml a file is not a TuxGuitar file
	@Test
	public void aFileWithoutItsVersionOrItsContentIsRefused() throws Exception {
		String noVersion = refusal("TuxGuitar_file_format 3.0", "content.xml");
		String noContent = refusal("TuxGuitar_file_format 3.0", "version.txt");

		String unsupported = TGMessagesManager.getProperty("error.unsupported-format");
		assertEquals(unsupported + " | " + unsupported, noVersion + " | " + noContent);
	}

	// a song's file whose version is none, or older than format 2.0 (read by the readers of older formats)
	@Test
	public void aFileOfNoVersionOrOfAnOlderOneIsRefused() throws Exception {
		Map<String, byte[]> entries = unzip(write(this.songManager.newSong(), null));
		entries.put("version.txt", "TuxGuitar_file_format".getBytes(StandardCharsets.UTF_8));
		assertThrows(TGFileFormatException.class, () -> read(rezip(entries), new TextCodec()));
		entries.put("version.txt", "TuxGuitar_file_format 1.5".getBytes(StandardCharsets.UTF_8));
		assertThrows(TGFileFormatException.class, () -> read(rezip(entries), new TextCodec()));
	}

	// content.xml names a take whose entry is not in the file
	@Test
	public void aTakeWithoutItsEntryIsRefused() throws Exception {
		measure(this.song, 2, 1).addTakeClip(new TGTakeClip(this.first, 0, 3840, 0, 120.0));
		Map<String, byte[]> entries = unzip(write(this.song, new TextCodec()));
		entries.remove("takes/1.take");

		assertEquals("No entry for take 1", innermostMessage(assertThrows(TGFileFormatException.class, () -> read(rezip(entries), new TextCodec()))));
	}

	// why a reader with a take codec refuses a file of the entries named, each holding the version's text
	private String refusal(String version, String... names) throws IOException {
		byte[] file = zip(version, names);
		return innermostMessage(assertThrows(TGFileFormatException.class, () -> read(file, new TextCodec())));
	}

	private static Map<String, byte[]> unzip(byte[] file) throws IOException {
		Map<String, byte[]> entries = new LinkedHashMap<String, byte[]>();
		try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(file))) {
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				entries.put(entry.getName(), zip.readAllBytes());
			}
		}
		return entries;
	}

	private static byte[] rezip(Map<String, byte[]> entries) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
			for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
				zip.putNextEntry(new ZipEntry(entry.getKey()));
				zip.write(entry.getValue());
				zip.closeEntry();
			}
		}
		return bytes.toByteArray();
	}

	// without a take codec there is nothing to write a take with
	@Test
	public void aSongWithAnAudioChannelCannotBeWrittenWithoutATakeCodec() {
		assertThrows(TGFileFormatException.class, () -> write(this.song, null));
	}

	// a file of the entries named, each holding the version's text
	private static byte[] zip(String version, String... names) throws IOException {
		Map<String, byte[]> entries = new LinkedHashMap<String, byte[]>();
		for (String name : names) {
			entries.put(name, version.getBytes(StandardCharsets.UTF_8));
		}
		return rezip(entries);
	}
}
