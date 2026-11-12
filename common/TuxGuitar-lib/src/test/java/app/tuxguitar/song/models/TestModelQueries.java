package app.tuxguitar.song.models;

import static app.tuxguitar.song.managers.MeasureFixture.QUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.MeasureFixture;

/**
 * Pins small queries of the song models: the highest fret of a voice, beat, measure and track,
 * a channel parameter by index, the notes of a chord, and color equality.
 */
public class TestModelQueries {

	private final TGFactory factory = new TGFactory();

	@Test
	public void theHighestFretIsTheHighestOfTheNotesWithinOrMinusOneWithoutNotes() {
		MeasureFixture fixture = new MeasureFixture();
		TGMeasure measure = fixture.measure;
		TGTrack track = measure.getTrack();
		fixture.songManager.addNewMeasureBeforeEnd(track.getSong());
		fixture.beatAt(0);
		assertEquals("-1 -1 -1 -1", highestFrets(fixture));

		fixture.fretted(0, 1, 7);
		fixture.fretted(0, 2, 3);
		fixture.fretted(0, 3, 12, 1);
		fixture.fretted(QUARTER, 1, 5);
		assertEquals("7 12 12 12", highestFrets(fixture));

		TGBeat later = factory.newBeat();
		TGNote note = factory.newNote();
		note.setValue(15);
		later.getVoice(0).addNote(note);
		track.getMeasure(1).addBeat(later);
		assertEquals("7 12 12 15", highestFrets(fixture));
	}

	// of voice 0 of the first beat, of that beat, of its measure, of its track
	private static String highestFrets(MeasureFixture fixture) {
		TGBeat beat = fixture.beatAt(0);
		return beat.getVoice(0).getHighestFret() + " " + beat.getHighestFret()
			+ " " + fixture.measure.getHighestFret() + " " + fixture.measure.getTrack().getHighestFret();
	}

	@Test
	public void aChannelParameterIsFoundByItsIndexOnly() {
		TGChannel channel = factory.newChannel();
		for (String key : new String[] {"first", "second"}) {
			TGChannelParameter parameter = factory.newChannelParameter();
			parameter.setKey(key);
			channel.addParameter(parameter);
		}
		assertEquals("null first second null", key(channel, -1) + " " + key(channel, 0) + " " + key(channel, 1) + " " + key(channel, 2));
	}

	private static String key(TGChannel channel, int index) {
		TGChannelParameter parameter = channel.getParameter(index);
		return (parameter != null ? parameter.getKey() : "null");
	}

	@Test
	public void aChordCountsItsFrettedStrings() {
		TGChord chord = factory.newChord(6);
		assertEquals(0, chord.countNotes());

		chord.addFretValue(0, 3);
		chord.addFretValue(2, 0);
		chord.addFretValue(5, 1);
		assertEquals(3, chord.countNotes());
	}

	@Test
	public void colorsAreEqualWhenTheirRedGreenAndBlueAre() {
		TGColor color = TGColor.newColor(10, 20, 30);
		assertEquals("true false false false", color.isEqual(TGColor.newColor(10, 20, 30))
			+ " " + color.isEqual(TGColor.newColor(11, 20, 30))
			+ " " + color.isEqual(TGColor.newColor(10, 21, 30))
			+ " " + color.isEqual(TGColor.newColor(10, 20, 31)));
	}
}
