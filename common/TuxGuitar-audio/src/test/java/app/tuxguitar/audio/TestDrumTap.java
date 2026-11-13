package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.midi.synth.impl.GervillDrums;
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
 * click on its sample and held a beat; silence without a Count In. The song has 2 measures of 4/4 at 120 bpm, played
 * from measure 2 at 44100 Hz.
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

	// the drums themselves, filled a block at a time from their first frame
	private float[][] drums(TGTimeline timeline, MidiClick click) {
		GervillDrums drums = new GervillDrums(context, 44100);
		for (long position : timeline.getCountIn().positions()) {
			drums.click(click, position, timeline.getCountIn().hold());
		}
		int blocks = (int) (timeline.length() / GervillDrums.FRAMES) + 1;
		float[][] sides = new float[2][blocks * GervillDrums.FRAMES];
		float[][] block = new float[2][GervillDrums.FRAMES];
		for (int b = 0; b < blocks; b++) {
			drums.fill(block);
			for (int side = 0; side < 2; side++) {
				System.arraycopy(block[side], 0, sides[side], b * GervillDrums.FRAMES, GervillDrums.FRAMES);
			}
		}
		drums.close();
		return new float[][] {Arrays.copyOf(sides[0], (int) timeline.length()), Arrays.copyOf(sides[1], (int) timeline.length())};
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

	@Test
	public void isSilentWithoutACountIn() {
		float[][] tap = TGDrumTap.render(context, fromMeasure2(), MidiClick.DEFAULT);

		assertEquals(0, Meters.peak(tap[0], 0, tap[0].length) + Meters.peak(tap[1], 0, tap[1].length), 0);
	}
}
