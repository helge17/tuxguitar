package app.tuxguitar.editor.action.song;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGSong;

/**
 * TGCopySongFromAction copies another song over the song and reports success, so an action refused before it runs
 * (as during a recording) can be told from a done one.
 */
public class TestCopySongFromAction {

	private final SongActionFixture fixture = new SongActionFixture();

	@Test
	public void copiesTheOtherSongOverTheSongAndReportsSuccess() {
		TGSong from = fixture.songManager.newSong();
		fixture.songManager.addNewMeasureBeforeEnd(from);
		fixture.songManager.addNewMeasureBeforeEnd(from);
		fixture.actionContext.setAttribute(TGCopySongFromAction.ATTRIBUTE_FROM, from);

		fixture.execute(new TGCopySongFromAction(fixture.context));

		assertEquals("3 measures success", fixture.song.countMeasureHeaders() + " measures " + fixture.outcome());
	}
}
