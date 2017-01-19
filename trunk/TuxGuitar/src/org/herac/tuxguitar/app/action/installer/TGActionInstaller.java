package org.herac.tuxguitar.app.action.installer;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.TGActionAdapterManager;
import org.herac.tuxguitar.app.action.impl.caret.TGGoDownAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoUpAction;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenClefDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenKeySignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTripletFeelDialogAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMenuShownAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseClickAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseExitAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseMoveAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseAllDocumentsAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseCurrentDocumentAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseDocumentAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseDocumentsAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseOtherDocumentsAction;
import org.herac.tuxguitar.app.action.impl.file.TGExitAction;
import org.herac.tuxguitar.app.action.impl.file.TGExportSongAction;
import org.herac.tuxguitar.app.action.impl.file.TGImportSongAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenURLAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGWriteFileAction;
import org.herac.tuxguitar.app.action.impl.help.TGOpenAboutDialogAction;
import org.herac.tuxguitar.app.action.impl.help.TGOpenDocumentationDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetChordDiagramEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetChordNameEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetTablatureEnabledAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoToMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGModifyMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGRemoveMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import org.herac.tuxguitar.app.action.impl.marker.TGUpdateMarkerAction;
import org.herac.tuxguitar.app.action.impl.measure.TGCopyMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureAddDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureCleanDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureRemoveDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGPasteMeasureAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenBeatMoveDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenKeyBindingEditorAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenPluginListDialogAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenSettingsEditorAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadIconsAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadLanguageAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadMidiDevicesAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadStylesAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadTableSettingsAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadTitleAction;
import org.herac.tuxguitar.app.action.impl.system.TGDisposeAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenTransposeDialogAction;
import org.herac.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import org.herac.tuxguitar.app.action.impl.tools.TGToggleBrowserAction;
import org.herac.tuxguitar.app.action.impl.tools.TGTransposeAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoFirstTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoLastTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoNextTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoPreviousTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoToTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGOpenTrackPropertiesDialogAction;
import org.herac.tuxguitar.app.action.impl.track.TGToggleLyricEditorAction;
import org.herac.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportCountDownAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportMetronomeAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportModeAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportSetLoopEHeaderAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportSetLoopSHeaderAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.app.action.impl.view.TGHideExternalBeatAction;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.action.impl.view.TGShowExternalBeatAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMainToolbarAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleViewAction;
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
import org.herac.tuxguitar.editor.action.duration.TGChangeDivisionTypeDurationAction;
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
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import org.herac.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGInsertChordAction;
import org.herac.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import org.herac.tuxguitar.editor.action.note.TGInsertTextAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveChordAction;
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
import org.herac.tuxguitar.editor.action.track.TGSetTrackLyricsAction;
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
		
		installAction(new TGDisposeAction(context));
		
		//file actions
		installAction(new TGLoadSongAction(context));
		installAction(new TGNewSongAction(context));
		installAction(new TGLoadTemplateAction(context));
		installAction(new TGReadSongAction(context));
		installAction(new TGWriteSongAction(context));
		installAction(new TGWriteFileAction(context));
		installAction(new TGSaveAsFileAction(context));
		installAction(new TGSaveFileAction(context));
		installAction(new TGReadURLAction(context));
		installAction(new TGOpenFileAction(context));
		installAction(new TGImportSongAction(context));
		installAction(new TGExportSongAction(context));
		installAction(new TGCloseDocumentsAction(context));
		installAction(new TGCloseDocumentAction(context));
		installAction(new TGCloseCurrentDocumentAction(context));
		installAction(new TGCloseOtherDocumentsAction(context));
		installAction(new TGCloseAllDocumentsAction(context));
		installAction(new TGExitAction(context));
		installAction(new TGPrintAction(context));
		installAction(new TGPrintPreviewAction(context));
		
		//edit actions
		installAction(new TGUndoAction(context));
		installAction(new TGRedoAction(context));
		installAction(new TGSetMouseModeSelectionAction(context));
		installAction(new TGSetMouseModeEditionAction(context));
		installAction(new TGSetNaturalKeyAction(context));
		installAction(new TGSetVoice1Action(context));
		installAction(new TGSetVoice2Action(context));
		
		//tablature actions
		installAction(new TGMouseClickAction(context));
		installAction(new TGMouseMoveAction(context));
		installAction(new TGMouseExitAction(context));
		installAction(new TGMenuShownAction(context));
		
		//caret actions
		installAction(new TGMoveToAction(context));
		installAction(new TGGoRightAction(context));
		installAction(new TGGoLeftAction(context));
		installAction(new TGGoUpAction(context));
		installAction(new TGGoDownAction(context));
		
		//song actions
		installAction(new TGCopySongFromAction(context));
		installAction(new TGClearSongAction(context));
		
		//track actions
		installAction(new TGAddNewTrackAction(context));
		installAction(new TGAddTrackAction(context));
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
		installAction(new TGSetTrackLyricsAction(context));
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
		installAction(new TGCopyMeasureAction(context));
		installAction(new TGPasteMeasureAction(context));
		
		//beat actions
		installAction(new TGChangeNoteAction(context));
		installAction(new TGChangeTiedNoteAction(context));
		installAction(new TGChangeVelocityAction(context));
		installAction(new TGCleanBeatAction(context));
		installAction(new TGDecrementNoteSemitoneAction(context));
		installAction(new TGDeleteNoteAction(context));
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
		installAction(new TGInsertChordAction(context));
		installAction(new TGRemoveChordAction(context));
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
		installAction(new TGChangeDivisionTypeDurationAction(context));
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
		installAction(new TGTransportModeAction(context));
		installAction(new TGTransportSetLoopSHeaderAction(context));
		installAction(new TGTransportSetLoopEHeaderAction(context));
		
		//marker actions
		installAction(new TGUpdateMarkerAction(context));
		installAction(new TGRemoveMarkerAction(context));
		installAction(new TGModifyMarkerAction(context));
		installAction(new TGGoToMarkerAction(context));
		installAction(new TGGoPreviousMarkerAction(context));
		installAction(new TGGoNextMarkerAction(context));
		installAction(new TGGoFirstMarkerAction(context));
		installAction(new TGGoLastMarkerAction(context));
		
		//layout actions
		installAction(new TGSetPageLayoutAction(context));
		installAction(new TGSetLinearLayoutAction(context));
		installAction(new TGSetMultitrackViewAction(context));
		installAction(new TGSetScoreEnabledAction(context));
		installAction(new TGSetTablatureEnabledAction(context));
		installAction(new TGSetCompactViewAction(context));
		installAction(new TGSetChordNameEnabledAction(context));
		installAction(new TGSetChordDiagramEnabledAction(context));
		
		//tools
		installAction(new TGSelectScaleAction(context));
		installAction(new TGTransposeAction(context));
		installAction(new TGShowExternalBeatAction(context));
		installAction(new TGHideExternalBeatAction(context));
		
		//system
		installAction(new TGReloadSettingsAction(context));
		installAction(new TGReloadTitleAction(context));
		installAction(new TGReloadIconsAction(context));
		installAction(new TGReloadLanguageAction(context));
		installAction(new TGReloadMidiDevicesAction(context));
		installAction(new TGReloadStylesAction(context));
		installAction(new TGReloadTableSettingsAction(context));
		
		installAction(new TGOpenSettingsEditorAction(context));
		installAction(new TGOpenKeyBindingEditorAction(context));
		installAction(new TGOpenPluginListDialogAction(context));
		
		//gui actions
		installAction(new TGOpenViewAction(context));
		installAction(new TGToggleViewAction(context));
		installAction(new TGOpenSongInfoDialogAction(context));
		installAction(new TGOpenTempoDialogAction(context));
		installAction(new TGOpenClefDialogAction(context));
		installAction(new TGOpenKeySignatureDialogAction(context));
		installAction(new TGOpenTimeSignatureDialogAction(context));
		installAction(new TGOpenTripletFeelDialogAction(context));
		installAction(new TGOpenStrokeUpDialogAction(context));
		installAction(new TGOpenStrokeDownDialogAction(context));
		installAction(new TGOpenBendDialogAction(context));
		installAction(new TGOpenTrillDialogAction(context));
		installAction(new TGOpenTremoloPickingDialogAction(context));
		installAction(new TGOpenTremoloBarDialogAction(context));
		installAction(new TGOpenHarmonicDialogAction(context));
		installAction(new TGOpenGraceDialogAction(context));
		installAction(new TGOpenBeatMoveDialogAction(context));
		installAction(new TGOpenTextDialogAction(context));
		installAction(new TGOpenChordDialogAction(context));
		installAction(new TGOpenRepeatCloseDialogAction(context));
		installAction(new TGOpenRepeatAlternativeDialogAction(context));
		installAction(new TGOpenMeasureAddDialogAction(context));
		installAction(new TGOpenMeasureCleanDialogAction(context));
		installAction(new TGOpenMeasureRemoveDialogAction(context));
		installAction(new TGOpenMeasureCopyDialogAction(context));
		installAction(new TGOpenMeasurePasteDialogAction(context));
		installAction(new TGOpenTrackPropertiesDialogAction(context));
		installAction(new TGOpenScaleDialogAction(context));
		installAction(new TGOpenURLAction(context));
		installAction(new TGOpenTransportModeDialogAction(context));
		installAction(new TGOpenMarkerEditorAction(context));
		installAction(new TGOpenTransposeDialogAction(context) );
		
		installAction(new TGToggleFretBoardEditorAction(context));
		installAction(new TGTogglePianoEditorAction(context));
		installAction(new TGToggleMatrixEditorAction(context));
		installAction(new TGToggleLyricEditorAction(context));
		installAction(new TGToggleChannelsDialogAction(context));
		installAction(new TGToggleBrowserAction(context));
		installAction(new TGToggleTransportDialogAction(context));
		installAction(new TGToggleMarkerListAction(context));
		installAction(new TGToggleMainToolbarAction(context));
		installAction(new TGToggleEditToolbarAction(context));
		
		installAction(new TGOpenDocumentationDialogAction(context));
		installAction(new TGOpenAboutDialogAction(context));
	}
	
	public void installAction(TGActionBase action) {
		String actionId = action.getName();
		
		TGActionManager.getInstance(this.manager.getContext()).mapAction(actionId, action);
		TGActionConfig config = this.configMap.get(actionId);
		if( config != null ) {
			if( config.isShortcutAvailable() ) {
				this.manager.getKeyBindingActionIds().addActionId(actionId);
			}
			if( config.isDisableOnPlaying() ) {
				this.manager.getDisableOnPlayInterceptor().addActionId(actionId);
			}
			if( config.isStopTransport() ) {
				this.manager.getStopTransportInterceptor().addActionId(actionId);
			}
			if( config.isSyncThread() ) {
				this.manager.getSyncThreadInterceptor().addActionId(actionId);
			}
			if( config.isUnsavedInterceptor()) {
				this.manager.getUnsavedDocumentInterceptor().addActionId(actionId);
			}
			if( config.isDocumentModifier() ) {
				this.manager.getDocumentModifierListener().addActionId(actionId);
			}
			if( config.isLockableAction() ) {
				this.manager.getLockableActionListener().addActionId(actionId);
			}
			
			this.manager.getUpdatableActionListener().getControllers().set(actionId, config.getUpdateController());
			this.manager.getUndoableActionListener().getControllers().set(actionId, config.getUndoableController());
		}
	}
}
