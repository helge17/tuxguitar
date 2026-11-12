package app.tuxguitar.audio.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.audio.MapProperties;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.util.properties.TGPropertiesManager;
import app.tuxguitar.audio.TGSongTime;
import app.tuxguitar.audio.TGTimeline;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.audio.TGTakeSpan;
import app.tuxguitar.audio.TGTakes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.channel.TGAddChannelAction;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.editor.action.measure.TGInsertMeasuresAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import app.tuxguitar.editor.action.song.TGCopySongFromAction;
import app.tuxguitar.editor.action.composition.TGChangeTempoAction;
import app.tuxguitar.editor.action.track.TGAddTrackAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGCopyTrackFromAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableBuffer;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTrack;

/**
 * The state of one scenario: the song of a document (so undo works on it), the action context the steps run actions
 * on, the audio device, and what the steps remember to compare with later. Cucumber makes one per scenario and gives
 * it to every step class that takes it in its constructor.
 *
 * An action runs as the app runs it: on a context whose outcome of an earlier action is cleared, and, when the app maps
 * an undoable controller to it (desktop's TGActionConfigMap), wrapped by that controller, its edit kept for undo when
 * there is one. The actions undo and redo run by name are mapped, as the app maps every action.
 */
public class AudioWorld {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final TGUndoableBuffer undoHistory = new TGUndoableBuffer();
	private final TestInterface device = new TestInterface();
	private final Map<String, float[]> takeSamples = new HashMap<String, float[]>();

	public AudioWorld() {
		this.fixture.map(new TGAddTrackAction(this.fixture.context), new TGRemoveTrackAction(this.fixture.context),
			new TGCloneTrackAction(this.fixture.context), new TGMoveTrackUpAction(this.fixture.context),
			new TGMoveTrackDownAction(this.fixture.context), new TGCopyTrackFromAction(this.fixture.context),
			new TGAddChannelAction(this.fixture.context), new TGAddMeasureAction(this.fixture.context),
			new TGRemoveMeasureAction(this.fixture.context), new TGInsertMeasuresAction(this.fixture.context),
			new TGCopySongFromAction(this.fixture.context), new TGChangeTempoAction(this.fixture.context));
		TGUndoableManager.getInstance(this.fixture.context).setBuffer(this.undoHistory);
		TGPropertiesManager.getInstance(this.fixture.context).setPropertiesFactory(MapProperties::new);
	}

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

	public TGAudioSession session() {
		return TGAudioSession.getInstance(this.fixture.context);
	}

	public TestInterface device() {
		return this.device;
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

	// the player (the scenario's musician) plays along with what is about to be played
	public void playAlong(long start, long stop, int passes) {
		MidiPlayerMode mode = MidiPlayer.getInstance(this.fixture.context).getMode();
		this.device.setPlayed(new TGTimeline(song(), TGSongTime.play(song(), mode, start, stop, passes), this.device.getSampleRate()));
	}

	public boolean succeeded() {
		return Boolean.TRUE.equals(this.fixture.actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS));
	}

	// the undo history as it stands: its edits, then where the next one goes
	public List<Object> undoHistory() {
		List<Object> history = new ArrayList<Object>(this.undoHistory.getEdits());
		history.add(this.undoHistory.getIndexOfNextAdd());
		return history;
	}

	// keeps a copy of the samples of the track's takes not kept yet, to compare with later
	public void rememberTakes(int trackNumber) {
		for (TGTakeSpan span : TGTakes.list(track(trackNumber))) {
			this.takeSamples.putIfAbsent(key(trackNumber, span.getTake().getNumber()), samples(span.getTake()));
		}
	}

	public float[] rememberedSamples(int trackNumber, int takeNumber) {
		return this.takeSamples.get(key(trackNumber, takeNumber));
	}

	public static float[] samples(TGTake take) {
		float[] samples = new float[take.countSamples()];
		for (int i = 0; i < samples.length; i++) {
			samples[i] = take.getSample(i);
		}
		return samples;
	}

	private static String key(int trackNumber, int takeNumber) {
		return trackNumber + ":" + takeNumber;
	}
}
