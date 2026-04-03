package app.tuxguitar.song.managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import app.tuxguitar.io.tg.TestFileFormat20;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;

public class TestTrackManager {

	@Test
	public void testGetNextTiedNote() throws IOException {
		TGSong song = new TestFileFormat20().readSong("getNextTied_20.tg", true).getSong();
		TGSongManager songMgr = new TGSongManager();
		TGTrackManager trackMgr = songMgr.getTrackManager();

		TGMeasure measure = song.getTrack(0).getMeasure(0);
		TGNote note = measure.getBeat(0).getVoice(0).getNote(0);
		assertTrue(trackMgr.isAnyTiedTo(note));
		TGNote nextTiedNote = trackMgr.getNextTiedNote(note);
		assertNotNull(nextTiedNote);
		assertEquals(1, nextTiedNote.getValue());
		assertEquals(measure.getBeat(1), nextTiedNote.getVoice().getBeat());

		for (int measureIndex = 1; measureIndex <= 4; measureIndex++) {
			measure = song.getTrack(0).getMeasure(measureIndex);
			note = measure.getBeat(0).getVoice(0).getNote(0);
			assertFalse(trackMgr.isAnyTiedTo(note));
			nextTiedNote = trackMgr.getNextTiedNote(note);
			assertNull(nextTiedNote);
		}

		measure = song.getTrack(0).getMeasure(5);
		note = measure.getBeat(0).getVoice(1).getNote(0);
		assertTrue(trackMgr.isAnyTiedTo(note));
		nextTiedNote = trackMgr.getNextTiedNote(note);
		assertNotNull(nextTiedNote);

		measure = song.getTrack(0).getMeasure(6);
		note = measure.getBeat(0).getVoice(1).getNote(0);
		assertFalse(trackMgr.isAnyTiedTo(note));
		nextTiedNote = trackMgr.getNextTiedNote(note);
		assertNull(nextTiedNote);

		measure = song.getTrack(0).getMeasure(7);
		note = measure.getBeat(0).getVoice(0).getNote(0);
		assertFalse(trackMgr.isAnyTiedTo(note));
		nextTiedNote = trackMgr.getNextTiedNote(note);
		assertNull(nextTiedNote);
		note = measure.getBeat(1).getVoice(0).getNote(0);
		assertFalse(trackMgr.isAnyTiedTo(note));
		nextTiedNote = trackMgr.getNextTiedNote(note);
		assertNull(nextTiedNote);
	}

}
