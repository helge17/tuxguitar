package app.tuxguitar.audio.action;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.editor.action.transport.TGTransportModeAction;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonUtil;

/**
 * During a recording the song cannot be edited and the Play Mode, the punch range, arming and input channels cannot be
 * changed: the action manager refuses those actions while the session says a recording is under way (an intercepted
 * action does not run, so it reports no success and makes no undo step). Mute, solo and monitoring still work.
 *
 * The audio features' own actions and the Play Mode are refused here. Which editing actions change the song is the
 * app's to say: it refuses them too (desktop: the actions it disables while playing). One per context, registered with
 * the context's action manager when first asked for.
 */
public final class TGRecordingVeto implements TGActionInterceptor {

	private final TGAudioSession session;
	private final Set<String> refused = ConcurrentHashMap.newKeySet();

	private TGRecordingVeto(TGAudioSession session) {
		this.session = session;
		this.refuse(TGTransportModeAction.NAME, TGSetPunchRangeAction.NAME, TGArmTrackAction.NAME, TGDisarmTrackAction.NAME,
			TGSetInputChannelsAction.NAME, TGAddAudioTrackAction.NAME, TGChooseTakeAction.NAME, TGDeleteTakeAction.NAME,
			TGRecordAction.NAME);
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

	public boolean intercept(String id, TGActionContext context) {
		return (this.session.isRecording() && this.refused.contains(id));
	}
}
