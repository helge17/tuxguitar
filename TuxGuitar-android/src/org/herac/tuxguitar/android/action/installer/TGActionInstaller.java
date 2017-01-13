package org.herac.tuxguitar.android.action.installer;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionAdapterManager;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCloseAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserOpenElementAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserOpenSessionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRunnableAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveNewElementAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoDownAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoUpAction;
import org.herac.tuxguitar.android.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.android.action.impl.caret.TGMoveToAxisPositionAction;
import org.herac.tuxguitar.android.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.android.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.android.action.impl.gui.TGBackAction;
import org.herac.tuxguitar.android.action.impl.gui.TGExitAction;
import org.herac.tuxguitar.android.action.impl.gui.TGFinishAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.action.impl.gui.TGRequestPermissionsAction;
import org.herac.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction;
import org.herac.tuxguitar.android.action.impl.intent.TGProcessIntentAction;
import org.herac.tuxguitar.android.action.impl.layout.TGSetChordDiagramEnabledAction;
import org.herac.tuxguitar.android.action.impl.layout.TGSetChordNameEnabledAction;
import org.herac.tuxguitar.android.action.impl.layout.TGSetLayoutScaleAction;
import org.herac.tuxguitar.android.action.impl.layout.TGSetScoreEnabledAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoFirstMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoLastMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoPreviousMeasureAction;
import org.herac.tuxguitar.android.action.impl.storage.TGOpenDocumentAction;
import org.herac.tuxguitar.android.action.impl.storage.TGSaveDocumentAction;
import org.herac.tuxguitar.android.action.impl.storage.TGSaveDocumentAsAction;
import org.herac.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction;
import org.herac.tuxguitar.android.action.impl.storage.uri.TGUriReadAction;
import org.herac.tuxguitar.android.action.impl.storage.uri.TGUriWriteAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoFirstTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoLastTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoNextTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoPreviousTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoToTrackAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.channel.TGAddChannelAction;
import org.herac.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import org.herac.tuxguitar.editor.action.channel.TGRemoveChannelAction;
import org.herac.tuxguitar.editor.action.channel.TGSetChannelsAction;
import org.herac.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeClefAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeInfoAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeTempoAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import org.herac.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetEighthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetHalfDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetQuarterDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetWholeDurationAction;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeBendNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeGraceNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangePoppingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTappingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloBarAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTrillNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import org.herac.tuxguitar.editor.action.file.TGLoadSongAction;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.editor.action.file.TGNewSongAction;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureAction;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import org.herac.tuxguitar.editor.action.measure.TGCleanMeasureAction;
import org.herac.tuxguitar.editor.action.measure.TGCleanMeasureListAction;
import org.herac.tuxguitar.editor.action.measure.TGCopyMeasureFromAction;
import org.herac.tuxguitar.editor.action.measure.TGInsertMeasuresAction;
import org.herac.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import org.herac.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGChangeStrokeAction;
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.editor.action.note.TGChangeVelocityAction;
import org.herac.tuxguitar.editor.action.note.TGCleanBeatAction;
import org.herac.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import org.herac.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import org.herac.tuxguitar.editor.action.note.TGInsertTextAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveTextAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveUnusedVoiceAction;
import org.herac.tuxguitar.editor.action.note.TGSetNoteFretNumberAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import org.herac.tuxguitar.editor.action.song.TGClearSongAction;
import org.herac.tuxguitar.editor.action.song.TGCopySongFromAction;
import org.herac.tuxguitar.editor.action.track.TGAddNewTrackAction;
import org.herac.tuxguitar.editor.action.track.TGAddTrackAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackPropertiesAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import org.herac.tuxguitar.editor.action.track.TGCloneTrackAction;
import org.herac.tuxguitar.editor.action.track.TGCopyTrackFromAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackNameAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import org.herac.tuxguitar.util.TGContext;

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
		installAction(new TGCopyMeasureFromAction(context));
		installAction(new TGInsertMeasuresAction(context));
		
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
		installAction(new TGTransportLoadSettingsAction(context));
		
		//layout actions
		installAction(new TGSetLayoutScaleAction(context));
		installAction(new TGSetScoreEnabledAction(context));
		installAction(new TGSetChordNameEnabledAction(context));
		installAction(new TGSetChordDiagramEnabledAction(context));
		
		//view actions
		installAction(new TGToggleTabKeyboardAction(context));

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
