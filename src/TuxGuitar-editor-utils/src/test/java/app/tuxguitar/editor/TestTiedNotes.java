package app.tuxguitar.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.io.tg.TGSongReaderImpl;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.util.TGContext;

public class TestTiedNotes {

	private TGActionManager actionManager;
	private TGContext context;
	private TGSongManager songManager;
	private TGActionContext actionContext;

	public TestTiedNotes() throws IOException {
		this.context = new TGContext();
		this.actionManager = TGActionManager.getInstance(context);
		this.songManager = new TGSongManager();
		this.actionContext = new TGActionContext() {
		};
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, songManager);
	}

	@Test
	public void testCreateTiedNotes() throws IOException {
		this.actionManager.mapAction(TGChangeTiedNoteAction.NAME, new TGChangeTiedNoteAction(context));
		
		TGSong song = this.readTestSong();
		// where creation of a tie should fail
		List<TGNote> invalidTies = new ArrayList<TGNote>();
		invalidTies.add(song.getTrack(0).getMeasure(0).getBeat(0).getVoice(0).getNote(0));
		invalidTies.add(song.getTrack(0).getMeasure(0).getBeat(0).getVoice(0).getNote(0));
		invalidTies.add(song.getTrack(0).getMeasure(0).getBeat(3).getVoice(0).getNote(0));
		invalidTies.add(song.getTrack(0).getMeasure(1).getBeat(3).getVoice(0).getNote(0));
		// where it should succeed
		List<TGNote> validTies = new ArrayList<TGNote>();
		validTies.add(song.getTrack(0).getMeasure(1).getBeat(4).getVoice(0).getNote(0));
		validTies.add(song.getTrack(0).getMeasure(2).getBeat(0).getVoice(0).getNote(0));
		
		for (boolean isFreeEditionMode : new boolean[] { false, true }) {
			this.songManager.setFreeEditionMode(isFreeEditionMode);
			for (TGNote note : validTies) {
				// just checking the test scenario
				assertFalse(note.isTiedNote());
				this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
				this.actionManager.execute(TGChangeTiedNoteAction.NAME, this.actionContext);
				assertTrue(note.isTiedNote());
				// check harmonic propagation
				if (note.getVoice().getBeat().getMeasure().getNumber()==2) {
					assertEquals(isFreeEditionMode, !note.getEffect().isHarmonic());
				}
				
				// undo (for next iteration)
				this.actionManager.execute(TGChangeTiedNoteAction.NAME, this.actionContext);
				if (note.getVoice().getBeat().getMeasure().getNumber()==2) {
					note.getEffect().setHarmonic(null);
				}
			}
			for (TGNote note : invalidTies) {
				// just checking the test scenario
				assertFalse(note.isTiedNote());
				this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
				this.actionManager.execute(TGChangeTiedNoteAction.NAME, this.actionContext);
				assertEquals(note.isTiedNote(), isFreeEditionMode);
				// undo (for next iteration)
				if (isFreeEditionMode) {
					this.actionManager.execute(TGChangeTiedNoteAction.NAME, this.actionContext);
				}
			}
		}
		
		// try to create tied notes with no selected note
		TGBeat beat = song.getTrack(0).getMeasure(0).getBeat(2);
		assertTrue(beat.isRestBeat());
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, null);
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, beat.getVoice(0));
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, beat.getVoice(0).getDuration());
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, beat.getMeasure());
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY,
				song.getTrack(0).getMeasure(0).getBeat(0).getVoice(0).getNote(0).getVelocity());
		TGString invalidString = song.getTrack(0).getString(1);
		TGString validString = song.getTrack(0).getString(2);

		for (boolean isFreeEditionMode : new boolean[] { false, true }) {
			this.songManager.setFreeEditionMode(isFreeEditionMode);
			// valid
			this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, validString);
			this.actionManager.execute(TGChangeTiedNoteAction.NAME, this.actionContext);
			assertFalse(beat.isRestBeat());
			assertNotNull(beat.getVoice(0).getNote(0));
			assertTrue(beat.getVoice(0).getNote(0).isTiedNote());
			assertEquals(1,beat.getVoice(0).getNote(0).getValue());
			// undo (for next iteration)
			this.songManager.getMeasureManager().cleanBeat(beat);
			
			// invalid
			this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, invalidString);
			this.actionManager.execute(TGChangeTiedNoteAction.NAME, this.actionContext);
			assertEquals(beat.isRestBeat(), !isFreeEditionMode);
			assertEquals(beat.getVoice(0).getNote(0)==null, !isFreeEditionMode);
			if (isFreeEditionMode) {
				assertTrue(beat.getVoice(0).getNote(0).isTiedNote());
				assertEquals(0,beat.getVoice(0).getNote(0).getValue());
				// undo (for next iteration)
				this.songManager.getMeasureManager().cleanBeat(beat);
			}
		}
	}
	
	private TGSong readTestSong() throws IOException {
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setContext(new TGSongStreamContext());
		handle.setInputStream(getClass().getClassLoader().getResource("tiedNotes_20.tg").openStream());
		handle.setFactory(new TGFactory());
		TGSongReaderImpl songReader = new TGSongReaderImpl();
		songReader.read(handle);
		return handle.getSong();
	}

}
