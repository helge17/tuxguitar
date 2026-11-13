package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.midi.synth.impl.GervillDrums;
import app.tuxguitar.midi.synth.impl.GervillProcessor;
import app.tuxguitar.player.base.MidiClick;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerCountDown;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * The drum tap over a timeline: from its first sample, what the General MIDI drums sound clicking the Count In, each
 * click heard from its sample (Gervill's latency taken out) and held a beat; silence without a Count In. The song has 2
 * measures of 4/4 at 120 bpm, played from measure 2 at 44100 Hz.
 */
public class TestDrumTap {

	private final TGContext context = new TGContext();
	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final MidiPlayerCountDown countDown = MidiPlayer.getInstance(context).getCountDown();

	public TestDrumTap() {
		TGPropertiesManager.getInstance(context).setPropertiesFactory(MapProperties::new);
		songManager.addNewMeasureBeforeEnd(song);
	}

	private TGTimeline fromMeasure2() {
		List<TGPlayedMeasure> played = TGSongTime.play(song, new MidiPlayerMode(), song.getMeasureHeader(1).getStart(), Long.MAX_VALUE, 1);
		return new TGTimeline(song, played, 44100, TGCountIn.before(countDown, played, 44100));
	}

	// the drums themselves, filled a block at a time, from their latency on
	private float[][] drums(TGTimeline timeline, MidiClick click) {
		GervillDrums drums = new GervillDrums(context, 44100);
		for (long position : timeline.getCountIn().positions()) {
			drums.click(click, position, timeline.getCountIn().hold());
		}
		int latency = GervillProcessor.latency(44100);
		int blocks = (int) ((timeline.length() + latency) / GervillDrums.FRAMES) + 1;
		float[][] sides = new float[2][blocks * GervillDrums.FRAMES];
		float[][] block = new float[2][GervillDrums.FRAMES];
		for (int b = 0; b < blocks; b++) {
			drums.fill(block);
			for (int side = 0; side < 2; side++) {
				System.arraycopy(block[side], 0, sides[side], b * GervillDrums.FRAMES, GervillDrums.FRAMES);
			}
		}
		drums.close();
		int end = latency + (int) timeline.length();
		return new float[][] {Arrays.copyOfRange(sides[0], latency, end), Arrays.copyOfRange(sides[1], latency, end)};
	}

	// one Count In tick: one click, on the first sample
	@Test
	public void isTheDrumsClickingTheCountIn() {
		countDown.setEnabled(true);
		countDown.setTickCount(1);
		TGTimeline timeline = fromMeasure2();
		float[][] tap = TGDrumTap.render(context, timeline, MidiClick.DEFAULT);

		assertTrue(Meters.peak(tap[0], 0, tap[0].length) > 0.001);
		assertArrayEquals(drums(timeline, MidiClick.DEFAULT)[0], tap[0]);
		assertArrayEquals(drums(timeline, MidiClick.DEFAULT)[1], tap[1]);
	}

	// Gervill's latency (600 samples at 44100 Hz) is taken out: the click on the first sample is heard from it
	@Test
	public void aClickIsHeardFromItsSample() {
		countDown.setEnabled(true);
		countDown.setTickCount(1);
		float[] left = TGDrumTap.render(context, fromMeasure2(), MidiClick.DEFAULT)[0];

		assertTrue(Meters.peak(left, 0, 50) > 0);
	}

	// stopped just after the Count In's click, the tap runs on to the timeline's end: the click sounds in its last samples
	@Test
	public void aClickSoundsToTheLastSample() {
		countDown.setEnabled(true);
		countDown.setTickCount(1);
		float[] left = TGDrumTap.render(context, fromMeasure2().until(1000), MidiClick.DEFAULT)[0];

		assertEquals(1000, left.length);
		assertTrue(Meters.peak(left, 900, 1000) > 0.001);
	}

	@Test
	public void isSilentWithoutACountIn() {
		float[][] tap = TGDrumTap.render(context, fromMeasure2(), MidiClick.DEFAULT);

		assertEquals(0, Meters.peak(tap[0], 0, tap[0].length) + Meters.peak(tap[1], 0, tap[1].length), 0);
	}
}
