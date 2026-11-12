package app.tuxguitar.audio.undo;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.action.TGRecordAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.util.TGContext;

/**
 * A refused recording is no undo step, whatever its action context still holds from an earlier one.
 */
public class TestUndoableRecordingController {

	@Test
	public void aRefusedRecordingIsNoUndoStepEvenWithTakesLeftInItsContext() {
		TGContext context = new TGContext();
		TGActionContext actionContext = new TGActionContext() {
		};
		actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, new TGSongManager().newSong());
		actionContext.setAttribute(TGRecordAction.ATTRIBUTE_TAKES, 2);
		TGUndoableRecordingController controller = new TGUndoableRecordingController();

		assertNull(controller.endUndoable(context, actionContext, controller.startUndoable(context, actionContext)));
	}
}
