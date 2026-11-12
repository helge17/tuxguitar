package app.tuxguitar.editor.features;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGVelocities;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

/**
 * Steps that edit notes, the way the tablature editor does it: the caret is put on a string of the caret beat, then
 * the action runs there.
 */
public class NoteSteps {

	private final SongWorld world;

	public NoteSteps(SongWorld world) {
		this.world = world;
	}

	// typing a fret at the caret, a quarter note at the default velocity; the outcome of an earlier action is cleared,
	// as the app runs each action on a new action context
	@Given("I have entered fret {int} on string {int}")
	@When("I enter fret {int} on string {int}")
	public void enterFret(int fret, int string) {
		SongActionFixture fixture = world.fixture();
		fixture.caret(world.caretBeat(), string);
		fixture.actionContext.setAttribute(TGActionBase.ATTRIBUTE_SUCCESS, null);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, world.caretBeat().getStart());
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, fret);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, TGVelocities.DEFAULT);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, fixture.songManager.getFactory().newDuration());
		fixture.execute(new TGChangeNoteAction(fixture.context));
	}
}
