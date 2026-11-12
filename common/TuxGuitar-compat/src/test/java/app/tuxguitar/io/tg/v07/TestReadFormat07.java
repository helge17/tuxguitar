package app.tuxguitar.io.tg.v07;

import static app.tuxguitar.io.tg.LegacyFormatFixture.describeBeatFeatures;
import static app.tuxguitar.io.tg.LegacyFormatFixture.describeHeaders;
import static app.tuxguitar.io.tg.LegacyFormatFixture.describeNotes;
import static app.tuxguitar.io.tg.LegacyFormatFixture.firstMeasure;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.tg.LegacyFormatFixture;
import app.tuxguitar.io.tg.LegacyStreamEncoder;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;

/**
 * Reads hand-encoded 0.7 files and pins how a track's channel is read (TGSongReaderImpl.readChannel): its GM
 * channel, effects channel, program and mixer values; GM channel 9 is percussion. A track whose GM channel is
 * already used by a channel of the song joins that channel; otherwise a new channel is added, numbered after the
 * song's channels, named after its program, with its GM channels as parameters.
 *
 * A song is described by its channels ("id name bank program volume balance chorus reverb phaser tremolo
 * [parameters]"), then each track's channel id. Every track has the same mixer values, no measures and no strings.
 * Without the application's messages, a channel is named by its message key.
 */
public class TestReadFormat07 {

	private static final String MIXER = " v100 b64 c10 r20 ph30 t40";

	private final List<int[]> tracks = new ArrayList<int[]>();

	@Test
	public void eachTrackOnANewGMChannelGetsANewChannelWithItsValuesAndPercussionOnGMChannel9() throws Exception {
		track(3, 4, 25);
		track(9, 9, 0);

		assertEquals("1 file.save.default-name 1 bank0 p25" + MIXER + " [gm-channel-1=3, gm-channel-2=4]"
			+ " | 2 instrument.drumkit 1 bank128 p0" + MIXER + " [gm-channel-1=9, gm-channel-2=9]"
			+ " | tracks 1 2", read());
	}

	@Test
	public void aMeasureJoinsNotesAndSilencesOfTheSameStartIntoBeatsAndReadsItsHeader() throws Exception {
		LegacyStreamEncoder encoder = trackHead(songOf(1), 1, new int[] {3, 4, 25}).ints(1);
		// measure 1 at 960 ticks; four notes: value, start, duration, velocity, string, tied, then their effects
		// (vibrato, bend, dead note, slide, hammer)
		encoder.ints(1).longs(1000).ints(4);
		// positive bend values are halved but negative ones are kept as they are: looks inconsistent, pinned as-is
		note(encoder, 5, 1000, 8).ints(90, 1).bytes(0).bytes(1, 1).ints(4, 0, 0, 3, 1, 6, 8, 12, -2).bytes(0, 0, 0);
		note(encoder, 7, 1000, 8).ints(100, 2).bytes(1).bytes(0, 0).bytes(1, 0, 0);
		duration(encoder.ints(3).longs(1500), 4, 0, 3, 2).ints(110, 3).bytes(0).bytes(0, 0).bytes(0, 1, 0);
		duration(encoder.ints(4).longs(1500), 4, 0, 3, 2).ints(120, 4).bytes(0).bytes(0, 0).bytes(0, 0, 1);
		// three silences: start, duration
		encoder.ints(3);
		duration(encoder.longs(2000), 4, 1, 1, 1);
		duration(encoder.longs(2000), 2, 0, 1, 1);
		duration(encoder.longs(3000), 8, 0, 1, 1);
		// time signature 3/8, tempo, clef, key signature, repeat open and close; then no strings and a black color
		duration(encoder.ints(3), 8, 0, 1, 1).ints(90, 2, 3).bytes(1).ints(2);
		TGSong song = read(encoder.ints(0, 0, 0, 0));

		assertEquals(Arrays.asList("#1 @960 3/8 q90 open=true close=2 triplet=1"), describeHeaders(song));
		assertEquals(Arrays.asList("+0 v0:d8 s1f5 s2f7", "+480 v0:d4/3:2 s3f3 s4f4", "+960 v0:d2 rest", "+1920 v0:d8 rest"),
			describeBeatFeatures(song));
		assertEquals(Arrays.asList("@960 s1 f5 v90 bend[0:0][3:0][6:4][12:-2] vibrato", "@960 s2 f7 v100 dead",
			"@1440 s3 f3 v110 slide", "@1440 s4 f4 v120 hammer"), describeNotes(song, false));
		TGMeasure measure = firstMeasure(song);
		assertEquals("clef 2 key 3 tied false true", "clef " + measure.getClef() + " key " + measure.getKeySignature()
			+ " tied " + measure.getBeat(0).getVoice(0).getNote(0).isTiedNote() + " " + measure.getBeat(0).getVoice(0).getNote(1).isTiedNote());
	}

	@Test
	public void aTrackOnAnUsedGMChannelJoinsItsChannelWhateverItsEffectsChannelAndProgram() throws Exception {
		track(3, 4, 25);
		track(5, 6, 30);
		track(5, 7, 40);
		track(3, 8, 50);
		track(4, 3, 60);

		assertEquals("1 file.save.default-name 1 bank0 p25" + MIXER + " [gm-channel-1=3, gm-channel-2=4]"
			+ " | 2 file.save.default-name 2 bank0 p30" + MIXER + " [gm-channel-1=5, gm-channel-2=6]"
			+ " | 3 file.save.default-name 3 bank0 p60" + MIXER + " [gm-channel-1=4, gm-channel-2=3]"
			+ " | tracks 1 2 2 1 3", read());
	}

	// ---- helpers ----

	// a track on the given GM channel and effects channel, with the given program
	private void track(int gmChannel, int gmEffectsChannel, int program) {
		this.tracks.add(new int[] {gmChannel, gmEffectsChannel, program});
	}

	private String read() throws Exception {
		LegacyStreamEncoder encoder = songOf(this.tracks.size());
		for (int i = 0; i < this.tracks.size(); i++) {
			int[] track = this.tracks.get(i);
			trackHead(encoder, i + 1, track).ints(0, 0, 0, 0, 0);
		}
		TGSong song = read(encoder);
		return describeChannels(song) + "tracks" + describeTracks(song);
	}

	private static TGSong read(LegacyStreamEncoder encoder) throws Exception {
		return LegacyFormatFixture.read(new TGSongReaderImpl(), new TGFactory(), encoder.toByteArray());
	}

	private static LegacyStreamEncoder songOf(int trackCount) {
		return new LegacyStreamEncoder(TGSongReaderImpl.SUPPORTED_FORMAT.getVersion())
			.string("name").string("artist").string("album").string("author").ints(trackCount);
	}

	// number, name, channel, solo and mute: what comes before a track's measure count
	private static LegacyStreamEncoder trackHead(LegacyStreamEncoder encoder, int number, int[] track) {
		return encoder.longs(number).string("track").shorts(track[0], track[1], track[2], 100, 64, 10, 20, 30, 40)
			.bytes(0, 0);
	}

	// value, dotted, double dotted, then the division's enters and times
	private static LegacyStreamEncoder duration(LegacyStreamEncoder encoder, int value, int dotted, int enters, int times) {
		return encoder.ints(value).bytes(dotted, 0).ints(enters, times);
	}

	// a 0.7 start is in thousandths of a quarter
	private static LegacyStreamEncoder note(LegacyStreamEncoder encoder, int value, long start, int duration) {
		return duration(encoder.ints(value).longs(start), duration, 0, 1, 1);
	}

	private static String describeChannels(TGSong song) {
		StringBuilder description = new StringBuilder();
		for (int i = 0; i < song.countChannels(); i++) {
			description.append(describe(song.getChannel(i))).append(" | ");
		}
		return description.toString();
	}

	private static String describeTracks(TGSong song) {
		StringBuilder description = new StringBuilder();
		for (int i = 0; i < song.countTracks(); i++) {
			description.append(' ').append(song.getTrack(i).getChannelId());
		}
		return description.toString();
	}

	private static String describe(TGChannel channel) {
		List<String> parameters = new ArrayList<String>();
		for (int n = 0; n < channel.countParameters(); n++) {
			TGChannelParameter parameter = channel.getParameter(n);
			parameters.add(parameter.getKey() + "=" + parameter.getValue());
		}
		return channel.getChannelId() + " " + channel.getName() + " bank" + channel.getBank() + " p" + channel.getProgram()
			+ " v" + channel.getVolume() + " b" + channel.getBalance() + " c" + channel.getChorus() + " r" + channel.getReverb()
			+ " ph" + channel.getPhaser() + " t" + channel.getTremolo() + " " + parameters;
	}
}
