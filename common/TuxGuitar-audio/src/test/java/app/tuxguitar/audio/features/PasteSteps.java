package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.measure.TGCopyMeasureAction;
import app.tuxguitar.editor.action.measure.TGPasteMeasureAction;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.editor.undo.impl.song.TGUndoableSongGenericController;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps that put notes on a track, and copy and paste measures of one track.
 */
public class PasteSteps {

	private final AudioWorld world;

	public PasteSteps(AudioWorld world) {
		this.world = world;
	}

	// fret 5 on string 1, a quarter note on each beat of every measure
	@Given("track {int} has a quarter note on every beat")
	public void aQuarterNoteOnEveryBeat(int number) {
		TGTrack track = world.track(number);
		for (int index = 0; index < track.countMeasures(); index++) {
			for (int quarter = 0; quarter < 4; quarter++) {
				world.fixture().note(world.fixture().beat(track.getMeasure(index), quarter), 1, 5);
			}
		}
	}

	@Given("I have copied measure {int} of track {int}")
	public void copyMeasure(int measure, int number) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, world.track(number));
		world.fixture().actionContext.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_1, measure);
		world.fixture().actionContext.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_2, measure);
		world.fixture().actionContext.setAttribute(TGCopyMeasureAction.ATTRIBUTE_ALL_TRACKS, Boolean.FALSE);
		world.execute(new TGCopyMeasureAction(world.fixture().context));
		assertNotNull(TGClipboard.getInstance(world.fixture().context).getSegment());
	}

	// replacing the measure, once, as desktop's TGActionConfigMap runs it: undoable as a whole song
	@When("I paste it over measure {int} of track {int}")
	public void pasteOver(int measure, int number) {
		TGTrack track = world.track(number);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, track.getMeasure(measure - 1));
		world.fixture().actionContext.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_MODE, TGPasteMeasureAction.TRANSFER_TYPE_REPLACE);
		world.fixture().actionContext.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_COUNT, 1);
		world.executeUndoable(new TGPasteMeasureAction(world.fixture().context), new TGUndoableSongGenericController());
	}

	@Then("track {int} still has {int} notes in measure {int}")
	public void stillHasNotes(int number, int notes, int measure) {
		TGMeasure trackMeasure = world.track(number).getMeasure(measure - 1);
		assertEquals(notes, trackMeasure.getBeats().stream().mapToInt((TGBeat beat) -> beat.getVoice(0).countNotes()).sum());
	}

	// on the audio track (track 2 in the takes feature)
	@Given("I have copied measure {int} and pasted it over measure {int}")
	public void copyAndPaste(int from, int to) {
		copyMeasure(from, 2);
		pasteOver(to, 2);
		assertTrue(world.succeeded());
	}
}
