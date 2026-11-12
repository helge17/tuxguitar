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
 * "input:from+count", and delivers a constant level of 0.5.
 */
public class TestListening {

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGAudioSession session = TGAudioSession.getInstance(new TGContext());
	private final List<String> captures = new ArrayList<String>();

	public TestListening() {
		session.setDevice(new TGAudioDevice() {
			public int getSampleRate() {
				return 44100;
			}

			public float[] capture(int inputChannel, long from, int count) {
				captures.add(inputChannel + ":" + from + "+" + count);
				float[] samples = new float[count];
				Arrays.fill(samples, 0.5f);
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
}
