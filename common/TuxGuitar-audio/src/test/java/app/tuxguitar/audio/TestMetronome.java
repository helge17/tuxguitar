package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

/**
 * The Metronome's clicks over a timeline at 48000 Hz: measure 1 in 4/4 at 120 bpm (a quarter is 24000 samples), measure
 * 2 in 3/8 at 90 bpm (an eighth is 16000 samples), played from measure 1 beat 2.5.
 */
public class TestMetronome {

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();

	@Test
	public void aClickOnEachBeatFromWherePlayingStartsEachHeldABeatOfItsMeasure() {
		songManager.addNewMeasureBeforeEnd(song);
		TGMeasureHeader second = song.getMeasureHeader(1);
		second.getTimeSignature().setNumerator(3);
		second.getTimeSignature().getDenominator().setValue(TGDuration.EIGHTH);
		second.getTempo().setQuarterValue(90);
		long start = song.getMeasureHeader(0).getStart() + 3 * TGDuration.QUARTER_TIME / 2;
		List<TGPlayedMeasure> played = TGSongTime.play(song, new MidiPlayerMode(), start, Long.MAX_VALUE, 1);
		TGMetronome metronome = TGMetronome.of(new TGTimeline(song, played, 48000));

		String holds = IntStream.range(0, metronome.positions().length).mapToObj(click -> Long.toString(metronome.hold(click))).collect(Collectors.joining(", "));
		assertEquals("[12000, 36000, 60000, 76000, 92000] | 24000, 24000, 16000, 16000, 16000", Arrays.toString(metronome.positions()) + " | " + holds);
	}

	@Test
	public void noMetronomeClicksNothing() {
		assertEquals("[]", Arrays.toString(TGMetronome.NONE.positions()));
	}
}
