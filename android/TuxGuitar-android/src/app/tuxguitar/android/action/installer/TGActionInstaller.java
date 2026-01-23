package app.tuxguitar.android.action.installer;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionAdapterManager;
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRunnableAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveNewElementAction;
import app.tuxguitar.android.action.impl.caret.TGGoDownAction;
import app.tuxguitar.android.action.impl.caret.TGGoLeftAction;
import app.tuxguitar.android.action.impl.caret.TGGoRightAction;
import app.tuxguitar.android.action.impl.caret.TGGoUpAction;
import app.tuxguitar.android.action.impl.caret.TGMoveToAction;
import app.tuxguitar.android.action.impl.caret.TGMoveToAxisPositionAction;
import app.tuxguitar.android.action.impl.edit.TGSetVoice1Action;
import app.tuxguitar.android.action.impl.edit.TGSetVoice2Action;
import app.tuxguitar.android.action.impl.gui.TGBackAction;
import app.tuxguitar.android.action.impl.gui.TGExitAction;
import app.tuxguitar.android.action.impl.gui.TGFinishAction;
import app.tuxguitar.android.action.impl.gui.TGOpenCabMenuAction;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import app.tuxguitar.android.action.impl.gui.TGRequestPermissionsAction;
import app.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction;
import app.tuxguitar.android.action.impl.intent.TGProcessIntentAction;
import app.tuxguitar.android.action.impl.layout.TGSetChordDiagramEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGSetChordNameEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGSetLayoutScaleAction;
import app.tuxguitar.android.action.impl.layout.TGSetScoreEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGToggleHighlightPlayedBeatAction;
import app.tuxguitar.android.action.impl.measure.TGGoFirstMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoLastMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoNextMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoPreviousMeasureAction;
import app.tuxguitar.android.action.impl.storage.TGOpenDocumentAction;
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAction;
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAsAction;
import app.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction;
import app.tuxguitar.android.action.impl.storage.uri.TGUriReadAction;
import app.tuxguitar.android.action.impl.storage.uri.TGUriWriteAction;
import app.tuxguitar.android.action.impl.track.TGGoFirstTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoLastTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoNextTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoPreviousTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoToTrackAction;
import app.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import app.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import app.tuxguitar.android.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.android.action.impl.view.TGShowSmartMenuAction;
import app.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.channel.TGAddChannelAction;
import app.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import app.tuxguitar.editor.action.channel.TGRemoveChannelAction;
import app.tuxguitar.editor.action.channel.TGSetChannelsAction;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.editor.action.composition.TGChangeClefAction;
import app.tuxguitar.editor.action.composition.TGChangeInfoAction;
import app.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import app.tuxguitar.editor.action.composition.TGChangeTempoAction;
import app.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import app.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import app.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import app.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import app.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import app.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import app.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import app.tuxguitar.editor.action.duration.TGSetDurationAction;
import app.tuxguitar.editor.action.duration.TGSetEighthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetHalfDurationAction;
import app.tuxguitar.editor.action.duration.TGSetQuarterDurationAction;
import app.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction;
import app.tuxguitar.editor.action.duration.TGSetWholeDurationAction;
import app.tuxguitar.editor.action.edit.TGRedoAction;
import app.tuxguitar.editor.action.edit.TGUndoAction;
import app.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeBendNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import app.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeGraceNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import app.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import app.tuxguitar.editor.action.effect.TGChangePoppingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import app.tuxguitar.editor.action.effect.TGChangeTappingAction;
import app.tuxguitar.editor.action.effect.TGChangeTremoloBarAction;
import app.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import app.tuxguitar.editor.action.effect.TGChangeTrillNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import app.tuxguitar.editor.action.file.TGLoadSongAction;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.editor.action.file.TGNewSongAction;
import app.tuxguitar.editor.action.file.TGReadSongAction;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import app.tuxguitar.editor.action.measure.TGCleanMeasureAction;
import app.tuxguitar.editor.action.measure.TGCleanMeasureListAction;
import app.tuxguitar.editor.action.measure.TGCopyMeasureAction;
import app.tuxguitar.editor.action.measure.TGCopyMeasureFromAction;
import app.tuxguitar.editor.action.measure.TGFixMeasureVoiceAction;
import app.tuxguitar.editor.action.measure.TGInsertMeasuresAction;
import app.tuxguitar.editor.action.measure.TGPasteMeasureAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction;
import app.tuxguitar.editor.action.measure.TGRemoveUnusedVoiceAction;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction;
import app.tuxguitar.editor.action.note.TGChangeStrokeAction;
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import app.tuxguitar.editor.action.note.TGChangeVelocityAction;
import app.tuxguitar.editor.action.note.TGCleanBeatAction;
import app.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import app.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import app.tuxguitar.editor.action.note.TGInsertTextAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import app.tuxguitar.editor.action.note.TGRemoveTextAction;
import app.tuxguitar.editor.action.note.TGSetNoteFretNumberAction;
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import app.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import app.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import app.tuxguitar.editor.action.song.TGClearSongAction;
import app.tuxguitar.editor.action.song.TGCopySongFromAction;
import app.tuxguitar.editor.action.track.TGAddNewTrackAction;
import app.tuxguitar.editor.action.track.TGAddTrackAction;
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import app.tuxguitar.editor.action.track.TGChangeTrackPropertiesAction;
import app.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import app.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGCopyTrackFromAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import app.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.track.TGSetTrackNameAction;
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import app.tuxguitar.editor.action.track.TGSetTrackStringCountAction;
import app.tuxguitar.editor.action.transport.TGTransportCountDownAction;
import app.tuxguitar.editor.action.transport.TGTransportMetronomeAction;
import app.tuxguitar.util.TGContext;

public class TGActionInstaller {

	private TGActionAdapterManager manager;
	private TGActionConfigMap configMap;

	public TGActionInstaller(TGActionAdapterManager manager) {
		this.manager = manager;
		this.configMap = new TGActionConfigMap();
	}

	public void installDefaultActions(){
		TGContext context = this.manager.getContext();

		//file actions
		installAction(new TGLoadSongAction(context));
		installAction(new TGNewSongAction(context));
		installAction(new TGLoadTemplateAction(context));
		installAction(new TGReadSongAction(context));
		installAction(new TGWriteSongAction(context));

		//edit actions
		installAction(new TGUndoAction(context));
		installAction(new TGRedoAction(context));
		installAction(new TGSetVoice1Action(context));
		installAction(new TGSetVoice2Action(context));

		//caret actions
		installAction(new TGMoveToAction(context));
		installAction(new TGGoRightAction(context));
		installAction(new TGGoLeftAction(context));
		installAction(new TGGoUpAction(context));
		installAction(new TGGoDownAction(context));
		installAction(new TGMoveToAxisPositionAction(context));

		//song actions
		installAction(new TGCopySongFromAction(context));
		installAction(new TGClearSongAction(context));

		//track actions
		installAction(new TGAddTrackAction(context));
		installAction(new TGAddNewTrackAction(context));
		installAction(new TGSetTrackMuteAction(context));
		installAction(new TGSetTrackSoloAction(context));
		installAction(new TGChangeTrackMuteAction(context));
		installAction(new TGChangeTrackSoloAction(context));
		installAction(new TGCloneTrackAction(context));
		installAction(new TGGoFirstTrackAction(context));
		installAction(new TGGoLastTrackAction(context));
		installAction(new TGGoNextTrackAction(context));
		installAction(new TGGoPreviousTrackAction(context));
		installAction(new TGGoToTrackAction(context));
		installAction(new TGMoveTrackDownAction(context));
		installAction(new TGMoveTrackUpAction(context));
		installAction(new TGRemoveTrackAction(context));
		installAction(new TGSetTrackInfoAction(context));
		installAction(new TGSetTrackNameAction(context));
		installAction(new TGSetTrackChannelAction(context));
		installAction(new TGSetTrackStringCountAction(context));
		installAction(new TGChangeTrackTuningAction(context));
		installAction(new TGCopyTrackFromAction(context));
		installAction(new TGChangeTrackPropertiesAction(context));

		//measure actions
		installAction(new TGAddMeasureAction(context));
		installAction(new TGAddMeasureListAction(context));
		installAction(new TGCleanMeasureAction(context));
		installAction(new TGCleanMeasureListAction(context));
		installAction(new TGGoFirstMeasureAction(context));
		installAction(new TGGoLastMeasureAction(context));
		installAction(new TGGoNextMeasureAction(context));
		installAction(new TGGoPreviousMeasureAction(context));
		installAction(new TGRemoveMeasureAction(context));
		installAction(new TGRemoveMeasureRangeAction(context));
		installAction(new TGInsertMeasuresAction(context));
		installAction(new TGCopyMeasureFromAction(context));
		installAction(new TGCopyMeasureAction(context));
		installAction(new TGPasteMeasureAction(context));
		installAction(new TGFixMeasureVoiceAction(context));
		//beat actions
		installAction(new TGChangeNoteAction(context));
		installAction(new TGChangeTiedNoteAction(context));
		installAction(new TGChangeVelocityAction(context));
		installAction(new TGCleanBeatAction(context));
		installAction(new TGDecrementNoteSemitoneAction(context));
		installAction(new TGDeleteNoteOrRestAction(context));
		installAction(new TGIncrementNoteSemitoneAction(context));
		installAction(new TGInsertRestBeatAction(context));
		installAction(new TGMoveBeatsAction(context));
		installAction(new TGMoveBeatsLeftAction(context));
		installAction(new TGMoveBeatsRightAction(context));
		installAction(new TGRemoveUnusedVoiceAction(context));
		installAction(new TGSetVoiceAutoAction(context));
		installAction(new TGSetVoiceDownAction(context));
		installAction(new TGSetVoiceUpAction(context));
		installAction(new TGShiftNoteDownAction(context));
		installAction(new TGShiftNoteUpAction(context));
		installAction(new TGChangePickStrokeDownAction(context));
		installAction(new TGChangePickStrokeUpAction(context));
		installAction(new TGChangeStrokeAction(context));
		installAction(new TGInsertTextAction(context));
		installAction(new TGRemoveTextAction(context));
		for( int i = 0 ; i < 10 ; i ++ ){
			installAction(new TGSetNoteFretNumberAction(context, i));
		}

		//effect actions
		installAction(new TGChangeAccentuatedNoteAction(context));
		installAction(new TGChangeBendNoteAction(context));
		installAction(new TGChangeDeadNoteAction(context));
		installAction(new TGChangeFadeInAction(context));
		installAction(new TGChangeGhostNoteAction(context));
		installAction(new TGChangeGraceNoteAction(context));
		installAction(new TGChangeHammerNoteAction(context));
		installAction(new TGChangeHarmonicNoteAction(context));
		installAction(new TGChangeHeavyAccentuatedNoteAction(context));
		installAction(new TGChangeLetRingAction(context));
		installAction(new TGChangePalmMuteAction(context));
		installAction(new TGChangePoppingAction(context));
		installAction(new TGChangeSlappingAction(context));
		installAction(new TGChangeSlideNoteAction(context));
		installAction(new TGChangeStaccatoAction(context));
		installAction(new TGChangeTappingAction(context));
		installAction(new TGChangeTremoloBarAction(context));
		installAction(new TGChangeTremoloPickingAction(context));
		installAction(new TGChangeTrillNoteAction(context));
		installAction(new TGChangeVibratoNoteAction(context));

		//duration actions
		installAction(new TGSetDurationAction(context));
		installAction(new TGSetWholeDurationAction(context));
		installAction(new TGSetHalfDurationAction(context));
		installAction(new TGSetQuarterDurationAction(context));
		installAction(new TGSetEighthDurationAction(context));
		installAction(new TGSetSixteenthDurationAction(context));
		installAction(new TGSetThirtySecondDurationAction(context));
		installAction(new TGSetSixtyFourthDurationAction(context));
		installAction(new TGSetDivisionTypeDurationAction(context));
		installAction(new TGChangeDottedDurationAction(context));
		installAction(new TGChangeDoubleDottedDurationAction(context));
		installAction(new TGIncrementDurationAction(context));
		installAction(new TGDecrementDurationAction(context));

		//composition actions
		installAction(new TGChangeTempoAction(context));
		installAction(new TGChangeTempoRangeAction(context));
		installAction(new TGChangeClefAction(context));
		installAction(new TGChangeTimeSignatureAction(context));
		installAction(new TGChangeKeySignatureAction(context));
		installAction(new TGChangeTripletFeelAction(context));
		installAction(new TGChangeInfoAction(context));
		installAction(new TGRepeatOpenAction(context));
		installAction(new TGRepeatCloseAction(context));
		installAction(new TGRepeatAlternativeAction(context));

		//channel actions
		installAction(new TGSetChannelsAction(context));
		installAction(new TGAddChannelAction(context));
		installAction(new TGAddNewChannelAction(context));
		installAction(new TGRemoveChannelAction(context));
		installAction(new TGUpdateChannelAction(context));

		//transport actions
		installAction(new TGTransportPlayAction(context));
		installAction(new TGTransportStopAction(context));
		installAction(new TGTransportMetronomeAction(context));
		installAction(new TGTransportCountDownAction(context));
		installAction(new TGTransportLoadSettingsAction(context));

		//layout actions
		installAction(new TGSetLayoutScaleAction(context));
		installAction(new TGSetScoreEnabledAction(context));
		installAction(new TGSetChordNameEnabledAction(context));
		installAction(new TGSetChordDiagramEnabledAction(context));
		installAction(new TGToggleHighlightPlayedBeatAction(context));

		//view actions
		installAction(new TGToggleTabKeyboardAction(context));
		installAction(new TGShowSmartMenuAction(context));

		//storage
		installAction(new TGUriReadAction(context));
		installAction(new TGUriWriteAction(context));
		installAction(new TGOpenDocumentAction(context));
		installAction(new TGSaveDocumentAction(context));
		installAction(new TGSaveDocumentAsAction(context));
		installAction(new TGStorageLoadSettingsAction(context));

		//browser actions
		installAction(new TGBrowserCloseAction(context));
		installAction(new TGBrowserCdRootAction(context));
		installAction(new TGBrowserCdUpAction(context));
		installAction(new TGBrowserCdElementAction(context));
		installAction(new TGBrowserRefreshAction(context));
		installAction(new TGBrowserOpenElementAction(context));
		installAction(new TGBrowserSaveElementAction(context));
		installAction(new TGBrowserSaveNewElementAction(context));
		installAction(new TGBrowserSaveCurrentElementAction(context));
		installAction(new TGBrowserPrepareForReadAction(context));
		installAction(new TGBrowserPrepareForWriteAction(context));
		installAction(new TGBrowserLoadSessionAction(context));
		installAction(new TGBrowserOpenSessionAction(context));
		installAction(new TGBrowserCloseSessionAction(context));
		installAction(new TGBrowserAddCollectionAction(context));
		installAction(new TGBrowserRemoveCollectionAction(context));
		installAction(new TGBrowserRunnableAction(context));

		//intent actions
		installAction(new TGProcessIntentAction(context));

		//gui actions
		installAction(new TGBackAction(context));
		installAction(new TGExitAction(context));
		installAction(new TGFinishAction(context));
		installAction(new TGOpenDialogAction(context));
		installAction(new TGOpenMenuAction(context));
		installAction(new TGOpenCabMenuAction(context));
		installAction(new TGOpenFragmentAction(context));
		installAction(new TGStartActivityForResultAction(context));
		installAction(new TGRequestPermissionsAction(context));
	}

	public void installAction(TGActionBase action) {
		String actionId = action.getName();

		TGActionManager.getInstance(this.manager.getContext()).mapAction(actionId, action);
		TGActionConfig config = this.configMap.get(actionId);
		if( config != null ) {
			if( config.isDisableOnPlaying() ) {
				this.manager.getDisableOnPlayInterceptor().addActionId(actionId);
			}
			if( config.isStopTransport() ) {
				this.manager.getStopTransportInterceptor().addActionId(actionId);
			}
			if( config.isSyncThread() ) {
				this.manager.getSyncThreadInterceptor().addActionId(actionId);
			}
			if( config.isLockableAction() ) {
				this.manager.getLockableActionListener().addActionId(actionId);
			}

			this.manager.getUpdatableActionListener().getControllers().set(actionId, config.getUpdateController());
			this.manager.getUndoableActionListener().getControllers().set(actionId, config.getUndoableController());
		}
	}
}
