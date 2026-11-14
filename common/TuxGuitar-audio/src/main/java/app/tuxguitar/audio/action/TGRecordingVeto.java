package app.tuxguitar.audio.action;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.editor.action.song.TGCopySongFromAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.transport.TGTransportModeAction;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonUtil;

/**
 * During a recording the song cannot be edited and the Play Mode, the punch range, arming and input channels cannot be
 * changed: the action manager refuses those actions while the session says a recording is under way (an intercepted
 * action does not run, so it reports no success and makes no undo step). Mute, solo, Volume and Balance and monitoring
 * still work.
 *
 * The audio features' own actions, the Play Mode, moving a track and copying a song in are refused here (the app lets
 * those run while playing). Which other editing actions change the song is the app's to say: it refuses them too
 * (desktop: the actions it disables while playing). One per context, registered with the context's action manager when
 * first asked for.
 */
public final class TGRecordingVeto implements TGActionInterceptor {

	private final TGAudioSession session;
	private final Set<String> refused = ConcurrentHashMap.newKeySet();
	// the actions refused: those named, and those of the rules an app gave
	private volatile Predicate<String> rule = this.refused::contains;

	private TGRecordingVeto(TGAudioSession session) {
		this.session = session;
		this.refuse(TGTransportModeAction.NAME, TGSetPunchRangeAction.NAME, TGArmTrackAction.NAME, TGDisarmTrackAction.NAME,
			TGSetInputChannelsAction.NAME, TGAddAudioTrackAction.NAME, TGChooseTakeAction.NAME, TGDeleteTakeAction.NAME,
			TGRecordAction.NAME, TGMoveTrackUpAction.NAME, TGMoveTrackDownAction.NAME, TGCopySongFromAction.NAME);
	}

	public static TGRecordingVeto getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGRecordingVeto.class.getName(), TGRecordingVeto::registered);
	}

	private static TGRecordingVeto registered(TGContext context) {
		TGRecordingVeto veto = new TGRecordingVeto(TGAudioSession.getInstance(context));
		TGActionManager.getInstance(context).addInterceptor(veto);
		return veto;
	}

	// these actions are refused during a recording too
	public void refuse(String... actionIds) {
		this.refused.addAll(Arrays.asList(actionIds));
	}

	// the actions the rule names are refused during a recording too
	public void refuse(Predicate<String> rule) {
		this.rule = this.rule.or(rule);
	}

	public boolean intercept(String id, TGActionContext context) {
		return (this.session.isRecording() && this.rule.test(id));
	}
}
