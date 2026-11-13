package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * While TuxGuitar is stopped the device delivers its input in blocks of 512 samples, its clock going on from where it
 * stopped: the armed track's input channel is read block by block. The device reads back each capture as
 * "input:from+count", and delivers on each input channel a constant level of a tenth of its number.
 */
public class TestListening {

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGContext context = new TGContext();
	private final TGAudioSession session = TGAudioSession.getInstance(context);
	private final List<String> captures = new ArrayList<String>();

	public TestListening() {
		session.setDevice(new TGAudioDevice() {
			public int getSampleRate() {
				return 44100;
			}

			public float[] capture(int inputChannel, long from, int count) {
				captures.add(inputChannel + ":" + from + "+" + count);
				float[] samples = new float[count];
				Arrays.fill(samples, inputChannel / 10f);
				return samples;
			}
		});
		session.arm(songManager.addAudioTrack(song, TGAudioInput.mono(2)));
	}

	@Test
	public void theInputIsReadInBlocksFromWhereTheClockStopped() {
		session.listen(songManager, song, 1000);
		session.listen(songManager, song, 512);

		assertEquals(List.of("2:0+512", "2:512+488", "2:1000+512"), captures);
	}

	// input meters run on armed tracks only: an audio track not armed has its input channel read by no one
	@Test
	public void onlyTheArmedTracksInputIsRead() {
		TGTrack unarmed = songManager.addAudioTrack(song, TGAudioInput.mono(3));

		session.listen(songManager, song, 512);

		assertEquals(List.of("2:0+512"), captures);
		assertEquals(Double.NEGATIVE_INFINITY, session.getInputMeter(unarmed).getLevel());
	}

	// monitoring while stopped: an armed track monitored Auto or Always sounds its input (mono on both sides, a pair on
	// its own), read on the device's clock where the delivery starts; one monitored Off, or not armed, sounds nothing
	@Test
	public void whileStoppedTheArmedTracksMonitoredAutoOrAlwaysSoundTheirInput() {
		TGTrack auto = monitored(songManager.addAudioTrack(song, TGAudioInput.mono(3)), TGMonitoring.AUTO);
		TGTrack always = monitored(songManager.addAudioTrack(song, TGAudioInput.stereo(4, 5)), TGMonitoring.ALWAYS);
		TGTrack unarmed = songManager.addAudioTrack(song, TGAudioInput.mono(6));
		session.setMonitoring(unarmed, TGMonitoring.ALWAYS);
		TGRenderer renderer = new TGRenderer(context, songManager, song, session);
		renderer.listen(1000);
		captures.clear();

		TGRender heard = renderer.listen(512);

		assertEquals("0.0 0.0 | 0.3 0.3 | 0.4 0.5 | 0.0 0.0", tap(heard, song.getTrack(1)) + " | " + tap(heard, auto) + " | " + tap(heard, always) + " | " + tap(heard, unarmed));
		assertEquals(0, heard.getLatency(), "a monitored input is not delayed by plugin delay compensation");
		assertEquals(List.of("2:1000+512", "3:1000+512", "4:1000+512", "5:1000+512", "3:1000+512", "4:1000+512", "5:1000+512"), captures);
	}

	private TGTrack monitored(TGTrack track, TGMonitoring monitoring) {
		session.arm(track);
		session.setMonitoring(track, monitoring);
		return track;
	}

	// the track's tap, left and right, at its last sample
	private static String tap(TGRender heard, TGTrack track) {
		return heard.getTap(track, 0)[511] + " " + heard.getTap(track, 1)[511];
	}
}
