package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.editor.undo.impl.measure.TGUndoableMeasureGenericController;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVelocities;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps that enter notes and count them. The caret is on a track's first measure, at its start.
 */
public class NoteSteps {

	private final AudioWorld world;
	private TGTrack caretTrack;

	public NoteSteps(AudioWorld world) {
		this.world = world;
	}

	@Given("I have moved the caret to track {int}")
	public void moveTheCaretTo(int number) {
		caretTrack = world.track(number);
	}

	// as the tablature editor does it: the caret's track, measure, string and beat (none on an audio track, which has
	// no strings and no beats), then the fret typed there
	@When("I enter fret {int} on string {int}")
	public void enterFret(int fret, int string) {
		enterFret(fret, string, TGVelocities.DEFAULT);
	}

	// on track 1 unless the caret was moved
	@Given("I have entered fret {int} on string {int}")
	public void haveEnteredFret(int fret, int string) {
		haveEnteredFret(fret, string, "f");
	}

	@Given("I have entered fret {int} on string {int} at dynamic {word}")
	public void haveEnteredFret(int fret, int string, String dynamic) {
		caretTrack = (caretTrack != null ? caretTrack : world.track(1));
		if (caretTrack.getMeasure(0).countBeats() == 0) {
			world.fixture().beat(caretTrack.getMeasure(0), 0);
		}
		enterFret(fret, string, DYNAMICS.get(dynamic));
		assertTrue(world.succeeded());
	}

	private static final java.util.Map<String, Integer> DYNAMICS = java.util.Map.of("ppp", TGVelocities.PIANO_PIANISSIMO, "pp", TGVelocities.PIANISSIMO,
		"p", TGVelocities.PIANO, "mp", TGVelocities.MEZZO_PIANO, "mf", TGVelocities.MEZZO_FORTE, "f", TGVelocities.FORTE,
		"ff", TGVelocities.FORTISSIMO, "fff", TGVelocities.FORTE_FORTISSIMO);

	private void enterFret(int fret, int string, int velocity) {
		TGMeasure measure = caretTrack.getMeasure(0);
		TGBeat beat = measure.getBeat(0);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, caretTrack);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, beat == null ? null : beat.getVoice(0));
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string <= caretTrack.stringCount() ? caretTrack.getString(string) : null);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, measure.getStart());
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, fret);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, velocity);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, world.fixture().songManager.getFactory().newDuration());
		world.executeUndoable(new TGChangeNoteAction(world.fixture().context), new TGUndoableMeasureGenericController());
	}

	@Then("track {int} holds no notes")
	public void holdsNoNotes(int number) {
		assertEquals(0, countNotes(world.track(number)));
	}

	private static int countNotes(TGTrack track) {
		int notes = 0;
		Iterator<TGMeasure> measures = track.getMeasures();
		while (measures.hasNext()) {
			for (TGBeat beat : measures.next().getBeats()) {
				notes += beat.getVoice(0).countNotes();
			}
		}
		return notes;
	}
}
