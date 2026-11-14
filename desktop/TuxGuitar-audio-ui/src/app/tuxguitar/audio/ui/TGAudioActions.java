package app.tuxguitar.audio.ui;

import java.util.Arrays;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.TGActionAdapterManager;
import app.tuxguitar.app.action.TGActionMap;
import app.tuxguitar.app.action.listener.cache.TGUpdateController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateAddedTrackController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateChannelsController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateItemsController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateMeasureController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateSongController;
import app.tuxguitar.audio.action.TGAddAudioTrackAction;
import app.tuxguitar.audio.action.TGAddVst3EffectAction;
import app.tuxguitar.audio.action.TGArmTrackAction;
import app.tuxguitar.audio.action.TGChooseGeneralMidiAction;
import app.tuxguitar.audio.action.TGChooseVst3InstrumentAction;
import app.tuxguitar.audio.action.TGDisarmTrackAction;
import app.tuxguitar.audio.action.TGMoveEffectUpAction;
import app.tuxguitar.audio.action.TGOpenPluginEditorAction;
import app.tuxguitar.audio.action.TGRecordAction;
import app.tuxguitar.audio.action.TGRemoveEffectAction;
import app.tuxguitar.audio.action.TGResetClipIndicatorAction;
import app.tuxguitar.audio.action.TGSetEffectEnabledAction;
import app.tuxguitar.audio.action.TGSetInputChannelsAction;
import app.tuxguitar.audio.action.TGSetMonitoringAction;
import app.tuxguitar.audio.action.TGSetPluginParameterAction;
import app.tuxguitar.audio.action.TGSetPluginStateAction;
import app.tuxguitar.audio.undo.TGUndoableOnSuccess;
import app.tuxguitar.audio.undo.TGUndoableRecordingController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.editor.action.note.TGSetNoteFretNumberAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableAddTrackController;
import app.tuxguitar.util.TGContext;

/**
 * The audio actions as the desktop maps its own (TGActionConfigMap): what view each updates, and which change the song
 * and how they are undone. Arming and monitoring are session state: no undo step, the song not modified. A change of a
 * sound chain is sent to the player (its synthesizer loads the chain again: the live plugins follow). Only opening a
 * plugin's editor runs on the UI thread (a plugin's window); the record action waits until the recording ends.
 */
final class TGAudioActions {

	private TGAudioActions() {
	}

	static void map(TGContext context) {
		TGUndoableActionController channel = new TGUndoableOnSuccess(new TGUndoableModifyChannelController());
		map(context, new TGRecordAction(context), new TGUpdateSongController(), new TGUndoableRecordingController());
		map(context, new TGAddAudioTrackAction(context), new TGUpdateAddedTrackController(), new TGUndoableAddTrackController());
		map(context, new TGSetInputChannelsAction(context), new TGUpdateItemsController(), channel);
		map(context, new TGSetPluginStateAction(context), new TGUpdateItemsController(), channel);
		map(context, new TGArmTrackAction(context), new TGUpdateItemsController(), null);
		map(context, new TGDisarmTrackAction(context), new TGUpdateItemsController(), null);
		map(context, new TGSetMonitoringAction(context), new TGUpdateItemsController(), null);
		map(context, new TGResetClipIndicatorAction(context), new TGUpdateItemsController(), null);
		mapSoundChain(context, channel);
		soundNotesOnce(context);
	}

	private static void mapSoundChain(TGContext context, TGUndoableActionController channel) {
		Arrays.asList(new TGAddVst3EffectAction(context), new TGChooseVst3InstrumentAction(context), new TGChooseGeneralMidiAction(context), new TGMoveEffectUpAction(context), new TGRemoveEffectAction(context), new TGSetEffectEnabledAction(context), new TGSetPluginParameterAction(context)).forEach(action -> map(context, action, new TGUpdateChannelsController(), channel));
		map(context, new TGOpenPluginEditorAction(context), new TGUpdateItemsController(), null);
		TGActionAdapterManager.getInstance(context).getSyncThreadInterceptor().addActionId(TGOpenPluginEditorAction.NAME);
	}

	// TGNoteEntry sounds a note entered through its track's sound chain: the desktop's note controller, which sounds it
	// too, is replaced by the measure controller it extends, so each note sounds once
	private static void soundNotesOnce(TGContext context) {
		TGActionMap<TGUpdateController> controllers = TGActionAdapterManager.getInstance(context).getUpdatableActionListener().getControllers();
		controllers.set(TGChangeNoteAction.NAME, new TGUpdateMeasureController());
		for (int number = 0; number < 10; number++) {
			controllers.set(TGSetNoteFretNumberAction.getActionName(number), new TGUpdateMeasureController());
		}
	}

	private static void map(TGContext context, TGActionBase action, TGUpdateController update, TGUndoableActionController undoable) {
		TGActionAdapterManager adapter = TGActionAdapterManager.getInstance(context);
		TGActionManager.getInstance(context).mapAction(action.getName(), action);
		adapter.getUpdatableActionListener().getControllers().set(action.getName(), update);
		adapter.getUndoableActionListener().getControllers().set(action.getName(), undoable);
		if (undoable != null) {
			adapter.getDocumentModifierListener().addActionId(action.getName());
		}
	}
}
