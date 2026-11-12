package app.tuxguitar.song.managers;

import static app.tuxguitar.song.managers.MeasureFixture.QUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

/**
 * Pins how TGMeasureManager finds beats in a list or a track: the first and last beat by start, the first and last
 * non-empty voice of an index, and the beat at a precise start anywhere in a track. A beat is named by its position
 * in quarters from the start of the first measure, "#<n>" telling apart beats that start together, "-" when none.
 */
public class TestFirstAndLast {

	private final MeasureFixture fixture = new MeasureFixture();
	private final TGMeasureManager manager = fixture.manager;
	private final List<TGBeat> created = new ArrayList<TGBeat>();

	// a beat at "quarter" quarters from the measure start, kept out of the measure, with voice 0 empty unless "voiced"
	private TGBeat beat(int quarter, boolean voiced) {
		TGBeat beat = fixture.factory.newBeat();
		beat.setPreciseStart(fixture.at(quarter * QUARTER));
		beat.getVoice(0).setEmpty(!voiced);
		created.add(beat);
		return beat;
	}

	private String name(TGBeat beat) {
		if (beat == null) {
			return "-";
		}
		long quarters = (beat.getPreciseStart() - fixture.measure.getPreciseStart()) / QUARTER;
		return quarters + "#" + created.indexOf(beat);
	}

	private String name(TGVoice voice) {
		return voice == null ? "-" : name(voice.getBeat());
	}

	@Test
	public void theFirstAndLastBeatsAreFoundByStartTheEarliestInTheListAmongEqualStarts() {
		List<TGBeat> beats = Arrays.asList(beat(2, true), beat(0, true), beat(3, true), beat(0, true), beat(3, true));

		assertEquals("0#1 3#2 - -", name(manager.getFirstBeat(beats)) + " " + name(manager.getLastBeat(beats))
			+ " " + name(manager.getFirstBeat(new ArrayList<TGBeat>())) + " " + name(manager.getLastBeat(new ArrayList<TGBeat>())));
	}

	@Test
	public void theFirstAndLastVoicesSkipTheBeatsWhereTheVoiceIsEmpty() {
		List<TGBeat> beats = Arrays.asList(beat(0, false), beat(2, true), beat(1, true), beat(1, true), beat(3, false), beat(2, true));
		List<TGBeat> withoutVoice = Arrays.asList(beat(0, false), beat(1, false));

		assertEquals("1#2 2#1 - -", name(manager.getFirstVoice(beats, 0)) + " " + name(manager.getLastVoice(beats, 0))
			+ " " + name(manager.getFirstVoice(withoutVoice, 0)) + " " + name(manager.getLastVoice(withoutVoice, 0)));
	}

	@Test
	public void aBeatIsFoundByItsPreciseStartInAnyMeasureOfTheTrack() {
		TGTrack track = fixture.measure.getTrack();
		fixture.songManager.addNewMeasureBeforeEnd(track.getSong());
		TGMeasure second = track.getMeasure(1);
		fixture.note(QUARTER, TGDuration.QUARTER, 0);
		TGBeat later = fixture.factory.newBeat();
		later.setPreciseStart(second.getPreciseStart() + QUARTER);
		manager.addBeat(second, later);
		created.add(fixture.beatAt(QUARTER));
		created.add(later);

		assertEquals("1#0 5#1 -", name(manager.getBeatPrecise(track, fixture.at(QUARTER)))
			+ " " + name(manager.getBeatPrecise(track, second.getPreciseStart() + QUARTER))
			+ " " + name(manager.getBeatPrecise(track, second.getPreciseStart() + 1)));
	}
}
