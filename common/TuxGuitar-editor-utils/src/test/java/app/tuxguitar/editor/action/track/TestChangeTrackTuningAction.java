package app.tuxguitar.editor.action.track;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

/**
 * Pins TGChangeTrackTuningAction: the track takes the new strings and its notes keep their pitch. When every string
 * moves by the same interval, each note stays on its string (the chord diagram follows); otherwise the notes are laid
 * on the new strings again (the chord diagram is dropped). The offset, when given, is set too.
 *
 * The track is a guitar in standard tuning (E2 A2 D3 G3 B3 E4) with one beat: a note at fret 0 on string 6 and fret 5
 * on string 3, under a chord diagram. It is read back as "tuning | s<string>f<fret> ... | chord".
 */
public class TestChangeTrackTuningAction {

	private static final int[] STANDARD = { 64, 59, 55, 50, 45, 40 };

	private final TGContext context = new TGContext();
	private final TGActionManager actionManager = TGActionManager.getInstance(context);
	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGTrack track = song.getTrack(0);
	private final TGBeat beat = songManager.getFactory().newBeat();
	private final TGActionContext actionContext = new TGActionContext() {
	};

	public TestChangeTrackTuningAction() {
		actionManager.mapAction(TGChangeTrackTuningAction.NAME, new TGChangeTrackTuningAction(context));
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, songManager);
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);

		TGMeasure measure = track.getMeasure(0);
		beat.setStart(measure.getStart());
		TGVoice voice = beat.getVoice(0);
		voice.setEmpty(false);
		voice.getDuration().setValue(TGDuration.QUARTER);
		TGChord chord = songManager.getFactory().newChord(6);
		for (int[] stringFret : new int[][] { { 6, 0 }, { 3, 5 } }) {
			TGNote note = songManager.getFactory().newNote();
			note.setString(stringFret[0]);
			note.setValue(stringFret[1]);
			voice.addNote(note);
			chord.addFretValue(stringFret[0] - 1, stringFret[1]);
		}
		beat.setChord(chord);
		songManager.getMeasureManager().addBeat(measure, beat);
	}

	private List<TGString> strings(int... values) {
		List<TGString> strings = new ArrayList<TGString>();
		for (int i = 0; i < values.length; i++) {
			TGString string = songManager.getFactory().newString();
			string.setNumber(i + 1);
			string.setValue(values[i]);
			strings.add(string);
		}
		return strings;
	}

	private static int[] shifted(int interval) {
		int[] values = new int[STANDARD.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = STANDARD[i] + interval;
		}
		return values;
	}

	private String retune(int... values) {
		actionContext.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_STRINGS, strings(values));
		actionManager.execute(TGChangeTrackTuningAction.NAME, actionContext);
		return describe();
	}

	private String describe() {
		StringBuilder description = new StringBuilder();
		for (TGString string : track.getStrings()) {
			description.append(string.getValue()).append(' ');
		}
		description.append('|');
		for (TGNote note : beat.getVoice(0).getNotes()) {
			description.append(" s").append(note.getString()).append('f').append(note.getValue());
		}
		description.append(" |");
		TGChord chord = beat.getChord();
		if (chord == null) {
			return description.append(" no chord").toString();
		}
		for (int i = 0; i < chord.countStrings(); i++) {
			if (chord.getFretValue(i) >= 0) {
				description.append(" s").append(i + 1).append('f').append(chord.getFretValue(i));
			}
		}
		return description.toString();
	}

	@Test
	public void tuningEveryStringDownKeepsEachNoteOnItsString() {
		assertEquals("63 58 54 49 44 39 | s6f1 s3f6 | s3f6 s6f1", retune(shifted(-1)));
	}

	@Test
	public void tuningEveryStringUpDropsANoteNowBelowTheLowestString() {
		assertEquals("66 61 57 52 47 42 | s3f3 | s3f3", retune(shifted(2)));
	}

	@Test
	public void anUnevenRetuningLaysTheNotesAgain() {
		assertEquals("64 59 55 50 45 38 | s6f2 s3f5 | no chord", retune(64, 59, 55, 50, 45, 38));
	}

	@Test
	public void unevenShiftsOfEveryStringLayTheNotesAgain() {
		assertEquals("63 58 54 49 44 37 | s6f3 s3f6 | no chord", retune(63, 58, 54, 49, 44, 37));
	}

	@Test
	public void anotherStringCountLaysTheNotesAgain() {
		assertEquals("64 59 55 50 45 40 35 | s6f0 s3f5 | no chord", retune(64, 59, 55, 50, 45, 40, 35));
	}

	@Test
	public void theSameTuningChangesNothing() {
		assertEquals("64 59 55 50 45 40 | s6f0 s3f5 | s3f5 s6f0", retune(STANDARD));
	}

	@Test
	public void setsTheOffsetAlone() {
		actionContext.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_OFFSET, 3);
		actionManager.execute(TGChangeTrackTuningAction.NAME, actionContext);

		assertEquals(3, track.getOffset());
		assertEquals("64 59 55 50 45 40 | s6f0 s3f5 | s3f5 s6f0", describe());
	}

	@Test
	public void setsTheOffsetWithTheTuning() {
		actionContext.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_OFFSET, -2);

		assertEquals("63 58 54 49 44 39 | s6f1 s3f6 | s3f6 s6f1", retune(shifted(-1)));
		assertEquals(-2, track.getOffset());
	}

	@Test
	public void withoutTrackNothingChanges() {
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, null);
		actionContext.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_OFFSET, 3);

		assertEquals("64 59 55 50 45 40 | s6f0 s3f5 | s3f5 s6f0", retune(shifted(-1)));
		assertEquals(0, track.getOffset());
	}
}
