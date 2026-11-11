package app.tuxguitar.io.tg.v07;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.tg.LegacyFormatFixture;
import app.tuxguitar.io.tg.LegacyStreamEncoder;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
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
		LegacyStreamEncoder encoder = new LegacyStreamEncoder(TGSongReaderImpl.SUPPORTED_FORMAT.getVersion())
			.string("name").string("artist").string("album").string("author").ints(this.tracks.size());
		for (int i = 0; i < this.tracks.size(); i++) {
			int[] track = this.tracks.get(i);
			encoder.longs(i + 1).string("track").shorts(track[0], track[1], track[2], 100, 64, 10, 20, 30, 40)
				.bytes(0, 0).ints(0, 0, 0, 0, 0);
		}
		TGSong song = LegacyFormatFixture.read(new TGSongReaderImpl(), new TGFactory(), encoder.toByteArray());
		return describeChannels(song) + "tracks" + describeTracks(song);
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
