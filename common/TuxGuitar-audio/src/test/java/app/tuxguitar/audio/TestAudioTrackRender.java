package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * An audio track renders its takes into its tap, side by side, and the output mix scales each side of the tap by its
 * side of Volume and Balance. The song is one measure of 4/4 at 120 bpm (88200 samples at 44100 Hz) whose guitar
 * track holds no note; the audio track holds a stereo take of the measure, 1 on the left and 0.5 on the right.
 */
public class TestAudioTrackRender {

	private final TGContext context = new TGContext();
	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGTrack track = songManager.addAudioTrack(song, TGAudioInput.stereo(1, 2));

	public TestAudioTrackRender() {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(MapProperties::new);
		float[][] sides = new float[2][88200];
		Arrays.fill(sides[0], 1f);
		Arrays.fill(sides[1], 0.5f);
		track.getMeasure(0).addTakeClip(new TGTakeClip(new TGTake(1, sides, 44100), 0, 3840, 0, 120));
	}

	private TGRender render() throws MidiPlayerException {
		return render(true);
	}

	// synthesizer: the MIDI Port is the TuxGuitar Synthesizer
	private TGRender render(boolean synthesizer) throws MidiPlayerException {
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
		long start = song.getMeasureHeader(0).getStart();
		TGTimeline timeline = new TGTimeline(song, TGSongTime.play(song, mode, start, Long.MAX_VALUE, 1), 44100);
		return new TGRenderer(this.context, this.songManager, this.song, session).render(timeline, mode, false, synthesizer);
	}

	@Test
	public void eachSideOfTheTapIsScaledByItsSideOfVolumeAndBalance() throws MidiPlayerException {
		TGChannel channel = songManager.getChannel(song, track.getChannelId());
		channel.setVolume((short) 100);
		channel.setBalance((short) 30);
		TGRender render = render();
		double pan = 30 / 127.0 * Math.PI / 2;

		assertEquals(1f, render.getTap(track, 0)[1000]);
		assertEquals(0.5f, render.getTap(track, 1)[1000]);
		assertEquals(100 / 127.0 * Math.cos(pan), render.getLeft()[1000], 1e-6);
		assertEquals(0.5 * 100 / 127.0 * Math.sin(pan), render.getRight()[1000], 1e-6);
	}

	// with another MIDI Port nothing sounds here: a tap that sounded nothing is silence as long as the render
	@Test
	public void aChannelThatSoundedNothingHasASilentTapAsLongAsTheRender() throws MidiPlayerException {
		float[] tap = render(false).getTap(track, 1);

		assertEquals(88200, tap.length);
		assertEquals(0, Meters.peak(tap, 0, tap.length));
	}
}
