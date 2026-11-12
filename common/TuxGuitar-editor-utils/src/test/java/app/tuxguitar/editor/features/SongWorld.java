package app.tuxguitar.editor.features;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;

/**
 * The state of one scenario: the song the steps edit and the beat the caret is on. Cucumber makes one per scenario
 * and gives the same one to every step class that takes it in its constructor, so steps of different classes work on
 * the same song.
 */
public class SongWorld {

	private SongActionFixture fixture;
	private TGBeat caretBeat;

	// a new song, one guitar track in standard tuning, its 4/4 measure full of quarter beats (a measure that is not full
	// switches the editor to free edition mode), the caret on the first
	public void newSong() {
		fixture = new SongActionFixture();
		TGMeasure measure = track().getMeasure(0);
		caretBeat = fixture.beat(measure, 0);
		for (int quarter = 1; quarter < 4; quarter++) {
			fixture.beat(measure, quarter);
		}
	}

	public SongActionFixture fixture() {
		return fixture;
	}

	public TGTrack track() {
		return fixture.song.getTrack(0);
	}

	public TGBeat caretBeat() {
		return caretBeat;
	}
}
