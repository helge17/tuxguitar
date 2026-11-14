package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerCountDown;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * The drum tap joins the output mix as the song's percussion channel does, the one Count In and the Metronome click on
 * today: scaled by its Volume and Balance with the tracks' constant-power law. A song without a percussion channel has
 * no Count In and no Metronome today; the drums click anyway, as a new channel sounds: Volume 127, Balance 64.
 *
 * The song has 2 measures of 4/4 at 120 bpm and no notes, played from measure 2 at 44100 Hz after a Count In of one
 * tick, so the output is the drums alone.
 */
public class TestDrumMix {

	private final TGContext context = new TGContext();
	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();

	public TestDrumMix() {
		TGPropertiesManager.getInstance(context).setPropertiesFactory(MapProperties::new);
		songManager.addNewMeasureBeforeEnd(song);
		MidiPlayerCountDown countDown = MidiPlayer.getInstance(context).getCountDown();
		countDown.setEnabled(true);
		countDown.setTickCount(1);
	}

	private TGRender render() throws MidiPlayerException {
		TGAudioSession session = TGAudioSession.getInstance(this.context);
		session.setDevice(new TGAudioDevice() {
			public int getSampleRate() {
				return 44100;
			}

			public float[] capture(int inputChannel, long from, int count) {
				return new float[count];
			}
		});
		MidiPlayerMode mode = new MidiPlayerMode();
		List<TGPlayedMeasure> played = TGSongTime.play(song, mode, song.getMeasureHeader(1).getStart(), Long.MAX_VALUE, 1);
		TGTimeline timeline = new TGTimeline(song, played, 44100, TGCountIn.before(MidiPlayer.getInstance(context).getCountDown(), played, 44100));
		return new TGRenderer(this.context, this.songManager, this.song, session).render(timeline, mode, false, true);
	}

	// each side of the output is that side of the drum tap scaled by the channel's gain for it
	private static void assertMixedAs(TGChannel channel, TGRender render) {
		float[] gains = TGRenderer.gains(channel);
		assertTrue(Meters.peak(render.getDrums(0), 0, 5000) > 0.01);
		for (int i = 0; i < 5000; i++) {
			assertEquals(render.getDrums(0)[i] * gains[0], render.getLeft()[i], 0.000001, "left at " + i);
			assertEquals(render.getDrums(1)[i] * gains[1], render.getRight()[i], 0.000001, "right at " + i);
		}
	}

	@Test
	public void theDrumsSoundAtThePercussionChannelsVolumeAndBalance() throws MidiPlayerException {
		songManager.addChannel(song);
		TGChannel percussion = songManager.addPercussionChannel(song);
		percussion.setVolume((short) 64);
		percussion.setBalance((short) 100);
		songManager.addPercussionChannel(song).setVolume((short) 10);

		assertMixedAs(percussion, render());
	}

	@Test
	public void withoutAPercussionChannelTheDrumsSoundAsANewChannel() throws MidiPlayerException {
		TGChannel channel = songManager.getFactory().newChannel();

		assertEquals("127 64", channel.getVolume() + " " + channel.getBalance());
		assertMixedAs(channel, render());
	}
}
