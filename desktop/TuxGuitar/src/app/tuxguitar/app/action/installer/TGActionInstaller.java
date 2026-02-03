package app.tuxguitar.app.action.installer;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.TGActionAdapterManager;
import app.tuxguitar.app.action.impl.caret.TGGoDownAction;
import app.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import app.tuxguitar.app.action.impl.caret.TGGoRightAction;
import app.tuxguitar.app.action.impl.caret.TGGoUpAction;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.action.impl.composition.TGOpenClefDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenKeySignatureDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTripletFeelDialogAction;
import app.tuxguitar.app.action.impl.edit.TGCutAction;
import app.tuxguitar.app.action.impl.edit.TGOpenMeasureErrorsDialogAction;
import app.tuxguitar.app.action.impl.edit.TGCopyAction;
import app.tuxguitar.app.action.impl.edit.TGPasteAction;
import app.tuxguitar.app.action.impl.edit.TGRepeatAction;
import app.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import app.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import app.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import app.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import app.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import app.tuxguitar.app.action.impl.edit.TGToggleFreeEditionModeAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMenuShownAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMouseClickAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMouseExitAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMouseMoveAction;
import app.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import app.tuxguitar.app.action.impl.file.TGCloseAllDocumentsAction;
import app.tuxguitar.app.action.impl.file.TGCloseCurrentDocumentAction;
import app.tuxguitar.app.action.impl.file.TGCloseDocumentAction;
import app.tuxguitar.app.action.impl.file.TGCloseDocumentsAction;
import app.tuxguitar.app.action.impl.file.TGCloseOtherDocumentsAction;
import app.tuxguitar.app.action.impl.file.TGCustomTemplateDeleteAction;
import app.tuxguitar.app.action.impl.file.TGCustomTemplateSelectAction;
import app.tuxguitar.app.action.impl.file.TGExitAction;
import app.tuxguitar.app.action.impl.file.TGExportSongAction;
import app.tuxguitar.app.action.impl.file.TGImportSongAction;
import app.tuxguitar.app.action.impl.file.TGOpenFileAction;
import app.tuxguitar.app.action.impl.file.TGOpenURLAction;
import app.tuxguitar.app.action.impl.file.TGPrintAction;
import app.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import app.tuxguitar.app.action.impl.file.TGReadURLAction;
import app.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import app.tuxguitar.app.action.impl.file.TGSaveFileAction;
import app.tuxguitar.app.action.impl.file.TGWriteFileAction;
import app.tuxguitar.app.action.impl.help.TGHelpGoHomeAction;
import app.tuxguitar.app.action.impl.help.TGOpenAboutDialogAction;
import app.tuxguitar.app.action.impl.help.TGOpenDocumentationDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import app.tuxguitar.app.action.impl.layout.TGSetChordDiagramEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetChordNameEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleResetAction;
import app.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetTablatureEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGToggleContinuousScrollingAction;
import app.tuxguitar.app.action.impl.layout.TGToggleHighlightPlayedBeatAction;
import app.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoToMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGModifyMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import app.tuxguitar.app.action.impl.marker.TGRemoveMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import app.tuxguitar.app.action.impl.marker.TGUpdateMarkerAction;
import app.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureAddDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureCleanDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureRemoveDialogAction;
import app.tuxguitar.app.action.impl.measure.TGToggleLineBreakAction;
import app.tuxguitar.app.action.impl.note.TGOpenBeatMoveDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import app.tuxguitar.app.action.impl.settings.TGOpenKeyBindingEditorAction;
import app.tuxguitar.app.action.impl.selector.*;
import app.tuxguitar.app.action.impl.settings.TGOpenPluginListDialogAction;
import app.tuxguitar.app.action.impl.settings.TGOpenSettingsEditorAction;
import app.tuxguitar.app.action.impl.settings.TGReloadSkinAction;
import app.tuxguitar.app.action.impl.settings.TGReloadLanguageAction;
import app.tuxguitar.app.action.impl.settings.TGReloadMidiDevicesAction;
import app.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import app.tuxguitar.app.action.impl.settings.TGReloadStylesAction;
import app.tuxguitar.app.action.impl.settings.TGReloadTableSettingsAction;
import app.tuxguitar.app.action.impl.settings.TGReloadTitleAction;
import app.tuxguitar.app.action.impl.system.TGDisposeAction;
import app.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import app.tuxguitar.app.action.impl.tools.TGOpenTransposeDialogAction;
import app.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import app.tuxguitar.app.action.impl.tools.TGToggleBrowserAction;
import app.tuxguitar.app.action.impl.tools.TGTransposeAction;
import app.tuxguitar.app.action.impl.track.TGGoFirstTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoLastTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoNextTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoPreviousTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoToTrackAction;
import app.tuxguitar.app.action.impl.track.TGOpenTrackPropertiesDialogAction;
import app.tuxguitar.app.action.impl.track.TGOpenTrackTuningDialogAction;
import app.tuxguitar.app.action.impl.track.TGToggleLyricEditorAction;
import app.tuxguitar.app.action.impl.transport.TGChangeTempoPercentageAction;
import app.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import app.tuxguitar.app.action.impl.transport.TGTransportCountDownAction;
import app.tuxguitar.app.action.impl.transport.TGTransportMetronomeAction;
import app.tuxguitar.app.action.impl.transport.TGTransportModeAction;
import app.tuxguitar.app.action.impl.transport.TGTransportPlayPauseAction;
import app.tuxguitar.app.action.impl.transport.TGTransportPlayStopAction;
import app.tuxguitar.app.action.impl.transport.TGTransportSetLoopEHeaderAction;
import app.tuxguitar.app.action.impl.transport.TGTransportSetLoopSHeaderAction;
import app.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.app.action.impl.view.TGHideExternalBeatAction;
import app.tuxguitar.app.action.impl.view.TGOpenMainToolBarSettingsDialogAction;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.action.impl.view.TGShowExternalBeatAction;
import app.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import app.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleMainToolbarAction;
import app.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import app.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleTableViewerAction;
import app.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleViewAction;
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
import app.tuxguitar.editor.action.duration.TGChangeDivisionTypeDurationAction;
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
import app.tuxguitar.editor.action.note.TGDeleteNoteAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import app.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGInsertChordAction;
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import app.tuxguitar.editor.action.note.TGInsertTextAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import app.tuxguitar.editor.action.note.TGRemoveChordAction;
import app.tuxguitar.editor.action.note.TGRemoveTextAction;
import app.tuxguitar.editor.action.note.TGSetNoteFretNumberAction;
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import app.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import app.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import app.tuxguitar.editor.action.note.TGToggleNoteEnharmonicAction;
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
import app.tuxguitar.editor.action.track.TGSetTrackLyricsAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.track.TGSetTrackNameAction;
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import app.tuxguitar.editor.action.track.TGSetTrackStringCountAction;
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
		installAction(new TGCutAction(context));
		installAction(new TGCopyAction(context));
		installAction(new TGPasteAction(context));
		installAction(new TGRepeatAction(context));
		installAction(new TGUndoAction(context));
		installAction(new TGRedoAction(context));
		installAction(new TGSetMouseModeSelectionAction(context));
		installAction(new TGSetMouseModeEditionAction(context));
		installAction(new TGSetNaturalKeyAction(context));
		installAction(new TGSetVoice1Action(context));
		installAction(new TGSetVoice2Action(context));
		installAction(new TGToggleFreeEditionModeAction(context));
		installAction(new TGOpenMeasureErrorsDialogAction(context));
		installAction(new TGFixMeasureVoiceAction(context));

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

		//selector actions
		installAction(new TGClearSelectionAction(context));
		installAction(new TGExtendSelectionLeftAction(context));
		installAction(new TGExtendSelectionRightAction(context));
		installAction(new TGExtendSelectionPreviousAction(context));
		installAction(new TGExtendSelectionNextAction(context));
		installAction(new TGExtendSelectionFirstAction(context));
		installAction(new TGExtendSelectionLastAction(context));
		installAction(new TGSelectAllAction(context));
		installAction(new TGStartDragSelectionAction(context));
		installAction(new TGUpdateDragSelectionAction(context));

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
		installAction(new TGSetTrackStringCountAction(context));
		installAction(new TGChangeTrackTuningAction(context));
		installAction(new TGCopyTrackFromAction(context));
		installAction(new TGSetTrackLyricsAction(context));
		installAction(new TGChangeTrackPropertiesAction(context));

		//measure actions
		installAction(new TGToggleLineBreakAction(context));
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
		installAction(new TGChangePickStrokeDownAction(context));
		installAction(new TGChangePickStrokeUpAction(context));
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
		installAction(new TGToggleNoteEnharmonicAction(context));

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
		installAction(new TGTransportPlayPauseAction(context));
		installAction(new TGTransportPlayStopAction(context));
		installAction(new TGTransportStopAction(context));
		installAction(new TGTransportMetronomeAction(context));
		installAction(new TGTransportCountDownAction(context));
		installAction(new TGTransportModeAction(context));
		installAction(new TGTransportSetLoopSHeaderAction(context));
		installAction(new TGTransportSetLoopEHeaderAction(context));
		installAction(new TGChangeTempoPercentageAction(context));

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
		installAction(new TGSetLayoutScaleAction(context));
		installAction(new TGSetLayoutScaleIncrementAction(context));
		installAction(new TGSetLayoutScaleDecrementAction(context));
		installAction(new TGSetLayoutScaleResetAction(context));
		installAction(new TGToggleHighlightPlayedBeatAction(context));
		installAction(new TGToggleContinuousScrollingAction(context));

		//tools
		installAction(new TGSelectScaleAction(context));
		installAction(new TGTransposeAction(context));
		installAction(new TGShowExternalBeatAction(context));
		installAction(new TGHideExternalBeatAction(context));

		//system
		installAction(new TGReloadSettingsAction(context));
		installAction(new TGReloadTitleAction(context));
		installAction(new TGReloadSkinAction(context));
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
		installAction(new TGOpenTrackTuningDialogAction(context));
		installAction(new TGOpenTrackPropertiesDialogAction(context));
		installAction(new TGOpenScaleDialogAction(context));
		installAction(new TGOpenURLAction(context));
		installAction(new TGCustomTemplateSelectAction(context));
		installAction(new TGCustomTemplateDeleteAction(context));
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
		installAction(new TGToggleTableViewerAction(context));

		installAction(new TGOpenDocumentationDialogAction(context));
		installAction(new TGOpenAboutDialogAction(context));
		installAction(new TGHelpGoHomeAction(context));
		
		installAction(new TGOpenMainToolBarSettingsDialogAction(context));
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
			if ( config.isInvalidSongInterceptor() ) {
				this.manager.getInvalidSongInterceptor().addActionId(actionId);
			}

			this.manager.getUpdatableActionListener().getControllers().set(actionId, config.getUpdateController());
			this.manager.getUndoableActionListener().getControllers().set(actionId, config.getUndoableController());
		}
	}
}
