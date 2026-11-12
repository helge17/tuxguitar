package app.tuxguitar.audio.features;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * The state of one scenario: the song of a document (so undo works on it) and the action context the steps run
 * actions on. Cucumber makes one per scenario and gives it to every step class that takes it in its constructor.
 *
 * An action runs as the app runs it: on a context whose outcome of an earlier action is cleared, and, when the app maps
 * an undoable controller to it (desktop's TGActionConfigMap), wrapped by that controller, its edit kept for undo when
 * there is one.
 */
public class AudioWorld {

	private final SongActionFixture fixture = SongActionFixture.onDocument();

	public SongActionFixture fixture() {
		return this.fixture;
	}

	public TGSong song() {
		return this.fixture.song;
	}

	// track numbers start at 1
	public TGTrack track(int number) {
		return this.fixture.songManager.getTrack(song(), number);
	}

	public TGTrack lastTrack() {
		return this.fixture.songManager.getLastTrack(song());
	}

	public TGChannel channel(TGTrack track) {
		return this.fixture.songManager.getChannel(song(), track.getChannelId());
	}

	public void execute(TGActionBase action) {
		this.fixture.actionContext.setAttribute(TGActionBase.ATTRIBUTE_SUCCESS, null);
		this.fixture.execute(action);
	}

	public void executeUndoable(TGActionBase action, TGUndoableActionController controller) {
		this.fixture.actionContext.setAttribute(TGActionBase.ATTRIBUTE_SUCCESS, null);
		TGUndoableEdit started = controller.startUndoable(this.fixture.context, this.fixture.actionContext);
		this.fixture.execute(action);
		TGUndoableEdit edit = controller.endUndoable(this.fixture.context, this.fixture.actionContext, started);
		if (edit != null) {
			TGUndoableManager.getInstance(this.fixture.context).addEdit(edit);
		}
	}

	public boolean succeeded() {
		return Boolean.TRUE.equals(this.fixture.actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS));
	}
}
